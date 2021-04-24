package com.serhii.core.security.impl.crypto;

public enum  CryptoError {

    OK(1, "NO_ERRORS"),
    USER_NOT_AUTHORIZED(2, "USER_NOT_AUTHORIZED"),
    UNKNOWN(3, "UNKNOWN");

    private int code;
    private String name;

    CryptoError(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
