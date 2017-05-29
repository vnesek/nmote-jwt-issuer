package com.nmote.jwti.web

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class KeyGenController {

    @RequestMapping("make-key")
    fun makeKey(@RequestParam(defaultValue = "HS512") algorithm: SignatureAlgorithm): String {
        val key = MacProvider.generateKey(algorithm)
        return "$algorithm:${Base64.getEncoder().encodeToString(key.encoded)}"
    }
}
