package com.nmote.jwti.config

import com.github.scribejava.apis.FacebookApi
import com.github.scribejava.core.builder.ServiceBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(prefix = "issuer.facebook", name = arrayOf("id", "scope", "secret", "callback"))
@Configuration
class FacebookConfig {

    @Bean
    fun facebookOAuthService(
            @Value("\${issuer.facebook.id}") appId: String,
            @Value("\${issuer.facebook.scope}") appScope: String,
            @Value("\${issuer.facebook.secret}") appSecret: String,
            @Value("\${issuer.facebook.callback}") callbackURI: String
    ) = ServiceBuilder()
            .apiKey(appId)
            .apiSecret(appSecret)
            .scope(appScope)
            .callback(callbackURI)
            .build(FacebookApi.instance())
}

