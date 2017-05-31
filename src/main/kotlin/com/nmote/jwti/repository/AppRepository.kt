package com.nmote.jwti.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

interface AppRepository {

    val url: String?

    operator fun get(appId: String): App?
}

//@ConditionalOnProperty("issuer.applications")
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "issuer")
class DefaultAppRepository : AppRepository {

    override var url: String? = null

    override fun get(appId: String): App? = applications[appId]

    var applications: MutableMap<String, App> = mutableMapOf()
}


