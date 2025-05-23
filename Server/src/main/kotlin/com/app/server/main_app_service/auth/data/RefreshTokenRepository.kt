package com.app.server.main_app_service.auth.data

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository : MongoRepository<RefreshToken, ObjectId> {
    fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshToken?
    fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)
}