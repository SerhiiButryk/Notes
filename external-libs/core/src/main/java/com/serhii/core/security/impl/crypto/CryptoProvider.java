package com.serhii.core.security.impl.crypto;

/**
 * Interface for provider cryptography operations
 */

public interface CryptoProvider extends CryptoSymmetric {

    void selectKey(String key);

    void createKey(String key, int timeOutSeconds, boolean authRequired);

}
