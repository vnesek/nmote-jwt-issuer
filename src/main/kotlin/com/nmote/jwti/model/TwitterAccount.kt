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
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.model.OAuth1AccessToken
import org.springframework.data.annotation.Transient
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class TwitterAccount : SocialAccount<OAuth1AccessToken>, Serializable {

    @get:Transient
    @get:JsonIgnore
    override var accessToken: OAuth1AccessToken? = null

    @get:Transient
    @JsonProperty("email")
    override val profileEmail: String? = null

    @get:Transient
    @get:JsonIgnore
    override val socialService: String
        get() = "twitter"

    @get:Transient
    @JsonProperty("id")
    override val accountId: String = "?"

    @get:Transient
    @JsonProperty("name")
    override val profileName: String? = null

    @get:Transient
    @JsonProperty("profile_image_url")
    override val profileImageURL: String? = null

    @JsonProperty("screen_name")
    val screenName: String? = null

}
