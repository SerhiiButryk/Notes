package com.example.notes.test.control.types;

public enum AuthResult {

    // Has to have non positive value

    EMPTY_FIELD(-1, "EMPTY_FIELD"),
    WRONG_PASSWORD(-2, "WRONG_PASSWORD"),
    ACCOUNT_INVALID(-3, "ACCOUNT_INVALID"),
    USER_NAME_EXISTS(-4, "USER_NAME_EXISTS"),
    PASSWORD_DIFFERS(-5, "PASSWORD_DIFFERS"),
    SPACE_CONTAIN(-6, "SPACE_CONTAIN"),
    UNLOCK_KEY_INVALID(-10, "UNLOCK_KEY_INVALID");

    int typeId;
    String name;

    AuthResult(int typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }

    public int getTypeId() { return typeId; }

    public String getName() { return name; }
}
