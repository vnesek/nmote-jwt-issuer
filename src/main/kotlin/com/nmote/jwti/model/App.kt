package com.nmote.jwti.model

import io.jsonwebtoken.SignatureAlgorithm
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

class App {
    var audience: String? = null
    var success: String = "/login-success"
    var failure: String = "/login-failure"
    var secret: String = "HS512:EcP5P36prfRfYhUrRfwpI01sigj2bU8b2N1d+9hycRfszlfQyVmcmh2QiPNIIfAqEdBGUkEN1qRqzYqSVoKxfQ=="

    val key: Key
        get() {
            val (id, encoded) = secret.split(':')
            val algorithm = SignatureAlgorithm.valueOf(id)
            val decoded = Base64.getDecoder().decode(encoded)
            return SecretKeySpec(decoded, algorithm.jcaName)
        }

    val algorithm: SignatureAlgorithm
        get() {
            val (id, encoded) = secret.split(':')
            return SignatureAlgorithm.valueOf(id)
        }
}
