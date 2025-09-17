package com.notes.auth

import com.notes.auth.data.getSalt
import com.notes.auth.data.updateSalt
import com.notes.api.HttpClient
import com.notes.api.NetSettings
import com.notes.api.PlatformAPIs
import com.notes.api.PlatformAPIs.logger
import com.notes.net.inputStreamAsString

class AuthService(private val netSettings: NetSettings, private val httpClient: HttpClient) {

    private val tag = "AuthService"

    suspend fun register(pass: String, confirmPass: String, email: String, callback: (result: AuthResult) -> Unit) {

        logger.logi("$tag register()")

        if (email.isEmpty() || pass.isEmpty() || pass != confirmPass) {
            callback(AuthResult.passwordEmptyOrNotMatching(email))
            return
        }

        val salt = PlatformAPIs.derivedKey.generateSalt()
        updateSalt(salt)

        val passEncoded = PlatformAPIs.derivedKey.generatePDKey(pass, salt)

        val body = """{"email":"$email","password":"$passEncoded"}"""
        val url = netSettings.registerUrl

        httpClient.post(url, body, "application/json") { statusCode, inputStream ->
            // Consume body if any
            inputStreamAsString(inputStream)
            val result: AuthResult
            if (statusCode == 200) {
                result = AuthResult.registrationSuccess(email)
            } else {
                result = AuthResult.registrationFailed(email = email, statusCode = statusCode)
            }
            callback(result)
        }

    }

    suspend fun login(pass: String, email: String, callback: (result: AuthResult) -> Unit) {

        logger.logi("$tag login()")

        if (pass.isEmpty() || email.isEmpty()) {
            callback(AuthResult.emailOrPassEmpty(email))
            return
        }

        val salt = getSalt()
        val passEncoded = PlatformAPIs.derivedKey.generatePDKey(pass, salt)

        val body = """{"email":"$email","password":"$passEncoded"}"""
        val url = netSettings.loginUrl

        httpClient.post(url, body, "application/json") { statusCode, inputStream ->
            val response = inputStreamAsString(inputStream)
            val result: AuthResult
            if (statusCode == 200) {
                val tokens = getTokens(response)
                result = AuthResult.loginSuccess(email, tokens.first, tokens.second)
            } else {
                result = AuthResult.loginFailed(email = email, statusCode = statusCode)
            }
            callback(result)
        }
    }

    suspend fun refreshToken(token: String, callback: (result: AuthResult) -> Unit) {

        logger.logi("$tag refreshToken()")

        val body = """{"refreshToken":"${token}""""
        val url = netSettings.refreshTokenUrl

        httpClient.post(url, body, "application/json") { statusCode, inputStream ->
            val response = inputStreamAsString(inputStream)
            if (statusCode == 200) {
                val tokens = getTokens(response)
                callback(AuthResult.refreshTokenSuccess(tokens.first, tokens.second))
            } else {
                callback(AuthResult.refreshTokenFailed(statusCode = statusCode))
            }
        }

    }

    fun getTokens(input: String): Pair<String, String> {
        // Capture the values of refreshToken and accessToken
        val regex = "\"refreshToken\":\"(.*?)\",\"accessToken\":\"(.*?)\"".toRegex()
        if (regex.containsMatchIn(input)) {
            val matches = regex.findAll(input)
            for (value in matches) {
                if (value.groupValues.size < 3)
                    break
                val refreshToken = value.groupValues[1]
                val accessToken = value.groupValues[2]
                return Pair(refreshToken, accessToken)
            }
        }
        return Pair("", "")
    }

}