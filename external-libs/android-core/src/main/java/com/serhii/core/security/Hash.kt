/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security

import com.serhii.core.security.impl.hash.HashGenerator
import com.serhii.core.CoreEngine

/**
 * Hash APIs
 */
object Hash {
    fun hashMD5(message: String): String {
        val generator: HashGenerator = CoreEngine.configure(this)
        return generator.makeHashMD5(message)
    }
}