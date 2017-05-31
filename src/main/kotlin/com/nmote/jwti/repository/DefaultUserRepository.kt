package com.nmote.jwti.repository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.nmote.jwti.model.BasicSocialAccount
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

@ConditionalOnProperty("issuer.users")
@Repository
class DefaultUserRepository @Autowired constructor(
        @Value("\${issuer.users}") val usersFile: String,
        val mapper: ObjectMapper
) : UserRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    var lastModified: FileTime = getLastModifiedFileTime()

    var users: MutableList<User> = loadUsers()

    private fun getLastModifiedFileTime() = try {
        Files.getLastModifiedTime(Paths.get(usersFile))
    } catch(ignored: Exception) {
        FileTime.from(Instant.now())
    }

    override fun findOrCreate(account: SocialAccount<*>): User {
        synchronized(this) {
            refresh()
            val user = (this[account] ?: User())
            user.plus(account as? BasicSocialAccount ?: BasicSocialAccount(account))
            return save(user)
        }
    }

    private operator fun get(account: SocialAccount<*>): User?
            = users.filter { it[account.accountId, account.socialService] != null }.firstOrNull()

    private operator fun get(id: String): User?
            = users.filter { it.accountId == id }.firstOrNull()

    fun refresh() {
        synchronized(this) {
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
    }

    override fun save(user: User): User {
        synchronized(this) {
            try {
                users.remove(user)
                users.add(user)
                mapper.writeValue(File(usersFile), users)
                lastModified = getLastModifiedFileTime()
                log.debug("Saved {} users to {}", users.size, usersFile)
            } catch (ignored: IOException) {
                log.error("Failed to save {}", usersFile)
            }
        }
        return user
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
