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

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.ServiceBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(prefix = "issuer.google", name = ["id", "scope", "secret", "callback"])
@Configuration
class GoogleConfig {

    @Bean
    fun googleOAuthService(
            @Value("\${issuer.google.id}") appId: String,
            @Value("\${issuer.google.scope}") appScope: String,
            @Value("\${issuer.google.secret}") appSecret: String,
            @Value("\${issuer.google.callback}") callbackURI: String
    ) = ServiceBuilder(appId)
            .apiSecret(appSecret)
            .withScope(appScope)
            .callback(callbackURI)
            .build(GoogleApi20.instance())
}
