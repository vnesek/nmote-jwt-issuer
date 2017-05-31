package com.nmote.jwti.repository


import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.model.User

interface UserRepository {

    fun findByEmail(email: String): User?

    fun findById(id: String): User?

    fun findOrCreate(account: SocialAccount<*>): User

    fun save(user: User): User
}
