package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.model.OAuth2AccessToken

class JwtiOAuth2AccessToken @JsonCreator constructor(
        @JsonProperty("accessToken") accessToken: String?,
        @JsonProperty("tokenType") tokenType: String?,
        @JsonProperty("expiresIn") expiresIn: Int?,
        @JsonProperty("refreshToken") refreshToken: String?,
        @JsonProperty("scope") scope: String?,
        @JsonProperty("rawResponse") rawResponse: String?
) : OAuth2AccessToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse), JwtiAccessToken {

    constructor(t: OAuth2AccessToken) : this(t.accessToken, t.tokenType, t.expiresIn, t.refreshToken, t.scope, t.rawResponse)
}

