package com.serhii.core.security;

import com.serhii.core.CoreEngine;
import com.serhii.core.security.impl.crypto.CryptoProvider;
import com.serhii.core.security.impl.crypto.Result;

public class Cipher {

    private CryptoProvider provider;

    public Cipher() {
        CoreEngine.getInstance().configure(this);
    }

    public void setSymmetricCrypto(CryptoProvider impl) {
        this.provider = impl;
    }

    public Result encryptSymmetric(String message) {
        return provider.encryptSymmetric(message);
    }

    public Result decryptSymmetric(String message, final byte[] inputIV) {
        return provider.decryptSymmetric(message, inputIV);
    }

    public void selectKey(String key) {
        provider.selectKey(key);
    }

    public void createKey(String key, int timeOutSeconds, boolean authRequired) {
        provider.createKey(key, timeOutSeconds, authRequired);
    }

    public void createKey(String key, boolean authRequired) {
        provider.createKey(key, 0, authRequired);
    }

}
