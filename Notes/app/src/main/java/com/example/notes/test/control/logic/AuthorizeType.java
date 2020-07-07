package com.example.notes.test.control.logic;

public enum  AuthorizeType {

    AUTH_REGISTRATION("AUTH_REGISTRATION", 101),
    AUTH_LOGIN_BASIC("AUTH_REGISTRATION", 102),
    AUTH_UNLOCK("AUTH_UNLOCK_LOGIN", 103),
    AUTH_BIOMETRIC_LOGIN("AUTH_BIOMETRIC_LOGIN", 103);

    String description;
    int type;

    AuthorizeType(String description, int type) {
        this.description = description;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

}
