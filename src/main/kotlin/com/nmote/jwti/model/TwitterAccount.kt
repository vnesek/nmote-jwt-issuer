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
