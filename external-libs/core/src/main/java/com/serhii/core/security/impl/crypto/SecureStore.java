package com.serhii.core.security.impl.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Base64;

import com.serhii.core.log.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecureStore implements CryptoSymmetric {

    private static final String TAG = "SecureStore";

    private static final String SECRET_KEY_ALIAS = "SK-729034";
    private static final String SECRET_KEY_ALGORITHM = "AES/GCM/NoPadding";

    private KeyStore keyStore;

    public SecureStore() {
        init();
    }

    @Override
    public Result encryptSymmetric(String message) {
        try {

            if (!isSecretKeyExists() && !tryRegenSecretKey()) {
                Log.error(TAG, "encryptSymmetric() failed to regen keys");
                // Should request authorization
                return new Result("", null, CryptoError.USER_NOT_AUTHORIZED);
            }

            final SecretKey secretKey = (SecretKey) keyStore.getKey(SECRET_KEY_ALIAS, null);

            Cipher encryptionCipher = Cipher.getInstance(SECRET_KEY_ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] _iv = encryptionCipher.getIV();
            byte[] encryptedText = encryptionCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return new Result(new String(Base64.encode(encryptedText, Base64.NO_WRAP)), _iv, CryptoError.OK);

        } catch (UserNotAuthenticatedException e) {
            Log.error(TAG, "user not authenticated exception: " + e);
            e.printStackTrace();

            return new Result("", null, CryptoError.USER_NOT_AUTHORIZED);

        } catch (Exception e) {
            Log.error(TAG, "exception during encryption: " + e.getMessage());
            e.printStackTrace();
        }

        return new Result("", null, CryptoError.UNKNOWN);
    }

    @Override
    public Result decryptSymmetric(String message, final byte[] inputIV) {

        try {

            if (!isSecretKeyExists() && !tryRegenSecretKey()) {
                Log.error(TAG, "decryptSymmetric() failed to regen keys");
                // Should request authorization
                return new Result("", null, CryptoError.USER_NOT_AUTHORIZED);
            }

            final SecretKey secretKey = (SecretKey) keyStore.getKey(SECRET_KEY_ALIAS, null);

            final Cipher cipher = Cipher.getInstance(SECRET_KEY_ALGORITHM);
            final GCMParameterSpec spec = new GCMParameterSpec(128, inputIV);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] textToDecrypt = message.getBytes();

            final byte[] text = cipher.doFinal(Base64.decode(textToDecrypt, Base64.NO_WRAP));

            return new Result(new String(text), null, CryptoError.OK);

        } catch (UserNotAuthenticatedException e) {
            Log.error(TAG, "user not authenticated exception: " + e);
            e.printStackTrace();

            return new Result("", null, CryptoError.USER_NOT_AUTHORIZED);

        } catch (Exception e) {
            Log.error(TAG, "exception during decryption: " + e.getMessage());
            e.printStackTrace();
        }

        return new Result("", null, CryptoError.UNKNOWN);

    }

    private void init() {
        keyStore = initKeyStore();

        if (keyStore != null) {
            boolean success = load(keyStore);

            if (success && !isSecretKeyExists()) {
                // Gen a secret key entity
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

    private boolean createKey() {
        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(SECRET_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                // More info on GCM - https://en.wikipedia.org/wiki/Galois/Counter_Mode
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                // GCM doesn't use padding
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                // User authentication is needed to user this key
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(500)
                .build();

        KeyGenerator keyGenerator = getKeyGenerator(KeyProperties.KEY_ALGORITHM_AES);

        if (keyGenerator != null) {
            return genSecretKey(keyGenerator, keyGenParameterSpec);
        }

        return false;
    }

    private KeyStore initKeyStore() {
        try {
            return KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            Log.error(TAG, "exception: failed to init keyStore object" + e.getMessage());
        }
        return null;
    }

    private KeyGenerator getKeyGenerator(String algorithm) {
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
        } catch (InvalidAlgorithmParameterException e) {
            Log.error(TAG, "exception: failed to gen Secret Key: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean tryRegenSecretKey() {
        return createKey();
    }

}
