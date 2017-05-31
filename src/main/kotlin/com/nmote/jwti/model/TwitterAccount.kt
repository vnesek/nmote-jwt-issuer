package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.model.OAuth1AccessToken
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class TwitterAccount : SocialAccount<OAuth1AccessToken>, Serializable {

    @get:JsonIgnore
    override var accessToken: OAuth1AccessToken? = null

    @JsonProperty("email")
    override val profileEmail: String? = null

    @get:JsonIgnore
    override val socialService: String
        get() = "twitter"

    @JsonProperty("id")
    override val accountId: String = "?"

    @JsonProperty("name")
    override val profileName: String? = null

    @JsonProperty("profile_image_url")
    override val profileImageURL: String? = null

    @JsonProperty("screen_name")
    val screenName: String? = null

}
