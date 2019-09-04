/*
 *   Copyright 2017. Vjekoslav Nesek
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.nmote.jwti.config

import com.github.scribejava.apis.TwitterApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth10aService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(prefix = "issuer.twitter", name = ["id", "secret", "callback"])
@Configuration
class TwitterConfig {

    @Bean
    fun twitterOAuthService(
        @Value("\${issuer.twitter.id}") appId: String,
        @Value("\${issuer.twitter.secret}") appSecret: String,
        @Value("\${issuer.twitter.callback}") callbackURI: String
    ): OAuth10aService = ServiceBuilder(appId)
        .apiSecret(appSecret)
        .callback(callbackURI)
        .build(TwitterApi.Authenticate.instance())
}



