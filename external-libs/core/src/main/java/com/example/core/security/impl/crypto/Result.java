package com.example.core.security.impl.crypto;

public class Result {

    private String message;
    private byte[] iv;

    public Result(String message, byte[] iv) {
        this.message = message;
        this.iv = iv;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getIv() {
        return iv;
    }

    @Override
    public String toString() {
        return message;
    }
}
