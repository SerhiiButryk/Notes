package com.example.notes.test.ui.data_model;

import androidx.annotation.NonNull;

import com.example.notes.test.control.logic.AuthorizeType;

public class AuthModel {

    private String email;
    private String password;
    private String confirmPassword;

    private AuthorizeType authType;

    public AuthModel() {
        this.email = "";
        this.password = "";
        this.confirmPassword = "";
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public void setAuthType(AuthorizeType authType) { this.authType = authType; }

    public AuthorizeType getAuthType() { return authType; }

    @NonNull
    @Override
    public String toString() {
        return "{" + email + ":" + password + ":" + confirmPassword + "}";
    }
}
