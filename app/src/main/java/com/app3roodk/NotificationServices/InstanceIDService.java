package com.app3roodk.NotificationServices;


import com.app3roodk.Schema.User;
import com.app3roodk.UtilityGeneral;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class InstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d("InstanceID", "Refreshed token: " + refreshedToken);
        if (UtilityGeneral.isRegisteredUser(getBaseContext()))
            sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        final User user = UtilityGeneral.loadUser(getBaseContext());
        user.setNotificationToken(refreshedToken);
        FirebaseDatabase.getInstance().getReference("Users").child(user.getObjectId()).child("NotificationToken").setValue(refreshedToken, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    UtilityGeneral.saveUser(getBaseContext(), user);
                }
            }
        });
    }
}
