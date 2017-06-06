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

package com.nmote.jwti.web

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.Duration
import java.time.Instant

@RequestMapping("/oauth")
@Controller
class TokenController(private val tokens: TokenCache) {

    private val parser = Jwts.parser()

    @RequestMapping("token")
    @ResponseBody
    fun token(request: OAuth2Request): Map<String, *> {
        val token = tokens.get(request.code ?: throw Exception("missing code parameter")) ?: throw Exception("token not found")
        val lastDot = token.lastIndexOf('.')
        val jwt: Jwt<*, Claims> = parser.parseClaimsJwt(token.substring(0, lastDot + 1))
        val expiration = Duration.between(Instant.now(), jwt.body.expiration.toInstant())
        val scope = jwt.body["scope"]
        return mapOf(
                "access_token" to token,
                "expires_in" to expiration.seconds,
                "token_type" to "bearer",
                "scope" to scope

        )
    }
}
