package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
class TwitterAccount : SocialAccount, Serializable {

    @JsonProperty("email")
    override val profileEmail: String? = null

    override val socialService: String
        get() = "twitter"

    @JsonProperty("id")
    override val accountId: String? = null

    @JsonProperty("name")
    override val profileName: String? = null

    @JsonProperty("profile_image_url")
    override val profileImageURL: String? = null

    @JsonProperty("screen_name")
    val screenName: String? = null

}
