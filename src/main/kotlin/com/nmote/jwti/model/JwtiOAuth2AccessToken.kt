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

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.openid.OpenIdOAuth2AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import org.springframework.data.annotation.PersistenceConstructor

class JwtiOAuth2AccessToken @JsonCreator @PersistenceConstructor constructor(
    @JsonProperty("accessToken") accessToken: String?,
    @JsonProperty("tokenType") tokenType: String?,
    @JsonProperty("expiresIn") expiresIn: Int?,
    @JsonProperty("refreshToken") refreshToken: String?,
    @JsonProperty("scope") scope: String?,
    @JsonProperty("rawResponse") rawResponse: String?,
    @JsonProperty("openIdToken") var openIdToken: String? = null
) : OAuth2AccessToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse), JwtiAccessToken {

    constructor(t: OAuth2AccessToken) : this(t.accessToken, t.tokenType, t.expiresIn, t.refreshToken, t.scope, t.rawResponse)

    constructor(t: OpenIdOAuth2AccessToken) : this(t as OAuth2AccessToken) {
        this.openIdToken = t.openIdToken
    }
}
