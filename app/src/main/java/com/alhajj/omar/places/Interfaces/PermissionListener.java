package com.alhajj.omar.places.Interfaces;

public interface PermissionListener {
    void onPermissionAsk(String permission);
    void onPermissionPreviouslyDenied(String permission);
    void onPermissionDisabled();
    void onPermissionGranted();
}
