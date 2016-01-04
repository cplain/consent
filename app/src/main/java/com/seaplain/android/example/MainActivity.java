package com.seaplain.android.example;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.seaplain.android.consent.Consent;
import com.seaplain.android.consent.DeclinedPermissions;
import com.seaplain.android.consent.PermissionRequest;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * A demo activity for Consent
 */
public class MainActivity extends AppCompatActivity {

    /**
     * This could be in a base activity in a real project
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Consent.handle(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.request_one_btn)
    public void onRequestOneClicked() {
        Consent.request(new PermissionRequest(this, READ_CONTACTS) {
            @Override
            protected void onPermissionsGranted() {
                performDesiredAction();
            }

            @Override
            protected void onPermissionsDeclined(@NonNull DeclinedPermissions declinedPermissions) {
                performPermissionDeniedAction(declinedPermissions.hasNeverAskAgainPermissions());
            }

            @Override
            protected Builder onExplanationRequested(@NonNull Builder builder, @NonNull String[] permissionsToExplain) {
                return builder.setTitle(R.string.explanation_title_single).setMessage(R.string.explanation_message_single);
            }
        });
    }

    @OnClick(R.id.request_multiple_btn)
    public void onRequestMultipleClicked() {
        Consent.request(new PermissionRequest(this, READ_CONTACTS, ACCESS_FINE_LOCATION) {
            @Override
            protected void onPermissionsGranted() {
                performDesiredAction();
            }

            @Override
            protected void onPermissionsDeclined(@NonNull DeclinedPermissions declinedPermissions) {
                performPermissionDeniedAction(declinedPermissions.hasNeverAskAgainPermissions());
            }

            @Override
            protected Builder onExplanationRequested(@NonNull Builder builder, @NonNull String[] permissionsToExplain) {
                return builder.setTitle(R.string.explanation_title_multiple).setMessage(R.string.explanation_message_multiple);
            }
        });
    }

    protected void performDesiredAction() {
        Toast.makeText(this, "The action has been successfully performed", LENGTH_SHORT).show();
    }

    protected void performPermissionDeniedAction(boolean neverAskAgainChecked) {
        String message = neverAskAgainChecked ?
                "You selected never ask again, go to settings if you want to change the permission" :
                "The action has been prevented because no permission was provided";

        Toast.makeText(this, message, LENGTH_SHORT).show();
    }
}
