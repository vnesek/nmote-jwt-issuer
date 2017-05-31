package com.nmote.jwti.model


import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.scribejava.core.model.OAuth2AccessToken
import com.restfb.types.User

class FacebookAccount : User(), SocialAccount<OAuth2AccessToken> {

    @get:JsonIgnore
    override val profileEmail: String?
        get() = email

    @get:JsonIgnore
    override val profileImageURL: String?
        get() = String.format("https://graph.facebook.com/v2.5/%s/picture", id)

    @get:JsonIgnore
    override val profileName: String?
        get() = name

    @get:JsonIgnore
    override val accountId: String
        get() = id

    @get:JsonIgnore
    override val socialService: String
        get() = "facebook"

    @get:JsonIgnore
    override var accessToken: OAuth2AccessToken? = null
}
