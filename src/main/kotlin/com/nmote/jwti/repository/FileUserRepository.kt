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

package com.nmote.jwti.repository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.model.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Repository
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.time.Instant

private val LIST_OF_USERS = object : TypeReference<List<User>>() {}

@ConditionalOnProperty("issuer.repository.file")
@Repository
class FileUserRepository @Autowired constructor(
        @Value("\${issuer.repository.file}") val usersFile: String,
        val mapper: ObjectMapper
) : UserRepository {

    override fun delete(usersToDelete: Iterable<User>) {
        synchronized(this) {
            refresh()
            val toDelete = usersToDelete.map(User::accountId).toSet()
            users.removeIf { toDelete.contains(it.accountId) }
            saveUsers()
        }
    }

    override fun findOne(id: String): User? = synchronized(this) {
        refresh()
        return users.find { it.accountId == id }
    }

    override fun findAll(): Collection<User> = users;

    private val log = LoggerFactory.getLogger(javaClass)

    var lastModified: FileTime = getLastModifiedFileTime()

    var users: MutableList<User> = loadUsers()

    private fun getLastModifiedFileTime() = try {
        Files.getLastModifiedTime(Paths.get(usersFile))
    } catch(ignored: Exception) {
        FileTime.from(Instant.now())
    }

    override fun findBySocialAccount(accountId: String, socialService: String): User? = synchronized(this) {
        refresh()
        return users.filter { it[accountId, socialService] != null }.firstOrNull()
    }

    private operator fun get(account: SocialAccount<*>): User?
            = findBySocialAccount(account.accountId, account.socialService)

    private operator fun get(id: String): User?
            = users.filter { it.accountId == id }.firstOrNull()

    fun refresh() = synchronized(this) {
        if (lastModified.toInstant().isBefore(Instant.now().plusSeconds(15))) {
            try {
                val lm = getLastModifiedFileTime()
                if (lastModified < lm) {
                    users = loadUsers()
                    lastModified = lm
                    // log.debug("Reloaded {} users from {}", users.size, usersFile)
                }
            } catch (ioe: IOException) {
                log.error("Failed to read {}", usersFile, ioe)
            }
        }
    }

    override fun save(user: User): User = synchronized(this) {
        users.remove(user)
        users.add(user)
        saveUsers()
        user
    }

    private fun saveUsers() {
        synchronized(this) {
            try {
                mapper.writeValue(File(usersFile), users)
                lastModified = getLastModifiedFileTime()
                log.debug("Saved {} users to {}", users.size, usersFile)
            } catch (ignored: IOException) {
                log.error("Failed to save {}", usersFile)
            }
        }
    }

    private fun loadUsers(): MutableList<User> {
        var result: MutableList<User>
        try {
            result = mapper.readValue<List<User>>(File(usersFile), LIST_OF_USERS).toMutableList()
            log.info("Loaded {} users from {}", result.size, usersFile)
        } catch (ex: Throwable) {
            log.error("Failed to load {}, will create empty one: {}", usersFile, ex.toString())
            result = mutableListOf<User>()
            mapper.writeValue(File(usersFile), result)

        }
        return result
    }
}
