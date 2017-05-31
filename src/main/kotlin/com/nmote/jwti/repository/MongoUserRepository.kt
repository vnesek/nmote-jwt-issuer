package com.nmote.jwti.repository

import com.nmote.jwti.model.User
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@ConditionalOnProperty("issuer.repository.mongo")
@Repository
interface MongoUserRepository : MongoRepository<User, String>, UserRepository {

    @Query("{ accounts : { \$elemMatch: { accountId: ?0, socialService: ?1 } } }")
    override fun findBySocialAccount(accountId: String, socialService: String): User?
}
