package com.serhii.apps.notes.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.preference.PreferenceManager;

import com.serhii.apps.notes.R;
import com.serhii.apps.notes.common.AppConstants;
import com.serhii.core.security.Cipher;
import com.serhii.core.security.Hash;
import com.serhii.core.security.impl.crypto.Result;

import java.util.UUID;

import static com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY;

/**
 *  Class accessor for underling C++ APIs
 */

public class NativeBridge {

    private static final String LOG_LIMIT_MARKER = "SSDD"; // Random value
    private static final String LOG_UNLOCK_KEY_MARKER = "AALL"; // Random value

    public String getUserName() {
        return _getUserName();
    }

    public boolean verifyPassword(String passwordHash) {
        return _verifyPassword(_getUserName(), passwordHash);
    }

    public boolean setNewPassword(String password) {

        Hash hash = new Hash();

        return _setNewPassword(hash.hashMD5(password));
    }

    public void executeBlockApp() {
        _executeBlockApp();
    }

    public boolean isAppBlocked() {
        return _isAppBlocked();
    }

    public void setLoginLimitFromDefault(Context context) {
        // Get limit value from prefs
        String limit = getLockLimit(context);

        setLimitAndEnc(Integer.parseInt(limit));
    }

    public void setLimitLeft(int value) {
        setLimitAndEnc(value);
    }

    private void setLimitAndEnc(int value) {
        // Encrypt value
        Cipher cipher = new Cipher();
        cipher.selectKey(AppConstants.SECRET_KEY_PASSWORD_ENC_ALIAS);
        Result result = cipher.encryptSymmetric(String.valueOf(value));

        String iv = new String(Base64.encode(result.getIv(), Base64.NO_WRAP));

        String encMessage = result.getMessage() + LOG_LIMIT_MARKER + iv;

        // Pass enc data
        _setLimitLeft(encMessage);
    }

    public void resetLoginLimitLeft(Context context) {
        setLoginLimitFromDefault(context);
    }

    public int getLimitLeft() {
        String encMessage = _getLimitLeft();

        // Decrypt value
        String encLimit = encMessage.split(LOG_LIMIT_MARKER)[0];
        String encIv = encMessage.split(LOG_LIMIT_MARKER)[1];

        byte[] iv = Base64.decode(encIv.getBytes(), Base64.NO_WRAP);

        Cipher cipher = new Cipher();
        cipher.selectKey(AppConstants.SECRET_KEY_PASSWORD_ENC_ALIAS);
        Result result = cipher.decryptSymmetric(encLimit, iv);

        // Return result
        return Integer.parseInt(result.getMessage());
    }

    public String getLockLimit(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String logLimitDefault = context.getString(R.string.preference_login_limit_default);

        return sharedPreferences.getString(context.getString(R.string.preference_login_limit_key), logLimitDefault);
    }

    public void createUnlockKey() {
        String unlockKey = UUID.randomUUID().toString();

        // Take the first 8 characters
        unlockKey = unlockKey.substring(0, unlockKey.indexOf('-'));

        // Encrypt value
        Cipher cipher = new Cipher();
        cipher.selectKey(AppConstants.SECRET_KEY_PASSWORD_ENC_ALIAS);
        Result result = cipher.encryptSymmetric(unlockKey);

        String iv = new String(Base64.encode(result.getIv(), Base64.NO_WRAP));

        String encMessage = result.getMessage() + LOG_UNLOCK_KEY_MARKER + iv;

        _setUnlockKey(encMessage);
    }

    public String getUnlockKey() {

        String unlockKey = _getUnlockKey();

        // Decrypt value
        String encLimit = unlockKey.split(LOG_UNLOCK_KEY_MARKER)[0];
        String encIv = unlockKey.split(LOG_UNLOCK_KEY_MARKER)[1];

        byte[] iv = Base64.decode(encIv.getBytes(), Base64.NO_WRAP);

        Cipher cipher = new Cipher();
        cipher.selectKey(AppConstants.SECRET_KEY_PASSWORD_ENC_ALIAS);
        Result result = cipher.decryptSymmetric(encLimit, iv);

        // Return result
        return result.getMessage();
    }

    public boolean checkPasswordRequirements(String password) {

        if (password == null || password.isEmpty())
            return false;

        // TODO: Check password strength

        return true;
    }

    private native String _getUserName();

    private native boolean _verifyPassword(String userName, String password);
    private native boolean _setNewPassword(String password);

    private native void _clearAppData();

    private native String _getLimitLeft();
    private native void _setLimitLeft(String newValue);

    private native void _executeBlockApp();
    private native boolean _isAppBlocked();

    private native String _getUnlockKey();
    private native void _setUnlockKey(String unlockKey);

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
