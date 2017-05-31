package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Email
import java.io.Serializable
import java.util.*

class User : SocialAccount<JwtiAccessToken>, Serializable {

    @JsonProperty("id")
    override val accountId: String = UUID.randomUUID().toString()

    @JsonProperty("imageURL")
    override val profileImageURL: String? = null
        get() = field ?: firstOrNull(SocialAccount<*>::profileImageURL)

    @JsonProperty("name")
    override val profileName: String? = null
        get() = field ?: firstOrNull(SocialAccount<*>::profileName)

    @JsonIgnore
    override val socialService: String = "user"

    @JsonIgnore
    override val accessToken: JwtiAccessToken? = null

    @JsonProperty("email")
    @get:Email
    override val profileEmail: String? = null
        get() = field ?: firstOrNull(SocialAccount<*>::profileEmail)

    fun <R : Any> firstOrNull(transform: (SocialAccount<*>) -> R?): R?
            = accounts.map(transform).filterNotNull().firstOrNull()

    var password: String = "not-set"

    var roles: Map<String, Set<String>> = emptyMap()

    var accounts: List<BasicSocialAccount> = emptyList()

    operator fun get(id: String, service: String): BasicSocialAccount?
            = accounts.find { it.socialService == service && it.accountId == id }

    operator fun get(account: SocialAccount<*>): BasicSocialAccount? = get(account.accountId, account.socialService)

    /**
     * Add new account to user
     */
    operator fun plus(account: BasicSocialAccount): User {
        this.minus(account)
        this.accounts += account
        return this
    }

    /** Remove existing account if it exists */
    operator fun minus(account: BasicSocialAccount): User {
        this[account]?.let { this.accounts -= it }
        return this
    }
}
