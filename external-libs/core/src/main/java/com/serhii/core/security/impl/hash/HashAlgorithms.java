package com.serhii.core.security.impl.hash;

import com.serhii.core.CoreEngine;

/**
 *  Class implements specific hash algorithms
 *
 *  Uses underling OpenSSL implementation
 */
public class HashAlgorithms implements HashGenerator {

    @Override
    public String makeHashMD5(String message) {
        return hashMD5(message);
    }

    private native String hashMD5(String message);

    static {
        System.loadLibrary(CoreEngine.RUNTIME_LIBRARY);
    }
}
