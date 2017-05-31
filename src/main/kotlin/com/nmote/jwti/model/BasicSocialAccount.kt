package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import org.hibernate.validator.constraints.Email
import java.io.Serializable

open class BasicSocialAccount : SocialAccount<JwtiAccessToken>, Serializable {

    constructor()

    constructor(account: SocialAccount<*>) {
        this.source = account

        profileEmail = account.profileEmail
        profileName = account.profileName
        profileImageURL = account.profileImageURL
        accountId = account.accountId
        socialService = account.socialService

        val token = account.accessToken
        accessToken = when (token) {
            is OAuth2AccessToken -> JwtiOAuth2AccessToken(token)
            is OAuth1AccessToken -> JwtiOAuth1AccessToken(token)
            else -> null
        }
    }

    @JsonProperty("email")
    @get:Email
    override var profileEmail: String? = null

    @JsonProperty("imageURL")
    override var profileImageURL: String? = null

    @JsonProperty("id")
    override var accountId: String = "?"

    @JsonProperty("name")
    override var profileName: String? = null

    @JsonProperty("service")
    override var socialService: String = "?"

    override var accessToken: JwtiAccessToken? = null

    var source: Any? = null
}
