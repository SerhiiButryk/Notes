package com.example.core.security.impl.crypto;

public interface CryptoSymmetric {

    Result encryptSymmetric(String message);

    String decryptSymmetric(String message, final byte[] inputIV);
}
