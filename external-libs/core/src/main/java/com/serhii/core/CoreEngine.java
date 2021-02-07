package com.serhii.core;

import com.serhii.core.base.Components;
import com.serhii.core.security.Cipher;
import com.serhii.core.security.Hash;
import com.serhii.core.security.impl.hash.HashAlgorithms;
import com.serhii.core.security.impl.crypto.SecureStore;

public class CoreEngine implements Components {

    public static String RUNTIME_LIBRARY = "core";

    private static CoreEngine instance;

    private CoreEngine() {
    }

    public static CoreEngine getInstance() {
        if (instance == null) {
            instance = new CoreEngine();
        }
        return instance;
    }

    @Override
    public void configure(Hash hash) {
        hash.setGenerator(new HashAlgorithms());
    }

    @Override
    public void configure(Cipher cipher) {
        cipher.setSymmetricCrypto(new SecureStore());
    }

}
