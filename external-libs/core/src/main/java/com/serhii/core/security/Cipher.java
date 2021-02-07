package com.serhii.core.security;

import com.serhii.core.CoreEngine;
import com.serhii.core.security.impl.crypto.CryptoBase;
import com.serhii.core.security.impl.crypto.CryptoSymmetric;
import com.serhii.core.security.impl.crypto.Result;

public class Cipher implements CryptoBase {

    private CryptoSymmetric impl;

    public Cipher() {
        CoreEngine.getInstance().configure(this);
    }

    public void setSymmetricCrypto(CryptoSymmetric impl) {
        this.impl = impl;
    }

    @Override
    public Result encryptSymmetric(String message) {
        return impl.encryptSymmetric(message);
    }

    @Override
    public Result decryptSymmetric(String message, final byte[] inputIV) {
        return impl.decryptSymmetric(message, inputIV);
    }
}
