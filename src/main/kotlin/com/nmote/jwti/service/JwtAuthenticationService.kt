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

package com.nmote.jwti.service

import com.nmote.jwti.repository.AppRepository
import io.jsonwebtoken.*
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import javax.servlet.http.HttpServletRequest

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class TokenMissingException : JwtException("Need Authorization Bearer in HTTP request")

@ResponseStatus(HttpStatus.FORBIDDEN)
class NotAuthorizedException(scope: String) : JwtException(scope)

@ResponseStatus(HttpStatus.NOT_FOUND)
class AppNotFoundException(audience: String) : JwtException(audience)

interface JwtAuthenticationService {

    val subject: String?

    val scope: List<String>

    val authentication: Jws<Claims>

    val audience: String

    fun hasScope(required: String) {
        if (!scope.contains(required)) throw NotAuthorizedException(required)
    }

    fun hasScope(vararg required: String) {
        val s = scope
        required.forEach { if (!s.contains(it)) throw NotAuthorizedException(it) }
    }
}

@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
class DefaultJwtAuthenticationService(val request: HttpServletRequest, val apps: AppRepository) : JwtAuthenticationService {

    private val parser = Jwts.parser()

    override val subject: String?
        get() = authentication.body.subject

    override val authentication: Jws<Claims>
        get() {
            @Suppress("UNCHECKED_CAST")
            var jws = request.getAttribute(tokenKey) as? Jws<Claims>
            if (jws == null) {
                val token = request.getBearerToken() ?: throw TokenMissingException()
                jws = getJws(token)
                request.setAttribute(tokenKey, jws)
            }
            return jws
        }

    private fun getJws(token: String): Jws<Claims> {
        // Get audience from token
        val jwt = parser.parseClaimsJwtIgnoreSignature(token)
        val audience = jwt.body.audience
        return getJws(token, audience)
    }

    private fun getJws(token: String, audience: String): Jws<Claims> {
        val app = apps.findByAudience(audience) ?: throw AppNotFoundException(audience)
        return app.parser.parseClaimsJws(token)
    }

    @Suppress("UNCHECKED_CAST")
    override val scope: List<String>
        get() = authentication.body["scope"] as? List<String> ?: emptyList()

    override val audience: String
        get() = authentication.body.audience
}

fun JwtParser.parseClaimsJwtIgnoreSignature(token: String): Jwt<Header<*>, Claims> {
    val lastDot = token.lastIndexOf('.')
    val tokenWithoutSignature = token.substring(0, lastDot + 1)
    return parseClaimsJwt(tokenWithoutSignature)
}

private const val tokenKey = "com.nmote.jwti.token"

private fun String.bearerToken(): String? {
    val space = indexOf(' ')
    if (space != -1) {
        if ("bearer".equals(substring(0, space), ignoreCase = true)) {
            return substring(space + 1).trimStart()
        }
    }
    return null
}

private fun HttpServletRequest.getBearerToken(): String? = getHeader("Authorization")?.bearerToken()

