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

package com.nmote.jwti

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import kotlin.math.abs


private val HOSTS = arrayOf("0", "1", "2", "3")

private val MD5 = ThreadLocal.withInitial { MessageDigest.getInstance("MD5") }

private fun hash(src: String): String {
    val s = src.toLowerCase().trim()
    return try {
        val d = MD5.get().digest(s.toByteArray(StandardCharsets.UTF_8))
        String.format("%032X", BigInteger(1, d))
    } catch (ignored: Exception) {
        "error"
    }
}

fun gravatarImageURL(s: String): String {
    val h = hash(s)
    // Rotate between multiple hosts
    val r = abs(h.hashCode() % HOSTS.size)
    return String.format("https://%s.gravatar.com/avatar/%s", HOSTS[r], h)
}
