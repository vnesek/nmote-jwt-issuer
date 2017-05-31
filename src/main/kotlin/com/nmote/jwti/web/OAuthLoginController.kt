package com.nmote.jwti.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.Token
import com.github.scribejava.core.oauth.OAuthService
import com.nmote.jwti.model.AppRepository
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.repository.UserRepository
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.IOException
import java.time.Instant
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

abstract class OAuthLoginController<out S : OAuthService<T>, T : Token> protected constructor(
        protected val service: S,
        protected val objectMapper: ObjectMapper,
        protected val users: UserRepository,
        protected val apps: AppRepository
) {

    // TODO Filter scopes based on auth request
    @RequestMapping("login")
    fun login(@RequestParam(name = "app", defaultValue = "default") appId: String, response: HttpServletResponse): String {
        val app = apps[appId] ?: return "redirect:/unknown-application"

        val cookie = Cookie("app", appId)
        cookie.isHttpOnly = true
        cookie.maxAge = -300
        response.addCookie(cookie)

        try {
            val authUrl = authorizationUrl
            log.debug("Authorizing {} via {}", appId, authUrl)
            return "redirect:" + authUrl
        } catch (ioe: IOException) {
            log.error("Failed to get auth URL", ioe)
            return "redirect:" + app.failure
        }
    }

    protected fun callback(accessToken: T, appId: String, response: HttpServletResponse): String {
        log.debug("Received access token {} for {}", accessToken, appId)

        val cookie = Cookie("app", "<deleted>")
        cookie.maxAge = 0
        response.addCookie(cookie)

        val app = apps[appId] ?: return "redirect:/unknown-application"

        val account: SocialAccount<T>
        try {
            account = getSocialAccount(accessToken)
            // log.debug("Personal information {}", account)
        } catch (e: Exception) {
            log.error("Login failed {}", accessToken, e)
            return "redirect:" + app.failure
        }

        val user = users.findOrCreate(account)

        val expiresIn = when (accessToken) {
            is OAuth2AccessToken -> accessToken.expiresIn!!
            is OAuth1AccessToken -> 6000
            else -> 6000
        }

        val key = app.key
        val jws = Jwts.builder()
                .setAudience(app.audience)
                .setSubject(user.socialAccountId)
                .claim("email", user.profileEmail)
                .claim("name", user.profileName)
                .claim("image", user.profileImageURL)
                .claim("scope", user.roles)
                .signWith(app.algorithm, key)
                .setExpiration(Date.from(Instant.now().plusSeconds(expiresIn.toLong())))
                .compact()

        val redirectTo = app.success + "#" + jws
        log.debug("Redirecting to {}", redirectTo);
        return "redirect:" + redirectTo
    }

    protected abstract val authorizationUrl: String

    protected abstract fun getSocialAccount(accessToken: T): SocialAccount<T>

    protected val log = LoggerFactory.getLogger(javaClass)!!

}
