package com.seaplain.android.consent;

/**
 * Encapsulates a permission that has been declined by the user
 */
public class DeclinedPermission {
    private String mPermission;
    private boolean mNeverAskAgainChecked;

    public DeclinedPermission(String permission, boolean neverAskAgainChecked) {
        mPermission = permission;
        mNeverAskAgainChecked = neverAskAgainChecked;
    }

    public String getPermission() {
        return mPermission;
    }

    public boolean isNeverAskAgainChecked() {
        return mNeverAskAgainChecked;
    }
}