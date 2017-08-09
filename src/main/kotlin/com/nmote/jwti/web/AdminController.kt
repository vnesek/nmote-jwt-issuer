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

import com.nmote.jwti.model.*
import com.nmote.jwti.repository.UserRepository
import com.nmote.jwti.service.AppNotFoundException
import com.nmote.jwti.service.JwtAuthenticationService
import com.nmote.jwti.service.ScopeService
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

data class ConfigData(val app: AppData, val users: Iterable<UserData>)

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(id: String) : JwtException(id)

@RequestMapping("/api")
@CrossOrigin
@RestController
class AdminController(
        private val users: UserRepository,
        private val apps: AppRepository,
        private val auth: JwtAuthenticationService,
        private val scopes: ScopeService
) {

    @GetMapping("health")
    fun health(): Map<String, Boolean> {
        val usersOk = try {
            users.findOne("ignored")
            true
        } catch (e: Throwable) {
            false
        }
        val appsOk = try {
            apps["ignored"]
            true
        } catch (e: Throwable) {
            false
        }
        return mapOf(
                "users" to usersOk,
                "apps" to appsOk,
                "running" to (usersOk && appsOk)
        )
    }

    @GetMapping("config")
    fun getConfig(): ConfigData {
        auth.hasScope("issuer:admin")
        return ConfigData(getApp().toAppData(), getAll())
    }

    private fun getApp(): App = apps.findByAudience(auth.audience) ?: throw AppNotFoundException(auth.audience)

    @GetMapping("users")
    fun getAll(): List<UserData> {
        auth.hasScope("issuer:admin")
        val app = getApp()
        return users.findAll().map { it.toUserData(app) }
    }

    @Transactional
    @PostMapping("users/merge")
    fun merge(@RequestParam id: List<String>): UserData {
        auth.hasScope("issuer:admin")
        if (id.size < 2) throw Exception("at least two users required for merge")
        val app = getApp()

        val u = id.map(this::getUser)
        val user = u[0]
        val mergees = u.subList(1, u.size)
        mergees.forEach(user::merge)
        users.delete(mergees)
        users.save(user)
        return user.toUserData(app)
    }

    @GetMapping("users/{id}/roles")
    fun getRoles(@PathVariable id: String): Set<String> {
        auth.hasScope("issuer:admin")
        val app = getApp()
        return getUser(id).roles[app.id] ?: emptySet()
    }

    @Transactional
    @PutMapping("users/{id}/roles")
    fun setRoles(@PathVariable id: String, @RequestBody roles: Set<String>): Set<String> {
        auth.hasScope("issuer:admin")
        val app = getApp()
        val user = getUser(id)
        user.roles[app.id] = roles
        users.save(user)
        return roles
    }

    @Transactional
    @DeleteMapping("users/{id}")
    fun delete(@PathVariable id: String): UserData {
        auth.hasScope("issuer:admin")
        val app = getApp()
        val user = getUser(id)
        users.delete(setOf(user))
        return user.toUserData(app)
    }

    private fun getUser(id: String) = users.findOne(id) ?: throw UserNotFoundException(id)

    private fun User.toUserData(app: App) = UserData(
            id = accountId,
            username = username,
            type = socialService,
            name = profileName,
            email = profileEmail,
            image = profileImageURL,
            roles = roles[app.id],
            scope = scopes.scopeFor(this, app),
            accounts = accounts.map { it.toUserData(app) })

    private fun SocialAccount<*>.toUserData(app: App) = UserData(
            id = accountId,
            type = socialService,
            name = profileName,
            email = profileEmail,
            scope = scopes.scopeFor(this, app),
            image = profileImageURL)
}