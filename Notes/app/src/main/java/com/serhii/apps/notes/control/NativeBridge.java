package com.serhii.apps.notes.control;

import com.serhii.core.security.Hash;

import static com.serhii.apps.notes.common.AppConstants.RUNTIME_LIBRARY;

/**
 *  Class accessor for underling C++ APIs
 */

public class NativeBridge {

    public String getUserName() {
        return _getUserName();
    }

    public boolean verifyPassword(String password) {

        Hash hash = new Hash();

        return _verifyPassword(_getUserName(), hash.hashMD5(password));
    }

    public boolean setNewPassword(String password) {

        Hash hash = new Hash();

        return _setNewPassword(hash.hashMD5(password));
    }

    public void setAttemptLimit(int newLimit) {
        _setAttemptLimit(newLimit);
    }

    public int getAttemptLimit() {
        return _getAttemptLimit();
    }

    public void updateLoginLimit() {
        setLimitLeft(getAttemptLimit());
    }

    public int getLimitLeft() {
        return _getLimitLeft();
    }

    public void setLimitLeft(int newValue) {
        _setLimitLeft(newValue);
    }

    public void executeBlockApp() {
        _executeBlockApp();
    }

    public boolean isAppBlocked() {
        return _isAppBlocked();
    }

    public String getUnlockKey() {
        return _getUnlockKey();
    }

    private native String _getUserName();

    private native boolean _verifyPassword(String userName, String password);
    private native boolean _setNewPassword(String password);

    private native void _clearAppData();

    private native int _getAttemptLimit();
    private native int _getLimitLeft();
    private native void _setAttemptLimit(int newLimit);
    private native void _setLimitLeft(int newValue);

    private native void _executeBlockApp();
    private native boolean _isAppBlocked();

    private native String _getUnlockKey();

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
