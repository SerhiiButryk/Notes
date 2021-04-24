package com.serhii.core.base;

import com.serhii.core.security.Cipher;
import com.serhii.core.security.Hash;

/**
 *  Initialization interface for library components
 */

public interface Components {

    void configure(Hash hash);

    void configure(Cipher cipher);

}
