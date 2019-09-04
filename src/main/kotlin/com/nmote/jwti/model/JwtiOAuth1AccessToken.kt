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
import com.github.scribejava.core.model.OAuth1AccessToken
import org.springframework.data.annotation.PersistenceConstructor

class JwtiOAuth1AccessToken @JsonCreator @PersistenceConstructor constructor(
    @JsonProperty("token") token: String?,
    @JsonProperty("tokenSecret") tokenSecret: String?,
    @JsonProperty("rawResponse") rawResponse: String?
) : OAuth1AccessToken(token, tokenSecret, rawResponse), JwtiAccessToken {

    constructor(t: OAuth1AccessToken) : this(t.token, t.tokenSecret, t.rawResponse)
}
