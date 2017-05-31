package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.apis.google.GoogleToken
import com.github.scribejava.core.model.OAuth2AccessToken
import org.springframework.data.annotation.PersistenceConstructor

class JwtiOAuth2AccessToken @JsonCreator @PersistenceConstructor constructor(
        @JsonProperty("accessToken") accessToken: String?,
        @JsonProperty("tokenType") tokenType: String?,
        @JsonProperty("expiresIn") expiresIn: Int?,
        @JsonProperty("refreshToken") refreshToken: String?,
        @JsonProperty("scope") scope: String?,
        @JsonProperty("rawResponse") rawResponse: String?,
        @JsonProperty("openIdToken") var openIdToken: String? = null
) : OAuth2AccessToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse), JwtiAccessToken {

    constructor(t: OAuth2AccessToken) : this(t.accessToken, t.tokenType, t.expiresIn, t.refreshToken, t.scope, t.rawResponse)

    constructor(t: GoogleToken) : this(t as OAuth2AccessToken) {
        openIdToken = t.openIdToken
    }
}

