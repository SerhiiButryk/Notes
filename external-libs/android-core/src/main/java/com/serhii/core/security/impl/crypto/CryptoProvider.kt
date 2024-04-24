/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Interface for provider of cryptography operations
 */
internal interface CryptoProvider {
    fun encrypt(message: String, key: String = "", inputIV: String = ""): Result
    fun decrypt(message: String, key: String = "", inputIV: String = ""): Result
    fun getRandomValue(size: Int) = ByteArray(0)

    // Should be overridden in derived class
    fun selectKey(key: String): Unit = throw RuntimeException("Illegal operation with the provider")
    // Should be overridden in derived class
    fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean): Unit = throw RuntimeException("Illegal operation with the provider")
    // Should be overridden in derived class
    fun type() = ""
    fun genDerivedKey(input: String) = ByteArray(0)
}

/**
 * Base class for provider of cryptography operations
 */
internal abstract class BaseProvider : CryptoProvider {

    // TODO: Consider replacement with OpenSSL APIs
    override fun getRandomValue(size: Int): ByteArray {
        val byteArray = ByteArray(size)
        SecureRandom.getInstance("SHA1PRNG").nextBytes(byteArray)
        return byteArray
    }

    // TODO: Consider other solutions here
    override fun genDerivedKey(input: String): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray())
    }

}