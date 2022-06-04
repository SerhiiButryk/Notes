/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security

import com.serhii.core.security.impl.hash.HashGenerator
import com.serhii.core.CoreEngine

class Hash {

    private var generator: HashGenerator? = null

    fun setGenerator(generator: HashGenerator?) {
        this.generator = generator
    }

    fun hashMD5(message: String): String {
        return generator?.makeHashMD5(message)
            ?: throw IllegalStateException("Hash was not initialized")
    }

    init {
        CoreEngine.configure(this)
    }

}