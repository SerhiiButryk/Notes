package com.example.notes.test.control;

import com.example.core.security.Hash;

import static com.example.notes.test.common.AppUtils.RUNTIME_LIBRARY;

/**
 *  Class which accesses underling C++ API's and methods
 */

public class NativeBridge {

    public NativeBridge() { }

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

    public void setIdleLockTime(int milliseconds) {
        _setIdleLockTime(milliseconds);
    }

    public int getIdleLockTime() {
        return _getIdleLockTime();
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

    private native void _setIdleLockTime(int milliseconds);
    private native int _getIdleLockTime();

    private native String _getUnlockKey();

    static {
        System.loadLibrary(RUNTIME_LIBRARY);
    }

}
