package com.serhii.core.security.impl.crypto;

public interface CryptoSymmetric {

    Result encryptSymmetric(String message, final byte[] inputIV);

    Result decryptSymmetric(String message, final byte[] inputIV);

    Result encryptSymmetricWithKey(String message, String key, final byte[] inputIV);

    Result decryptSymmetricWithKey(String message, String key, final byte[] inputIV);
}
