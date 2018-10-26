package com.seaplain.android.consent;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * Encapsulates the different hosts that support permission delegation
 */
public class HostWrapper {
    private final Activity activity;
    private final Fragment fragment;

    public HostWrapper(@NonNull Activity activity) {
        this.activity = activity;
        this.fragment = null;
    }

    public HostWrapper(@NonNull Fragment fragment) {
        this.activity = null;
        this.fragment = fragment;
    }

    public Context getContext() {
        return activity != null ? activity : fragment.getContext();
    }

    public static boolean shouldShowRequestPermissionRationale(@NonNull HostWrapper hostWrapper, @NonNull String permission) {
        if (hostWrapper.activity != null) {
            return ActivityCompat.shouldShowRequestPermissionRationale(hostWrapper.activity, permission);
        } else if (hostWrapper.fragment != null) {
            return hostWrapper.fragment.shouldShowRequestPermissionRationale(permission);
        } else {
            throw new RuntimeException("No host defined. Either an activity or fragment has to be provided.");
        }
    }

    public static void requestPermissions(@NonNull HostWrapper hostWrapper, @NonNull String[] permissions, int requestCode) {
        if (hostWrapper.activity != null) {
            ActivityCompat.requestPermissions(hostWrapper.activity, permissions, requestCode);
        } else if (hostWrapper.fragment != null) {
            hostWrapper.fragment.requestPermissions(permissions, requestCode);
        } else {
            throw new RuntimeException("No host defined. Either an activity or fragment has to be provided.");
        }
    }

}
