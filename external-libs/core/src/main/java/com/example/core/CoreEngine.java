package com.example.core;

import com.example.core.base.Components;
import com.example.core.security.Cipher;
import com.example.core.security.Hash;
import com.example.core.security.impl.hash.HashAlgorithms;
import com.example.core.security.impl.crypto.SecureStore;

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
