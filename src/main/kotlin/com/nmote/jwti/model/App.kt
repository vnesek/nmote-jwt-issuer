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

import io.jsonwebtoken.SignatureAlgorithm
import java.security.Key
import java.util.*
import java.util.regex.Pattern
import javax.crypto.spec.SecretKeySpec

class Client {

    var expiresIn: Long? = null

    var clientId: String = ""

    var clientSecret: String = ""

    var success: String = "/login-success"

    var failure: String = "/login-failure"
}

class App {

    lateinit var id: String

    var clients: MutableMap<String, Client> = mutableMapOf()

    var audience: String? = null

    var roles: Map<String, Pattern> = mutableMapOf()

    var secret: String = ""

    val key: Key
        get() {
            val (id, encoded) = secret.split(':', limit = 2)
            val algorithm = SignatureAlgorithm.valueOf(id)
            val decoded = Base64.getDecoder().decode(encoded)
            return SecretKeySpec(decoded, algorithm.jcaName)
        }

    val algorithm: SignatureAlgorithm
        get() = SignatureAlgorithm.valueOf(secret.substringBefore(':'))

    fun rolesFor(email: Iterable<String>) : Set<String> {
        val result = mutableSetOf<String>()
        roles.filterValues { it.matchesAny(email) }.mapTo(result, Map.Entry<String, *>::key)
        return result
    }

}

private fun Pattern.matchesAny(input: Iterable<String>): Boolean {
    val matcher = matcher("")
    return input.find { matcher.reset(it).matches() } != null
}