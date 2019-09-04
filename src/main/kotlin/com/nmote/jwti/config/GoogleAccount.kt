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

package com.nmote.jwti.config

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.openid.OpenIdOAuth2AccessToken
import com.nmote.jwti.model.SocialAccount

class GoogleAccount : SocialAccount<OpenIdOAuth2AccessToken> {

    @JsonProperty("email")
    override var profileEmail: String? = null

    @JsonProperty("sub")
    override var accountId: String = "unknown"

    @JsonProperty("picture")
    override val profileImageURL: String? = null

    @JsonProperty("name")
    override val profileName: String? = null

    override val socialService: String = "google"

    override var accessToken: OpenIdOAuth2AccessToken? = null
}