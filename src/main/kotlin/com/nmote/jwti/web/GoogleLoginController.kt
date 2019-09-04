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
import com.github.scribejava.apis.openid.OpenIdOAuth2AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.nmote.jwti.config.GoogleAccount
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.repository.AppRepository
import com.nmote.jwti.repository.UserRepository
import com.nmote.jwti.service.ScopeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletResponse

@ConditionalOnBean(name = ["googleOAuthService"])
@Controller
@RequestMapping("/google")
class GoogleLoginController @Autowired
constructor(
    @Qualifier("googleOAuthService") service: OAuth20Service,
    objectMapper: ObjectMapper,
    users: UserRepository,
    apps: AppRepository,
    tokens: TokenCache,
    scopes: ScopeService
) : OAuthLoginController<OAuth20Service, OpenIdOAuth2AccessToken>(service, objectMapper, users, apps, tokens, scopes) {

    @RequestMapping("callback")
    fun callback(
        @RequestParam code: String,
        @CookieValue("authState") authState: String,
        response: HttpServletResponse
    ): String {
        val accessToken = service.getAccessToken(code) as OpenIdOAuth2AccessToken
        return callback(accessToken, authState, response)
    }

    override fun getSocialAccount(accessToken: OpenIdOAuth2AccessToken): SocialAccount<*> {
        val request = OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v3/userinfo")
        service.signRequest(accessToken, request)
        val response = service.execute(request)
        val responseBody = response.body
        val account = objectMapper.readValue(responseBody, GoogleAccount::class.java)
        account.accessToken = accessToken
        return account
    }

    override val authorizationUrl: String
        get() = service.authorizationUrl
}
