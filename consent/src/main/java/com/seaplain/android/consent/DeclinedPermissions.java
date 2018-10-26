package com.seaplain.android.consent;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.seaplain.android.consent.HostWrapper.shouldShowRequestPermissionRationale;

/**
 * Encapsulates a list of declined permissions from the user, with some helper functions for easier usage
 */
public class DeclinedPermissions {
    private List<DeclinedPermission> mDeclinedPermissions;
    private List<DeclinedPermission> mNeverAskAgainPermissions; // Cached for performance

    public static DeclinedPermissions from(@NonNull String[] permissions, @NonNull int[] grantResults, @NonNull PermissionRequest request) {
        List<DeclinedPermission> declinedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PERMISSION_GRANTED) {
                // If it was declined but we are told not to show an explanation, they must have selected the "never show again" option
                declinedPermissions.add(new DeclinedPermission(permissions[i], !shouldShowRequestPermissionRationale(request.getHostWrapper(), permissions[i])));
            }
        }
        return new DeclinedPermissions(declinedPermissions);
    }

    /**
     * Private constructor, class must be created using {@link #from(String[], int[], PermissionRequest)}
     */
    private DeclinedPermissions(@NonNull List<DeclinedPermission> declinedPermissions) {
        mDeclinedPermissions = declinedPermissions;
    }

    /**
     * Returns all permissions declined by the user, including Never Ask Again permissions
     */
    @NonNull
    public List<DeclinedPermission> getDeclinedPermissions() {
        return mDeclinedPermissions;
    }

    /**
     * Shortcut for {@code getDeclinedPermissions().isEmpty()}
     */
    public boolean isEmpty() {
        return mDeclinedPermissions.isEmpty();
    }

    /**
     * Shortcut for {@code getDeclinedPermissions().size()}
     */
    public int size() {
        return mDeclinedPermissions.size();
    }

    /**
     * Shortcut for {@code getDeclinedPermissions().get()}
     */
    public DeclinedPermission get(int location) {
        return mDeclinedPermissions.get(location);
    }

    public boolean hasNeverAskAgainPermissions() {
        return !getNeverAskAgainPermissions().isEmpty();
    }

    /**
     * Returns only the permissions declined by the user that are rejected due to the Never Ask Again option
     */
    @NonNull
    public List<DeclinedPermission> getNeverAskAgainPermissions() {
        if (mNeverAskAgainPermissions == null) {
            mNeverAskAgainPermissions = new ArrayList<>();
            for (DeclinedPermission permission : mDeclinedPermissions) {
                if (permission.isNeverAskAgainChecked()) {
                    mNeverAskAgainPermissions.add(permission);
                }
            }
        }
        return mNeverAskAgainPermissions;
    }
}