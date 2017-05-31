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
        account.accessToken = accessToken
        return account
    }

    override val authorizationUrl: String
        get() = service.getAuthorizationUrl(null)


}

