package com.example.core.security.impl.crypto;

public class Result {

    private String message;
    private byte[] iv;
    private CryptoError error;

    public Result(String message, byte[] iv, CryptoError error) {
        this.message = message;
        this.iv = iv;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getIv() {
        return iv;
    }

    public CryptoError getError() {
        return error;
    }

    public boolean isResultAvailable() {
        return error == CryptoError.OK;
    }

    @Override
    public String toString() {
        return message;
    }
}
