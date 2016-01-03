package com.seaplain.android.consent;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class that manages permission requests
 */
public class PermissionManager {

    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    private SparseArray<PermissionRequest> mPendingRequests = new SparseArray<>();

    /**
     * Perform an action that needs a permission to complete
     */
    public void requestPermissions(PermissionRequest request) {
        if (request.hasUnprovidedPermissions()) {
            if (request.hasPermissionsThatNeedExplanation()) {
                showExplanation(request);
            } else {
                executeRequest(request);
            }
        } else {
            request.onPermissionsGranted();
        }
    }

    /**
     * Inform the {@link PermissionManager} of the permission result so that it may trigger the appropriate callbacks
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest request = mPendingRequests.get(requestCode);
        if (request != null) {
            mPendingRequests.remove(requestCode);
            DeclinedPermissions declinedPermissions = DeclinedPermissions.from(permissions, grantResults, request);

            if (declinedPermissions.isEmpty()) {
                request.onPermissionsGranted();
            } else {
                request.onPermissionsDeclined(declinedPermissions);
            }
        }
    }

    /**
     * Condenses all actual permission requesting into one distinct place
     */
    private void executeRequest(PermissionRequest request) {
        int id = NEXT_ID.incrementAndGet();
        mPendingRequests.append(id, request);
        ActivityCompat.requestPermissions(request.getContext(), request.getUnprovidedPermissions(), id);
    }

    /**
     * Helper method to make the explanation logic easier to work with
     */
    private void showExplanation(@NonNull final PermissionRequest request) {
        // We do it this way so that if a user of the manager wants, they can return
        // null with onExplanationRequested, display their own way and then call onExplanationCompleted themselves
        request.setExplanationListener(new PermissionRequest.ExplanationListener() {
            @Override
            public void onExplanationCompleted() {
                executeRequest(request);
            }
        });

        // Build default dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(request.getContext())
                .setTitle("Permission Required")
                .setMessage("We need to be able to access a part of your phone for this feature to function correctly")
                .setPositiveButton("OK", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        request.onExplanationCompleted();
                    }
                }); // This will catch every way the dialog can be closed instead of just the button

        // Provide to request to customisez
        builder = request.onExplanationRequested(builder);

        // If they return null, then they are handling it and request.onExplanationCompleted() will be triggered by them
        if (builder != null) {
            builder.show();
        }
    }
}
