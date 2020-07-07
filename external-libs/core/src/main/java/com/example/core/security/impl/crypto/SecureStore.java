package com.example.core.security.impl.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.example.core.common.log.Log;
import com.example.core.security.impl.crypto.CryptoSymmetric;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecureStore implements CryptoSymmetric {

    private static final String TAG = "SecureStore";

    private final String SECRET_KEY_ALIAS = "SK-729034-11_LK";
    private KeyStore keyStore;

    public SecureStore() {
        init();
    }

    @Override
    public Result encryptSymmetric(String message) {
        try {

            final SecretKey secretKey = (SecretKey) keyStore.getKey(SECRET_KEY_ALIAS, null);

            Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] _iv = encryptionCipher.getIV();
            byte[] encryptedText = encryptionCipher.doFinal(message.getBytes("UTF-8"));

            return new Result(new String(Base64.encode(encryptedText, Base64.DEFAULT)), _iv);

        } catch (Exception e) {
            Log.error(TAG, "exception during encryption: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String decryptSymmetric(String message, final byte[] inputIV) {

        try {

            final SecretKey secretKey = (SecretKey) keyStore.getKey(SECRET_KEY_ALIAS, null);

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec spec = new GCMParameterSpec(128, inputIV);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] textToDecrypt = message.getBytes();

            final byte[] text = cipher.doFinal(Base64.decode(textToDecrypt, Base64.DEFAULT));

            return new String(text);

        } catch (Exception e) {
            Log.error(TAG, "exception during decryption: " + e.getMessage());
            e.printStackTrace();
        }

        return "";

    }

    private void init() {
        keyStore = initKeyStore(null);

        boolean success = false;

        if (keyStore != null) {
            success = load(keyStore);
        }

        if (success) {

            if (!isSecretKeyExists()) {
                // gen a new secret key entity
                createKey();
            }

        }

    }

    private boolean load(KeyStore keyStore) {
        try {
            keyStore.load(null);
            return true;
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            Log.error(TAG, "exception during loading keyStore: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private boolean isSecretKeyExists() {
        try {

            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(SECRET_KEY_ALIAS, null);

            return secretKeyEntry != null;

        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            Log.error(TAG, "exception: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void createKey() {
        final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(SECRET_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();

        KeyGenerator keyGenerator = getKeyGenerator(KeyProperties.KEY_ALGORITHM_AES, null);

        if (keyGenerator != null) {
            genSecretKey(keyGenerator, keyGenParameterSpec);
        }
    }

    private KeyStore initKeyStore(String provider) {
        try {
            return KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            Log.error(TAG, "exception: failed to init keyStore object" + e.getMessage());
        }
        return null;
    }

    private KeyGenerator getKeyGenerator(String algorithm, String provider) {
        try {
            return KeyGenerator.getInstance(algorithm, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.error(TAG, "exception: failed to get Key Generator" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private boolean genSecretKey(KeyGenerator keyGenerator, KeyGenParameterSpec keyGenParameterSpec) {
        try {
            keyGenerator.init(keyGenParameterSpec);
            keyGenerator.generateKey();
            return true;
        } catch (InvalidAlgorithmParameterException e) {
            Log.error(TAG, "exception: failed to gen Secret Key" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
