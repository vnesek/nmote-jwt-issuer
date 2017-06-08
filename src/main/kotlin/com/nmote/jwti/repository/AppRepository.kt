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

package com.nmote.jwti.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

interface AppRepository {

    val url: String?

    operator fun get(clientId: String): Pair<App, Client>?
}

//@ConditionalOnProperty(prefix="issuer.applications",)
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "issuer")
class DefaultAppRepository : AppRepository {

    override var url: String? = null

    override fun get(clientId: String): Pair<App, Client>? {
        for (app in applications.values) {
            for (client in app.clients.values) {
                if (client.clientId == clientId) return Pair(app, client)
            }
        }
        return null
    }

    var applications: MutableMap<String, App> = mutableMapOf()
}


