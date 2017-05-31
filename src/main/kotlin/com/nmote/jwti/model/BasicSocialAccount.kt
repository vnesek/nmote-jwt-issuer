package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.nmote.jwti.gravatarImageURL
import org.hibernate.validator.constraints.Email
import java.io.Serializable

open class BasicSocialAccount : SocialAccount<JwtiAccessToken>, Serializable {

    constructor()

    constructor(account: SocialAccount<*>) {
        profileEmail = account.profileEmail
        profileName = account.profileName
        profileImageURL = account.profileImageURL
        accountId = account.accountId
        socialService = account.socialService
    }

    @JsonProperty("email")
    @get:Email
    override var profileEmail: String? = null

    @JsonProperty("imageURL")
    override var profileImageURL: String? = null
        get() = field ?: gravatarImageURL(this.profileEmail ?: "info@nmote.com")


    @JsonProperty("id")
    override var accountId: String? = null

    @JsonProperty("name")
    override var profileName: String? = null

    @JsonProperty("service")
    override var socialService: String = "unknown"

    //@JsonDeserialize(`as` = JwtiOAuth2AccessToken::class)
    override var accessToken: JwtiAccessToken? = null
}
