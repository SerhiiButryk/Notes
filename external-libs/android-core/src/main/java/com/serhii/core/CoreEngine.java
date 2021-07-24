package com.serhii.core;

import com.serhii.core.base.Components;
import com.serhii.core.security.Cipher;
import com.serhii.core.security.Hash;
import com.serhii.core.security.impl.crypto.CryptoOpenssl;
import com.serhii.core.security.impl.hash.HashAlgorithms;
import com.serhii.core.security.impl.crypto.SecureStore;

import static com.serhii.core.security.Cipher.CRYPTO_PROVIDER_ANDROID;
import static com.serhii.core.security.Cipher.CRYPTO_PROVIDER_OPENSSL;

public class CoreEngine implements Components {

    private static String RUNTIME_LIBRARY = "core";

    private static CoreEngine instance;

    private CoreEngine() {
    }

    public static CoreEngine getInstance() {
        if (instance == null) {
            synchronized (CoreEngine.class) {
                if (instance == null) {
                    instance = new CoreEngine();
                }
            }
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

    public static void configureCipher(Cipher cipher, String provider) {
        if (provider.equals(CRYPTO_PROVIDER_ANDROID)) {
            cipher.setSymmetricCrypto(new SecureStore());
        } else if (provider.equals(CRYPTO_PROVIDER_OPENSSL)) {
            cipher.setSymmetricCrypto(new CryptoOpenssl());
        } else {
            throw new IllegalArgumentException("Unknown crypto provider is passed");
        }
    }

    public static void loadNativeLibrary() { System.loadLibrary(CoreEngine.RUNTIME_LIBRARY); }

}
