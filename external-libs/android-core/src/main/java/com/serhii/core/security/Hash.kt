/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security

import com.serhii.core.security.impl.hash.HashGenerator
import com.serhii.core.CoreEngine

class Hash {

    private val generator: HashGenerator = CoreEngine.configure(this)

    fun hashMD5(message: String): String {
        return generator.makeHashMD5(message)
    }

}