package com.nmote.jwti.model

import com.nmote.jwti.gravatarImageURL
import org.hibernate.validator.constraints.Email
import java.io.Serializable

open class BasicSocialAccount : SocialAccount, Serializable {

    constructor()

    constructor(account: SocialAccount) {
        profileEmail = account.profileEmail
        profileName = account.profileName
        profileImageURL = account.profileImageURL
        accountId = account.accountId
        socialService = account.socialService
    }

    @get:Email
    override var profileEmail: String? = null

    override var profileImageURL: String? = null
        get() = field ?: gravatarImageURL(this.profileEmail ?: "info@nmote.com")


    override var accountId: String? = null

    override var profileName: String? = null

    override var socialService: String = "unknown"
}
