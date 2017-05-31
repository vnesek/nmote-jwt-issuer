package com.nmote.jwti.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import com.nmote.jwti.model.AppRepository
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.model.TwitterAccount
import com.nmote.jwti.repository.UserRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletResponse

@ConditionalOnBean(name = arrayOf("twitterOAuthService"))
@Controller
@RequestMapping("/twitter")
class TwitterLoginController(
        @Qualifier("twitterOAuthService") service: OAuth10aService,
        objectMapper: ObjectMapper,
        users: UserRepository,
        apps: AppRepository
) : OAuthLoginController<OAuth10aService, OAuth1AccessToken>(service, objectMapper, users, apps) {

    @RequestMapping("callback")
    fun callback(
            @RequestParam oauth_token: String,
            @RequestParam oauth_verifier: String,
            @CookieValue("app") appId: String,
            response: HttpServletResponse
    ): String {
        val requestToken = OAuth1RequestToken(oauth_token, oauth_verifier)
        val accessToken = service.getAccessToken(requestToken, oauth_verifier)
        return callback(accessToken, appId, response)
    }

    override val authorizationUrl: String
        get() {
            val requestToken = service.requestToken
            return service.getAuthorizationUrl(requestToken)
        }

    override fun getSocialAccount(accessToken: OAuth1AccessToken): SocialAccount<OAuth1AccessToken> {
        val request = OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json") //,service);
        service.signRequest(accessToken, request)
        val response = service.execute(request)
        val body = response.body
        val account = objectMapper.readValue(body, TwitterAccount::class.java)
        account.accessToken = accessToken
        return account
    }

}
