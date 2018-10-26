package com.seaplain.android.consent;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.seaplain.android.consent.HostWrapper.shouldShowRequestPermissionRationale;

/**
 * Encapsulates a request for permissions, contains all relevant information
 */
public abstract class PermissionRequest {

    public interface ExplanationListener {
        void onExplanationCompleted();
    }

    private HostWrapper mHostWrapper;
    private String[] mRequestedPermissions;
    private String[] mPermissionsThatNeedExplanation; // Cached for performance reasons
    private String[] mUnprovidedPermissions; // Cached for performance reasons
    private ExplanationListener mExplanationListener;

    public PermissionRequest(@NonNull Activity activity, @NonNull String... permissions) {
        mHostWrapper = new HostWrapper(activity);
        mRequestedPermissions = permissions;
    }

    public PermissionRequest(@NonNull Fragment fragment, @NonNull String... permissions) {
        mHostWrapper = new HostWrapper(fragment);
        mRequestedPermissions = permissions;
    }

    /**
     * A message to display to the user to indicate why the permission is required, return null or empty if you wish to manage this yourself, then call {@link #onExplanationCompleted()}
     */
    protected abstract AlertDialog.Builder onExplanationRequested(@NonNull AlertDialog.Builder builder, @NonNull String[] permissionsToExplain);

    protected abstract void onPermissionsGranted();

    protected abstract void onPermissionsDeclined(@NonNull DeclinedPermissions declinedPermissions);

    public Context getContext() {
        return mHostWrapper.getContext();
    }

    public HostWrapper getHostWrapper() {
        return mHostWrapper;
    }

    public String[] getRequestedPermissions() {
        return mRequestedPermissions;
    }

    public void onExplanationCompleted() {
        if (mExplanationListener != null) {
            mExplanationListener.onExplanationCompleted();
        }
    }

    public boolean hasUnprovidedPermissions(){
        return getUnprovidedPermissions().length > 0;
    }

    public boolean hasPermissionsThatNeedExplanation(){
        return getPermissionsThatNeedExplanation().length > 0;
    }

    public String[] getUnprovidedPermissions() {
        if (mUnprovidedPermissions == null) {
            List<String> requiredPermissions = new ArrayList<>();
            for (String permission : getRequestedPermissions()) {
                if (checkSelfPermission(getContext(), permission) != PERMISSION_GRANTED) {
                    requiredPermissions.add(permission);
                }
            }
            mUnprovidedPermissions = requiredPermissions.toArray(new String[requiredPermissions.size()]);
        }
        return mUnprovidedPermissions;
    }

    public String[] getPermissionsThatNeedExplanation() {
        if (mPermissionsThatNeedExplanation == null) {
            List<String> permsToExplain = new ArrayList<>();
            for (String permission : getRequestedPermissions()) {
                if (shouldShowRequestPermissionRationale(mHostWrapper, permission)) {
                    permsToExplain.add(permission);
                }
            }
            mPermissionsThatNeedExplanation = permsToExplain.toArray(new String[permsToExplain.size()]);
        }
        return mPermissionsThatNeedExplanation;
    }

    public void setExplanationListener(ExplanationListener explanationListener) {
        mExplanationListener = explanationListener;
    }

    /**
     * Overloaded convenience method, package private as only internal classes will be using it
     */
    AlertDialog.Builder onExplanationRequested(@NonNull AlertDialog.Builder builder) {
        return onExplanationRequested(builder, getPermissionsThatNeedExplanation());
    }
}
