package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable

interface SocialAccount<T> : Serializable {

    val profileEmail: String?

    val accountId: String?

    val profileImageURL: String?

    val profileName: String?

    val socialService: String

    val socialAccountId: String
        @JsonIgnore
        get() = String.format("%s:%s", socialService, accountId)

    val accessToken: T?
}