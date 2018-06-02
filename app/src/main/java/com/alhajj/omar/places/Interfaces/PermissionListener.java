package com.alhajj.omar.places.Interfaces;

public interface PermissionListener {
    /*
     * Callback to ask permission
     * */
    void onPermissionAsk();
    /*
     * Callback on permission denied
     * */
    void onPermissionPreviouslyDenied();
    /*
     * Callback on permission "Never show again" checked and denied
     * */
    void onPermissionDisabled();
    /*
     * Callback on permission granted
     * */
    void onPermissionGranted();
}
