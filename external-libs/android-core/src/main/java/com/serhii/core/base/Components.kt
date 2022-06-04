/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.base

import com.serhii.core.security.Cipher
import com.serhii.core.security.Hash

/**
 * Initialization interface for library components
 */
internal interface Components {
    fun configure(hash: Hash)
    fun configure(cipher: Cipher)
    fun configure(cipher: Cipher, provider: String) {}
}