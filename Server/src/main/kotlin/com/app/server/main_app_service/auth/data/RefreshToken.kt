package com.app.server.main_app_service.auth.data

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("refresh_token") // Document name
class RefreshToken (
    val userId: ObjectId,
    @Indexed(expireAfter = "0s") // Deletes entry when the token expires or time has elapsed
    val expiresAt: Instant,
    val createdAt: Instant = Instant.now(),
    val hashedToken: String
)