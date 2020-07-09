package com.example.notes.test.control.logic;

public enum UserRequest {

    REQ_AUTHORIZE("REQ_AUTHORIZE", 1),
    REQ_REGISTER("REQ_REGISTER", 2),
    REQ_CHECK_PASSWORD("REQ_CHECK_PASSWORD", 3),
    REQ_BIOMETRIC_LOGIN("REQ_BIOMETRIC_LOGIN", 3);

    String description;
    int type;

    UserRequest(String description, int type) {
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
