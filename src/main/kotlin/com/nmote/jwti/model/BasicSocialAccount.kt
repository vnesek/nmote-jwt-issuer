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

package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.openid.OpenIdOAuth2AccessToken
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.time.Instant
import javax.validation.constraints.Email

private fun jwtiAccessToken(token: Any?) = when (token) {
    null -> null
    is OpenIdOAuth2AccessToken -> JwtiOAuth2AccessToken(token)
    is OAuth2AccessToken -> JwtiOAuth2AccessToken(token)
    is OAuth1AccessToken -> JwtiOAuth1AccessToken(token)
    is JwtiAccessToken -> token
    else -> null
}


@Suppress("LeakingThis", "unused")
open class BasicSocialAccount : SocialAccount<JwtiAccessToken>, Serializable {

    constructor(account: SocialAccount<*>) {
        profileEmail = account.profileEmail
        profileName = account.profileName
        profileImageURL = account.profileImageURL
        accountId = account.accountId
        socialService = account.socialService
        accessToken = jwtiAccessToken(account.accessToken)
    }

    constructor(
        service: String,
        id: String,
        name: String? = null,
        email: String? = null,
        imageUrl: String? = null,
        token: Any? = null
    ) {
        socialService = service
        accountId = id
        profileName = name
        profileEmail = email
        profileImageURL = imageUrl
        accessToken = jwtiAccessToken(token)
    }

    constructor()

    @Field("email")
    @JsonProperty("email")
    @get:Email
    override var profileEmail: String? = null

    @Field("imageURL")
    @JsonProperty("imageURL")
    override var profileImageURL: String? = null

    @Field("id")
    @JsonProperty("id")
    override var accountId: String = "?"

    @Field("name")
    @JsonProperty("name")
    override var profileName: String? = null

    @Field("service")
    @JsonProperty("service")
    override var socialService: String = "?"

    override var accessToken: JwtiAccessToken? = null

    var createdAt: Instant = Instant.now()
}
