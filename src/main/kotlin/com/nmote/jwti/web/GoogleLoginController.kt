package com.nmote.jwti.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.nmote.jwti.model.AppRepository
import com.nmote.jwti.model.GoogleAccount
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletResponse

@ConditionalOnBean(name = arrayOf("googleOAuthService"))
@Controller
@RequestMapping("/google")
class GoogleLoginController @Autowired
constructor(
        @Qualifier("googleOAuthService") service: OAuth20Service,
        objectMapper: ObjectMapper,
        users: UserRepository,
        apps: AppRepository
) : OAuthLoginController<OAuth20Service, OAuth2AccessToken>(service, objectMapper, users, apps) {

    @RequestMapping("callback")
    fun callback(
            @RequestParam code: String,
            @CookieValue("app") appId: String,
            response: HttpServletResponse
    ): String {
        val accessToken = service.getAccessToken(code)
        return callback(accessToken, appId, response)
    }

    override fun getSocialAccount(accessToken: OAuth2AccessToken): SocialAccount<OAuth2AccessToken> {
        val request = OAuthRequest(Verb.GET, "https://www.googleapis.com/plus/v1/people/me")
        service.signRequest(accessToken, request)
        val response = service.execute(request)
        val body = response.body
        val account = objectMapper.readValue(body, GoogleAccount::class.java)
        account.accessToken = accessToken
        return account
    }

    override val authorizationUrl: String
        get() = service.getAuthorizationUrl(null)
}