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


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.scribejava.core.model.OAuth2AccessToken
import com.restfb.types.User
import org.springframework.data.annotation.Transient

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class FacebookAccount : User(), SocialAccount<OAuth2AccessToken> {

    @get:Transient
    @get:JsonIgnore
    override val profileEmail: String?
        get() = email

    @get:Transient
    @get:JsonIgnore
    override val profileImageURL: String?
        get() = String.format("https://graph.facebook.com/v2.5/%s/picture", id)

    @get:Transient
    @get:JsonIgnore
    override val profileName: String?
        get() = name

    @get:Transient
    @get:JsonIgnore
    override val accountId: String
        get() = id

    @get:Transient
    @get:JsonIgnore
    override val socialService: String
        get() = "facebook"

    @get:Transient
    @get:JsonIgnore
    override var accessToken: OAuth2AccessToken? = null
}
