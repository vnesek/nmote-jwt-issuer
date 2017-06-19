/*
 *   Copyright 2017. Vjekoslav Nesek
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.nmote.jwti.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.Token
import com.github.scribejava.core.oauth.OAuthService
import com.nmote.jwti.model.AppRepository
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.repository.UserRepository
import com.nmote.jwti.repository.findOrCreate
import com.nmote.jwti.service.ScopeService
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import java.io.IOException
import java.time.Instant
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

abstract class OAuthLoginController<out S : OAuthService<T>, T : Token> protected constructor(
        protected val service: S,
        protected val objectMapper: ObjectMapper,
        protected val users: UserRepository,
        protected val apps: AppRepository,
        protected val tokens: TokenCache,
        protected val scopes: ScopeService
) {

    // TODO Filter scopes based on auth request
    @RequestMapping("login")
    fun login(request: OAuth2Request, response: HttpServletResponse): String {
        val clientId = request.client_id ?: return "redirect:/missing-client-id"
        val (_, client) = apps[clientId] ?: return "redirect:/unknown-application"

        val cookie = Cookie("authState", Base64.getEncoder().encodeToString(objectMapper.writeValueAsBytes(request)))
        cookie.isHttpOnly = true
        cookie.maxAge = -300
        response.addCookie(cookie)

        try {
            val authUrl = authorizationUrl
            log.debug("Authorizing {} via {}", clientId, authUrl)
            return "redirect:" + authUrl
        } catch (ioe: IOException) {
            log.error("Failed to get auth URL", ioe)
            return "redirect:" + client.failure
        }
    }

    protected fun callback(accessToken: T, authState: String, response: HttpServletResponse): String {
        val request = objectMapper.readValue(Base64.getDecoder().decode(authState), OAuth2Request::class.java)
        log.debug("Received access token {} for {}", accessToken, request.client_id)

        val cookie = Cookie("app", "<deleted>")
        cookie.maxAge = 0
        response.addCookie(cookie)

        val clientId = request.client_id ?: return "redirect:/missing-client-id"
        val (app, client) = apps[clientId] ?: return "redirect:/unknown-application"

        val account: SocialAccount<T>
        try {
            account = getSocialAccount(accessToken)
            // log.debug("Personal information {}", account)
        } catch (e: Exception) {
            log.error("Login failed {}", accessToken, e)
            return "redirect:" + client.failure
        }

        val user = users.findOrCreate(account)

        val tokenExpiresIn: Long? = when (accessToken) {
            is OAuth2AccessToken -> accessToken.expiresIn?.toLong()
            is OAuth1AccessToken -> null
            else -> null
        }

        // Determine expires
        val expiresIn = client.expiresIn ?: tokenExpiresIn ?: 6000

        // Determine scope
        val scope = scopes.scopeFor(user, app)

        val name = account.profileName ?: user.profileName
        val email = account.profileEmail ?: user.profileEmail

        val key = app.key
        val jws = Jwts.builder()
                .setAudience(app.audience)
                .setSubject(user.accountId)
                .setIssuedAt(Date())
                .setIssuer(apps.url)
                .claim("email", email)
                .claim("name", name)
                .claim("image", (account.profileImageURL ?: user.profileImageURL)?.removePrefix("http:"))
                .claim("scope", scope)
                .signWith(app.algorithm, key)
                .setExpiration(Date.from(Instant.now().plusSeconds(expiresIn)))
                .compact()

        val code = tokens.put(jws)

        log.debug("Issued access token for {} to {} scope {}", app.id, name, scope)

        var redirectTo: String
        if (!request.redirect_uri.isNullOrBlank()) {
            redirectTo = request.redirect_uri + "?code=$code"
            if (!request.state.isNullOrBlank()) redirectTo += "&state=${request.state}"
        } else {
            redirectTo = client.success
                    .replace("[token]", jws)
                    .replace("[code]", code)
                    .replace("[state]", request.state ?: "")
        }
        log.debug("Redirecting to {}", redirectTo)
        return "redirect:" + redirectTo
    }

    protected abstract val authorizationUrl: String

    protected abstract fun getSocialAccount(accessToken: T): SocialAccount<T>

    protected val log = LoggerFactory.getLogger(javaClass)!!

}
