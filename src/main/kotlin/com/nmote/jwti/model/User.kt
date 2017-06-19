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
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Email
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.time.Instant
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserData(
        val id: String,
        val type: String,
        val name: String? = null,
        val email: String? = null,
        val image: String? = null,
        val roles: Set<String>? = null,
        val scope: Set<String>? = null,
        val accounts: List<UserData>? = null)


class User : SocialAccount<JwtiAccessToken>, Serializable {

    @Id
    @JsonProperty("id")
    override val accountId: String = UUID.randomUUID().toString()

    @Field("imageURL")
    @JsonProperty("imageURL")
    override val profileImageURL: String? = null
        get() = field ?: firstOrNull(SocialAccount<*>::profileImageURL)

    @Field("name")
    @JsonProperty("name")
    override val profileName: String? = null
        get() = field ?: firstOrNull(SocialAccount<*>::profileName)

    @Transient
    @JsonIgnore
    override val socialService: String = "com.nmote.jwti"

    @JsonIgnore
    override val accessToken: JwtiAccessToken? = null

    @Field("email")
    @JsonProperty("email")
    @get:Email
    override val profileEmail: String? = null
        get() = field ?: firstOrNull(SocialAccount<*>::profileEmail)

    fun <R : Any> firstOrNull(transform: (SocialAccount<*>) -> R?): R?
            = accounts.map(transform).filterNotNull().firstOrNull()

    var password: String = "not-set"

    var roles: MutableMap<String, Set<String>> = mutableMapOf()

    var accounts: List<BasicSocialAccount> = emptyList()

    var createdAt: Instant = Instant.now()

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

    fun merge(user: User) {
        accounts += user.accounts
        val r = roles.toMutableMap()
        for ((app, roles) in user.roles) {
            r.merge(app, roles, { u, v -> u + v })
        }
    }
}