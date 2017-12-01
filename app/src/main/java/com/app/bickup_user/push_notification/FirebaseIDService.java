package com.app.bickup_user.push_notification;

import android.util.Log;

import com.app.bickup_user.utility.ConstantValues;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
//FirebaseIDService: Refreshed token: eZwfOlQ4U4w:APA91bGNcP4qmFEs0C-DE4bDPhrq1Oogd5r2h7VvoL4jQMn8xc2rL4aB4_XuPaLB3Ru9pAM6noE_PJvlZ4__brFKRnPX3bwHQStgiUmCQX8jKig-00K4rzAZwcVqYaNUzaxA_7KKG92j
        ConstantValues.Is_device_token=refreshedToken;
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);


    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}