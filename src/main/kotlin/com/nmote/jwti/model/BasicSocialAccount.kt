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
import com.github.scribejava.apis.google.GoogleToken
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import org.hibernate.validator.constraints.Email
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.time.Instant

@Suppress("LeakingThis")
open class BasicSocialAccount : SocialAccount<JwtiAccessToken>, Serializable {

    constructor()

    constructor(account: SocialAccount<*>) {
        this.source = account

        profileEmail = account.profileEmail
        profileName = account.profileName
        profileImageURL = account.profileImageURL
        accountId = account.accountId
        socialService = account.socialService

        val token = account.accessToken
        accessToken = when (token) {
            is GoogleToken -> JwtiOAuth2AccessToken(token)
            is OAuth2AccessToken -> JwtiOAuth2AccessToken(token)
            is OAuth1AccessToken -> JwtiOAuth1AccessToken(token)
            else -> null
        }
    }

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

    var source: Any? = null

    var createdAt: Instant = Instant.now()
}
