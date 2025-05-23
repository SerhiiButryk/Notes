package com.app.server.main_app_service.auth.jwt

import com.app.server.main_app_service.security.Credentials
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        processAuthorizationRequest(request)

        filterChain.doFilter(request, response)
    }

    private fun processAuthorizationRequest(request: HttpServletRequest) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (jwtService.hasToken(authHeader)
            && jwtService.isValidToken(ACCESS_TOKEN, authHeader)) {
            // Store credentials for this user
            val userId = jwtService.getUserIdByToken(token = authHeader)
            Credentials.setAuthenticatedUserId(userId)
        }
    }
}