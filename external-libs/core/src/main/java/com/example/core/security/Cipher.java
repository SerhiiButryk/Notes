package com.example.core.security;

import com.example.core.CoreEngine;
import com.example.core.common.components.Components;
import com.example.core.security.impl.crypto.CryptoBase;
import com.example.core.security.impl.crypto.CryptoSymmetric;
import com.example.core.security.impl.crypto.Result;

public class Cipher implements CryptoBase {

    private CryptoSymmetric impl;

    public Cipher() {
        CoreEngine.getInstance().configure(this);
    }

    public void setCrypto(CryptoSymmetric impl) {
        this.impl = impl;
    }

    @Override
    public Result encryptSymmetric(String message) {
        return impl.encryptSymmetric(message);
    }

    @Override
    public String decryptSymmetric(String message, final byte[] inputIV) {
        return impl.decryptSymmetric(message, inputIV);
    }
}
