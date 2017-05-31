package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.scribejava.core.model.OAuth1AccessToken
import org.springframework.data.annotation.PersistenceConstructor

class JwtiOAuth1AccessToken @JsonCreator @PersistenceConstructor constructor(
        @JsonProperty("token") token: String?,
        @JsonProperty("tokenSecret") tokenSecret: String?,
        @JsonProperty("rawResponse") rawResponse: String?
) : OAuth1AccessToken(token, tokenSecret, rawResponse), JwtiAccessToken {

    constructor(t: OAuth1AccessToken) : this(t.token, t.tokenSecret, t.rawResponse)
}
