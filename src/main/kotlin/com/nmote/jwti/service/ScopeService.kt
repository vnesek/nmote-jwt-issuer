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

import com.nmote.jwti.model.App
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.model.User
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


interface ScopeService {

    fun scopeFor(account: SocialAccount<*>, app: App, requestedScopes: Set<String> = emptySet()): Set<String>
}

/**
 * Determine scope for a given user and app
 */
@Service
class DefaultScopeService : ScopeService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun scopeFor(account: SocialAccount<*>, app: App, requestedScopes: Set<String>): Set<String> {
        val scope = mutableSetOf<String>()
        if (account is User) {
            val appRoles = account.roles[app.id]
            val emails = account.accounts.mapNotNull(SocialAccount<*>::profileEmail)
            val implicitRoles = app.rolesFor(emails)
            log.debug("Roles for {} => {}. Implicit roles for {} => {}", app.id, appRoles, emails, implicitRoles)
            if (appRoles != null) scope += appRoles
            scope += implicitRoles
        } else {
            val email = account.profileEmail
            if (email != null) scope += app.rolesFor(setOf(email))
        }
        if (requestedScopes.isNotEmpty()) {
            scope.retainAll(requestedScopes)
        }
        return scope
    }
}