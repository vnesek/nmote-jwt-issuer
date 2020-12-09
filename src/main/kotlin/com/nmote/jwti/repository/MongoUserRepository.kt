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

import com.mongodb.client.MongoClient
import com.nmote.jwti.model.User
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@ConditionalOnProperty(value = ["issuer.repository.mongo"])
@ConditionalOnBean(MongoClient::class)
@Repository
interface MongoUserRepository : MongoRepository<User, String>, UserRepository {

    @Query("{ accounts : { \$elemMatch: { accountId: ?0, socialService: ?1 } } }")
    override fun findBySocialAccount(accountId: String, socialService: String): Optional<User>
}
