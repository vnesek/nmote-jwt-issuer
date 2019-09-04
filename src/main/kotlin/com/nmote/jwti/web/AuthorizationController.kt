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

import com.nmote.jwti.model.*
import com.nmote.jwti.repository.AppRepository
import com.nmote.jwti.repository.UserRepository
import com.nmote.jwti.service.ScopeService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
@RequestMapping("oauth/token")
class AuthorizationController(
    val users: UserRepository,
    val apps: AppRepository,
    val scopes: ScopeService
) {

    fun String.basicAuthorization(): Pair<String, String> {
        val (type, encoded) = split(' ')
        if (!type.equals("basic", true)) throw IllegalArgumentException("expected basic authorization: $type")
        val decoded = String(Base64.getDecoder().decode(encoded))
        val (username, password) = decoded.split(':', limit = 2)
        return Pair(username, password)
    }

    fun authorizeClient(request: OAuth2Request, authorization: String?): Pair<App, Client> {
        authorization?.basicAuthorization()?.let { request.client_id = it.first; request.client_secret = it.second }
        val clientId = request.client_id ?: throw OAuthException(OAuthError.invalid_request)
        val (app, client) = apps[clientId] ?: throw OAuthException(OAuthError.invalid_request)
        if (client.clientSecret != request.client_secret) throw OAuthException(OAuthError.invalid_grant)
        return Pair(app, client)
    }

    @RequestMapping(params = ["grant_type=password"])
    @ResponseBody
    fun resourceOwnerPasswordCredentials(request: OAuth2Request, @RequestHeader(value = "Authorization", required = false) authorization: String?): Map<String, *> {
        val (app, client) = authorizeClient(request, authorization)
        val username = request.username ?: throw OAuthException(OAuthError.invalid_request)
        val user = users.findByUsername(username).orElseThrow { OAuthException(OAuthError.invalid_grant) }
        if (user.password != request.password) throw OAuthException(OAuthError.invalid_grant)

        // Determine expires
        val expiresIn = client.expiresIn ?: 6000

        // Determine scope
        val scope = scopes.scopeFor(user, app)

        val jws = issueToken(
            user,
            app,
            scope,
            apps.url,
            expiresIn = expiresIn)

        return mapOf(
            "access_token" to jws,
            "expires_in" to expiresIn,
            "token_type" to "bearer",
            "scope" to scope
        )
    }

    @RequestMapping(params = ["grant_type=client_credentials"])
    @ResponseBody
    fun clientCredentials(request: OAuth2Request, @RequestHeader(value = "Authorization", required = false) authorization: String?): Map<String, *> {
        val (app, client) = authorizeClient(request, authorization)

        // Determine expires
        val expiresIn = client.expiresIn ?: 6000

        // Determine scope
        val scope = client.roles

        val user = User()
        user.username = client.clientId

        val jws = issueToken(
            user,
            app,
            scope,
            apps.url,
            expiresIn = expiresIn)

        return mapOf(
            "access_token" to jws,
            "expires_in" to expiresIn,
            "token_type" to "bearer",
            "scope" to scope
        )
    }
}