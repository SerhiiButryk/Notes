package com.app.server.main_app_service.auth.data

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class User(
    val email: String,
    val passwordHash: String,
    @Id val id: ObjectId = ObjectId()
)