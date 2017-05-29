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

@ConditionalOnProperty("issuer.users")
@Repository
class DefaultUserRepository @Autowired constructor(
        @Value("\${issuer.users}") val usersFile: String,
        val mapper: ObjectMapper
) : UserRepository {

    private fun getLastModifiedFileTime() = Files.getLastModifiedTime(Paths.get(usersFile))

    var lastModified: FileTime = getLastModifiedFileTime()

    var users: MutableMap<String, User> = loadUsers()

    override fun findByEmail(email: String): User? {
        synchronized(this) {
            refreshUsers()
            return users[email]
        }
    }

    override fun findById(id: String): User? {
        synchronized(this) {
            refreshUsers()
            return users[id]
        }
    }

    override fun findOrCreate(account: SocialAccount): User {
        synchronized(this) {
            val id = account.socialAccountId
            val user = users[id] ?: User(account)

            if (account.profileEmail != null) user.profileEmail = account.profileEmail
            if (account.profileImageURL != null) user.profileImageURL = account.profileImageURL
            if (account.profileName != null) user.profileName = account.profileName

            return save(user)
        }
    }

    fun refreshUsers() {
        synchronized(this) {
            if (lastModified.toInstant().isBefore(Instant.now().plusSeconds(15))) {
                try {
                    val lm = getLastModifiedFileTime()
                    if (lastModified < lm) {
                        users = loadUsers()
                        lastModified = lm
                        log.debug("Reloaded {} users from {}", users.size, usersFile)
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
                val existing = users[user.socialAccountId]
                if (user !== existing) {
                    users[user.socialAccountId] = user
                    mapper.writeValue(File(usersFile), users.values)
                    this.lastModified = getLastModifiedFileTime()
                    log.debug("Saved {}", usersFile)
                }
            } catch (ignored: IOException) {
                log.error("Failed to save {}", usersFile)
            }
        }
        return user
    }

    private fun loadUsers(): MutableMap<String, User> {
        val users = mutableMapOf<String, User>()
        mapper.readValue<List<User>>(File(usersFile), LIST_OF_USERS)
                .associateByTo(users, User::socialAccountId)
        return users
    }

    private val log = LoggerFactory.getLogger(javaClass)
}
