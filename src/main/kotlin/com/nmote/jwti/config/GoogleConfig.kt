package com.nmote.jwti.config

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(prefix = "issuer.google", name = arrayOf("id", "scope", "secret", "callback"))
@Configuration
class GoogleConfig {

    @Bean
    fun googleOAuthService(
            @Value("\${issuer.google.id}") appId: String,
            @Value("\${issuer.google.scope}") appScope: String,
            @Value("\${issuer.google.secret}") appSecret: String,
            @Value("\${issuer.google.callback}") callbackURI: String
    ) = ServiceBuilder()
            .apiKey(appId)
            .apiSecret(appSecret)
            .scope(appScope)
            .callback(callbackURI)
            .build(GoogleApi20.instance())
}
