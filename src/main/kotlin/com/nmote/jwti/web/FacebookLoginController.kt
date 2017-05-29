package com.nmote.jwti.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.oauth.OAuth20Service
import com.nmote.jwti.model.AppRepository
import com.nmote.jwti.model.FacebookAccount
import com.nmote.jwti.repository.UserRepository
import com.restfb.DefaultFacebookClient
import com.restfb.Parameter
import com.restfb.Version
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletResponse

@ConditionalOnBean(name = arrayOf("facebookOAuthService"))
@Controller
@RequestMapping("/facebook")
class FacebookLoginController(
        @Qualifier("facebookOAuthService") service: OAuth20Service,
        objectMapper: ObjectMapper,
        users: UserRepository,
        apps: AppRepository,
        @Value("\${issuer.facebook.secret}") val appSecret: String
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

    override fun getSocialAccount(accessToken: OAuth2AccessToken): FacebookAccount {
        val facebookClient = DefaultFacebookClient(accessToken.accessToken, appSecret, Version.VERSION_2_6)

        // Fetch user info
        val fields = "id,bio,birthday,email,first_name,last_name,gender,hometown,interested_in,location,middle_name,name,website"
        val account = facebookClient.fetchObject("me", FacebookAccount::class.java, //
                Parameter.with("fields", fields))
        return account
    }

    override val authorizationUrl: String
        get() = service.getAuthorizationUrl(null)


}

