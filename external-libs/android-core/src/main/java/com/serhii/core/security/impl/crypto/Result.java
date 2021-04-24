package com.serhii.core.security.impl.crypto;

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

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(CryptoError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "";
    }
}
