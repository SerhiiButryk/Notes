package com.example.notes.test.control.types;

public enum RequestType {

    REQ_AUTHORIZE("REQ_AUTHORIZE", 1),
    REQ_REGISTER("REQ_REGISTER", 2),
    REQ_BIOMETRIC_LOGIN("REQ_BIOMETRIC_LOGIN", 3),
    REQ_UNLOCK_KEYSTORE("REQ_BIOMETRIC_LOGIN", 4);

    String description;
    int type;

    RequestType(String description, int type) {
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
