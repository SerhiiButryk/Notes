package com.serhii.core.security;

import com.serhii.core.CoreEngine;
import com.serhii.core.security.impl.crypto.CryptoProvider;
import com.serhii.core.security.impl.crypto.Result;

/**
 *  Class provides symmetric encryption functionality.
 *
 *  Note: You choose between OpenSSL or Android implementation.
 */
public class Cipher {

    private CryptoProvider provider;

    public static final String CRYPTO_PROVIDER_OPENSSL = "openssl";
    public static final String CRYPTO_PROVIDER_ANDROID = "android";

    public Cipher() {
        CoreEngine.getInstance().configure(this);
    }

    public Cipher(String provider) {
        selectProvider(provider);
    }

    public void setSymmetricCrypto(CryptoProvider impl) {
        this.provider = impl;
    }

    public Result encryptSymmetric(String message) {
        return provider.encryptSymmetric(message, null);
    }

    public Result decryptSymmetric(String message, final byte[] inputIV) {
        return provider.decryptSymmetric(message, inputIV);
    }

    public Result encryptSymmetricWithKey(String message, String key) {
        return provider.encryptSymmetricWithKey(message, key, null);
    }

    public Result encryptSymmetricWithKey(String message, String key, final byte[] inputIV) {
        return provider.encryptSymmetricWithKey(message, key, inputIV);
    }

    public Result decryptSymmetricWithKey(String message, String key, final byte[] inputIV) {
        return provider.decryptSymmetricWithKey(message, key, inputIV);
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

    public void selectProvider(String provider) {
        CoreEngine.configureCipher(this, provider);
    }

}
