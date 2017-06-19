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
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import com.nmote.jwti.model.AppRepository
import com.nmote.jwti.model.GoogleAccount
import com.nmote.jwti.model.SocialAccount
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

@ConditionalOnBean(name = arrayOf("googleOAuthService"))
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
) : OAuthLoginController<OAuth20Service, OAuth2AccessToken>(service, objectMapper, users, apps, tokens, scopes) {

    @RequestMapping("callback")
    fun callback(
            @RequestParam code: String,
            @CookieValue("authState") authState: String,
            response: HttpServletResponse
    ): String {
        val accessToken = service.getAccessToken(code)
        return callback(accessToken, authState, response)
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