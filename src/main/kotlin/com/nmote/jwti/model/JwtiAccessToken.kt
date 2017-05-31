package com.nmote.jwti.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        JsonSubTypes.Type(value = JwtiOAuth1AccessToken::class, name = "oauth1"),
        JsonSubTypes.Type(value = JwtiOAuth2AccessToken::class, name = "oauth2")
)
interface JwtiAccessToken
