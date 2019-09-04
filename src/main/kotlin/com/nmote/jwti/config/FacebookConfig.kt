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

import com.github.scribejava.apis.FacebookApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth20Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConditionalOnProperty(prefix = "issuer.facebook", name = ["id", "scope", "secret", "callback"])
@Configuration
class FacebookConfig {

    @Bean
    fun facebookOAuthService(
        @Value("\${issuer.facebook.id}") appId: String,
        @Value("\${issuer.facebook.scope}") appScope: String,
        @Value("\${issuer.facebook.secret}") appSecret: String,
        @Value("\${issuer.facebook.callback}") callbackURI: String
    ): OAuth20Service = ServiceBuilder(appId)
        .apiSecret(appSecret)
        .withScope(appScope)
        .callback(callbackURI)
        .build(FacebookApi.instance())
}

