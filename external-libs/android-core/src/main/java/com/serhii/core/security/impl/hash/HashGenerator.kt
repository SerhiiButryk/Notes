/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.hash

interface HashGenerator {
    fun makeHashMD5(message: String): String
}