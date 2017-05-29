package com.nmote.jwti.config

import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(prefix = "issuer.twitter", name = arrayOf("id", "secret", "callback"))
@Configuration
class TwitterConfig {

    @Bean
    fun twitterOAuthService(
            @Value("\${issuer.twitter.id}") appId: String,
            @Value("\${issuer.twitter.secret}") appSecret: String,
            @Value("\${issuer.twitter.callback}") callbackURI: String
    ) = ServiceBuilder()
            .apiKey(appId)
            .apiSecret(appSecret)
            .callback(callbackURI)
            .build(TwitterApi.Authenticate.instance())
}



