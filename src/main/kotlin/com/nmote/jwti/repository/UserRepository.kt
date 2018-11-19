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


import com.nmote.jwti.model.BasicSocialAccount
import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.model.User
import java.util.*

interface UserRepository {

    fun deleteAll(user: Iterable<User>)

    fun findById(id : String): Optional<User>

    fun save(user: User): User

    fun findBySocialAccount(accountId: String, socialService: String): Optional<User>

    fun findByUsername(username: String): Optional<User>

    fun findAll(): Collection<User>
}

fun UserRepository.findOrCreate(account: SocialAccount<*>): User {
    val user = (findBySocialAccount(account.accountId, account.socialService)).orElse(User())
    user.plus(account as? BasicSocialAccount ?: BasicSocialAccount(account))
    return save(user)
}

