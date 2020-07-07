package com.example.core.common.components;

import com.example.core.security.Cipher;
import com.example.core.security.Hash;

/**
 *  Initialization interface for library components
 */

public interface Components {

    void configure(Hash hash);

    void configure(Cipher cipher);

}
