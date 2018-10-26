Consent
===============
A nifty little library to simplify runtime permissions



Usage
----------------------------------
Using Consent is simple:

```java
Consent.request(new PermissionRequest(this, READ_CONTACTS, ACCESS_FINE_LOCATION) {
    @Override
    protected void onPermissionsGranted() {
        // perform desired action
    }

    @Override
    protected void onPermissionsDeclined(@NonNull DeclinedPermissions declinedPermissions) {
        // perform rejected action
        // Note: DeclinedPermissions contains all the permissions rejected in this request as well as various
        // helpers, such as ones to extract permissions rejected because the user selected "Never Ask Again"
    }

    @Override
    protected AlertDialog.Builder onExplanationRequested(@NonNull AlertDialog.Builder builder, @NonNull String[] permissionsToExplain) {
        // return the builder if you want to show a dialog or null if you want to handle the explanation yourself
        // if you are handling the explanation yourself be sure to call onExplanationCompleted() when you want the request to continue
        return builder.setTitle(R.string.explanation_title_multiple).setMessage(R.string.explanation_message_multiple);
    }
});
```

Due to how Android processes permissions you also need to add the following code (which can be placed in a base activity or fragment if it suits you)

```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Consent.handle(requestCode, permissions, grantResults);
}
```


Download via jcenter
----------------------------------
```groovy
compile 'com.seaplain:consent:1.0.3'
```

License
----------------------------------

    Copyright 2015 Coby Plain

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.