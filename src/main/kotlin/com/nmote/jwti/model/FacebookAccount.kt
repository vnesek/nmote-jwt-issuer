package com.nmote.jwti.model


import com.github.scribejava.core.model.OAuth2AccessToken
import com.restfb.types.User

class FacebookAccount : User(), SocialAccount<OAuth2AccessToken> {

    override val profileEmail: String?
        get() = email

    override val profileImageURL: String?
        get() = String.format("https://graph.facebook.com/v2.5/%s/picture", id)

    override val profileName: String?
        get() = name

    override val accountId: String
        get() = id

    override val socialService: String
        get() = "facebook"

    //var accessToken: String? = null

    override var accessToken: OAuth2AccessToken? = null

}
