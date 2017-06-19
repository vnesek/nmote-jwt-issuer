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

import com.fasterxml.jackson.annotation.JsonInclude
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.Key
import java.util.*
import java.util.regex.Pattern
import javax.crypto.spec.SecretKeySpec

class Client {

    var expiresIn: Long? = null

    lateinit var clientId: String

    lateinit var clientSecret: String

    var success: String = "/login-success"

    var failure: String = "/login-failure"
}


@JsonInclude(JsonInclude.Include.NON_NULL)
data class AppData(
        val id: String,
        val audience: String?,
        val roles: Set<String>)

class App {

    lateinit var id: String

    var clients: MutableMap<String, Client> = mutableMapOf()

    lateinit var audience: String

    var roleForEmail: Map<String, Pattern> = mutableMapOf()

    lateinit var secret: String

    val roles: Set<String> get() = roleForEmail.keys

    val key: Key by lazy {
        val (id, encoded) = secret.split(':', limit = 2)
        val algorithm = SignatureAlgorithm.valueOf(id)
        val decoded = Base64.getDecoder().decode(encoded)
        SecretKeySpec(decoded, algorithm.jcaName)
    }

    val algorithm: SignatureAlgorithm by lazy {
        SignatureAlgorithm.valueOf(secret.substringBefore(':'))
    }

    fun rolesFor(email: Iterable<String>): Set<String> {
        val result = mutableSetOf<String>()
        roleForEmail.filterValues { it.matchesAny(email) }.mapTo(result, Map.Entry<String, *>::key)
        return result
    }

    val parser: JwtParser by lazy { Jwts.parser().setSigningKey(key) }

    fun toAppData() = AppData(id, audience, roles)
}

private fun Pattern.matchesAny(input: Iterable<String>): Boolean {
    val matcher = matcher("")
    return input.find { matcher.reset(it).matches() } != null
}