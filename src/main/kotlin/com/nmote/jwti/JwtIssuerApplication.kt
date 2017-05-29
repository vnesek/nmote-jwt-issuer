package com.nmote.jwti

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class JwtIssuerApplication

fun main(args: Array<String>) {
    SpringApplication.run(JwtIssuerApplication::class.java, *args)
}
