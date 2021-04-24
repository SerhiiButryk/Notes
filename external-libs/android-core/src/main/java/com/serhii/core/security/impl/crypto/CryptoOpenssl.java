package com.serhii.core.security.impl.crypto;

import com.serhii.core.CoreEngine;

/**
 *  Class provides OpenSSL interface for crypto_utils operations
 *
 */
public class CryptoOpenssl implements CryptoProvider {

    public static final String IV_TAG = "iv_tag";

    @Override
    public Result encryptSymmetricWithKey(String message, String key, final byte[] inputIV) {

        Result resultData = new Result("", null, CryptoError.OK);

        String encryptedMessage = _encryptSymmetric(message, key, new String(inputIV));

        if (encryptedMessage.isEmpty()) {
            resultData.setError(CryptoError.UNKNOWN);
        } else {
            resultData.setMessage(encryptedMessage);
            resultData.setIv(inputIV);
        }

        return resultData;
    }

    @Override
    public Result decryptSymmetricWithKey(String message, String key, final byte[] inputIV) {

        Result resultData = new Result("", null, CryptoError.OK);

        String decryptedMessage = _decryptSymmetric(message, key, new String(inputIV));

        if (decryptedMessage.isEmpty()) {
            resultData.setError(CryptoError.UNKNOWN);
        } else {
            resultData.setMessage(decryptedMessage);
            resultData.setIv(inputIV);
        }

        return resultData;
    }

    @Override
    public Result encryptSymmetric(String message, byte[] inputIV) {
        // No-op
        throw new RuntimeException("Illegal operation with the provider");
    }

    @Override
    public Result decryptSymmetric(String message, byte[] inputIV) {
        // No-op
        throw new RuntimeException("Illegal operation with the provider");
    }

    @Override
    public void selectKey(String key) {
        // No-op
        throw new RuntimeException("Illegal operation with the provider");
    }

    @Override
    public void createKey(String key, int timeOutSeconds, boolean authRequired) {
        // No-op
        throw new RuntimeException("Illegal operation with the provider");
    }

    private native String _encryptSymmetric(String message, String key, String iv);

    private native String _decryptSymmetric(String message, String key, String iv);

    static {
        CoreEngine.loadNativeLibrary();
    }

}
