package com.nmote.jwti.repository


import com.nmote.jwti.model.BasicSocialAccount
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.model.User

interface UserRepository {

    fun save(user: User): User

    fun findBySocialAccount(accountId: String, socialService: String): User?
}

fun UserRepository.findOrCreate(account: SocialAccount<*>): User {
    val user = (findBySocialAccount(account.accountId, account.socialService) ?: User())
    user.plus(account as? BasicSocialAccount ?: BasicSocialAccount(account))
    return save(user)
}

