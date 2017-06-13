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

import com.nmote.jwti.model.SocialAccount
import com.nmote.jwti.model.UserData
import com.nmote.jwti.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RequestMapping("/users")
@CrossOrigin
@RestController
class UserAdminController(val users: UserRepository) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun all() = users.findAll().map(SocialAccount<*>::toUserData)

    @Transactional
    @RequestMapping(value = "merge", method = arrayOf(RequestMethod.POST))
    fun merge(@RequestParam id: List<String>): UserData {
        if (id.size < 2) throw Exception("at least two users required for merge")
        val u = id.map { users.findOne(it) ?: throw Exception("not found " + it) }
        val user = u[0]
        val mergees = u.subList(1, u.size)
        mergees.forEach(user::merge)
        users.delete(mergees)
        users.save(user)
        return user.toUserData()
    }
}