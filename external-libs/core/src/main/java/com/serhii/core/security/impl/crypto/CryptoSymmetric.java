package com.serhii.core.security.impl.crypto;

public interface CryptoSymmetric {

    Result encryptSymmetric(String message);

    Result decryptSymmetric(String message, final byte[] inputIV);
}
