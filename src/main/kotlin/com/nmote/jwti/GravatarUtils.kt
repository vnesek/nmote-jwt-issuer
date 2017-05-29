package com.nmote.jwti

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

private val HOSTS = arrayOf("0", "1", "2", "3")

private val MD5 = ThreadLocal.withInitial({ MessageDigest.getInstance("MD5") })

private fun hash(src: String): String {
    val s = src.toLowerCase().trim()
    try {
        return DatatypeConverter.printHexBinary(MD5.get().digest(s.toByteArray(StandardCharsets.UTF_8)))
    } catch (ignored: Exception) {
        return "error"
    }
}

fun gravatarImageURL(s: String): String {
    val h = hash(s)
    // Rotate between multiple hosts
    val r = Math.abs(h.hashCode() % HOSTS.size)
    return String.format("https://%s.gravatar.com/avatar/%s", HOSTS[r], h)
}

