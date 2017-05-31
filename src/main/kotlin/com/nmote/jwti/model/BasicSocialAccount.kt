package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.google.GoogleToken
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth2AccessToken
import org.hibernate.validator.constraints.Email
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.time.Instant

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
            is GoogleToken -> JwtiOAuth2AccessToken(token)
            is OAuth2AccessToken -> JwtiOAuth2AccessToken(token)
            is OAuth1AccessToken -> JwtiOAuth1AccessToken(token)
            else -> null
        }
    }

    @Field("email")
    @JsonProperty("email")
    @get:Email
    override var profileEmail: String? = null

    @Field("imageURL")
    @JsonProperty("imageURL")
    override var profileImageURL: String? = null

    @Field("id")
    @JsonProperty("id")
    override var accountId: String = "?"

    @Field("name")
    @JsonProperty("name")
    override var profileName: String? = null

    @Field("service")
    @JsonProperty("service")
    override var socialService: String = "?"

    override var accessToken: JwtiAccessToken? = null

    var source: Any? = null

    var createdAt: Instant = Instant.now()
}
