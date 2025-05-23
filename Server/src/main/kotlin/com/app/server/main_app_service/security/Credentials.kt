package com.app.server.main_app_service.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

object Credentials {

    fun getAuthenticatedUserId(): String {
        return SecurityContextHolder.getContext().authentication.principal as String
    }

    fun setAuthenticatedUserId(userId: String) {
        val token = UsernamePasswordAuthenticationToken(userId, null, emptyList())
        SecurityContextHolder.getContext().authentication = token
    }

}