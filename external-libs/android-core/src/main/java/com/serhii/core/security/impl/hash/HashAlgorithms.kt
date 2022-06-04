/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.hash

import com.serhii.core.CoreEngine.loadNativeLibrary
import com.serhii.core.security.impl.hash.HashGenerator
import com.serhii.core.CoreEngine

/**
 * Class implements specific hash algorithms
 * Uses underling OpenSSL implementation
 */
internal class HashAlgorithms : HashGenerator {

    override fun makeHashMD5(message: String): String {
        return hashMD5(message)
    }

    private external fun hashMD5(message: String): String

    companion object {
        init {
            loadNativeLibrary()
        }
    }

}