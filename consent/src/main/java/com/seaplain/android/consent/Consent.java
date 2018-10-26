package com.seaplain.android.consent;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;

import com.seaplain.android.consent.PermissionRequest.ExplanationListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Library class that manages permission requests
 */
public class Consent {

    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    private static Consent mInstance;
    private SparseArray<PermissionRequest> mPendingRequests = new SparseArray<>();

    /**
     * @return The current instance of {@link Consent}
     */
    private static Consent getInstance() {
        if (mInstance == null) {
            mInstance = new Consent();
        }
        return mInstance;
    }

    /**
     * Private constructor to prevent outside initialisation
     */
    private Consent() {}

    /**
     * Inform {@link Consent} of the permission result so that it may trigger the appropriate callbacks
     */
    public static void handle(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Perform an action that needs a permission to complete
     */
    public static void request(PermissionRequest request) {
        getInstance().requestPermissions(request);
    }

    private void requestPermissions(PermissionRequest request) {
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

    private void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest request = mPendingRequests.get(requestCode);
        if (request != null) {
            mPendingRequests.remove(requestCode);

            if (permissions.length == 0 && grantResults.length == 0) {
                // According to Activity#onRequestPermissionsResult, this means permissions request was cancelled.
                return;
            }

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
        HostWrapper.requestPermissions(request.getHostWrapper(), request.getUnprovidedPermissions(), id);
    }

    /**
     * Helper method to make the explanation logic easier to work with
     */
    private void showExplanation(@NonNull final PermissionRequest request) {
        // We do it this way so that if a user of the manager wants, they can return
        // null with onExplanationRequested, display their own way and then call onExplanationCompleted themselves
        request.setExplanationListener(new ExplanationListener() {
            @Override
            public void onExplanationCompleted() {
                executeRequest(request);
            }
        });

        // Build default dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(request.getContext())
                .setTitle(R.string.default_explanation_title)
                .setMessage(R.string.default_explanation_message)
                .setPositiveButton(R.string.default_explanation_confirm, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        request.onExplanationCompleted();
                    }
                }); // This will catch every way the dialog can be closed instead of just the button

        // Provide to request to customise
        builder = request.onExplanationRequested(builder);

        // If they return null, then they are handling it and request.onExplanationCompleted() will be triggered by them
        if (builder != null) {
            builder.show();
        }
    }
}
