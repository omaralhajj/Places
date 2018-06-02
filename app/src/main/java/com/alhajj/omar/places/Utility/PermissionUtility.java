package com.alhajj.omar.places.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import com.alhajj.omar.places.Interfaces.PermissionListener;

// Permission utility class inspired by https://stackoverflow.com/questions/32347532/android-m-permissions-confused-on-the-usage-of-shouldshowrequestpermissionrati (Second answer)
public class PermissionUtility {
    private Context context;

    public PermissionUtility(Context context) {
        this.context = context;
    }

    private boolean shouldCheckPermissions() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private boolean shouldAskPermission(String permission) {
        return shouldCheckPermissions() && ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission(String permission, PermissionListener permissionListener) {
        if (shouldAskPermission(permission)) {
            /**
             * Returns true if previously denied: https://developer.android.com/training/permissions/requesting#java
             * Then prompts user with dialog and rationale explaining the permission needed
             */
            if(((Activity) context).shouldShowRequestPermissionRationale(permission)) {
                permissionListener.onPermissionPreviouslyDenied();
            } else {
                /**
                 * Check to see if this is the first time permission is requested.
                 * If not, then shouldShowRequestPermissionRationale must have returned true, and permission was denied
                 */
                if(context.getSharedPreferences("PERMISSION_STORE", Context.MODE_PRIVATE).getBoolean(permission, false)) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("PERMISSION_STORE", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(permission, true).apply();
                    permissionListener.onPermissionAsk();
                } else {
                    permissionListener.onPermissionDisabled();
                }
            }
        }
    }


}
