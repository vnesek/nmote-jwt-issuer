package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
interface SocialAccount<T> : Serializable {

    val profileEmail: String?

    val accountId: String

    val profileImageURL: String?

    val profileName: String?

    val socialService: String

    val accessToken: T?
}