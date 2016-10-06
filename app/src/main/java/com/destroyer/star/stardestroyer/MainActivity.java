package com.destroyer.star.stardestroyer;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient sGoogleApiClient;
    private FirebaseAuth sFirebaseAuth;
    private FirebaseUser sFirebaseUser;
    private FirebaseRemoteConfig sFirebaseRemoteConfig;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AdView sAdView;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_INVITE = 1;
    public static final String ANONYMOUS = "anonymous";
    private String sUsername;
    private String sPhotoUrl;
    private SharedPreferences sSharedPreferences;

    private LinearLayoutManager sLinearLayoutManager;
    private ProgressBar sProgressBar;

    private DatabaseReference sFirebaseDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        sGoogleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //TODO should check the device for compatible google play services apk -- also in on resume
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sUsername = ANONYMOUS;
        sFirebaseAuth = FirebaseAuth.getInstance();
        sFirebaseUser =sFirebaseAuth.getCurrentUser();
        if (sFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            sUsername = sFirebaseUser.getDisplayName();
            if (sFirebaseUser.getPhotoUrl() != null) {
                sPhotoUrl = sFirebaseUser.getPhotoUrl().toString();
            }
        }

        sGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API).build();

        sProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        sLinearLayoutManager = new LinearLayoutManager(this);

        sFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

//        sSendButton = (Button) findViewById(R.id.sendButton);
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FriendlyMessage friendlyMessage = new
//                        FriendlyMessage(mMessageEditText.getText().toString(),
//                        mUsername,
//                        mPhotoUrl);
//                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
//                        .push().setValue(friendlyMessage);
//                mMessageEditText.setText("");
//            }
//        });

        sFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build();

        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        sFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        sFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        fetchConfig();

       // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        sAdView.loadAd(adRequest);



    }

    public void fetchConfig() {
        long cacheExpiration = 1000;

        if (sFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {

            cacheExpiration = 0;
        }
        sFirebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sFirebaseRemoteConfig.activateFetched();
                applyRetrievedLengthLimit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error fetching config: " + e.getMessage());
                applyRetrievedLengthLimit();

            }
        });
    }

    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length =
                sFirebaseRemoteConfig.getLong("friendly_msg_length");
        //sMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        if (sAdView != null) {
            sAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (sAdView != null) {
            sAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (sAdView != null) {
            sAdView.destroy();
        }
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.crash_menu:
//                FirebaseCrash.logcat(Log.ERROR, TAG, "crash caused");
//                causeCrash();
//                return true;
//            case R.id.invite_menu:
//                sendInvitation();
//                return true;
//
//            case R.id.sign_out_menu:
//                sFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(sGoogleApiClient);
//                sUsername = ANONYMOUS;
//                startActivity(new Intent(this, SignInActivity.class));
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }

    //used for app invites

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

//    private void sendInvitation() {
//        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
//                .setMessage(getString(R.string.invitation_message))
//                .setCallToActionText(getString(R.string.invitation_cta))
//                .build();
//        startActivityForResult(intent, REQUEST_INVITE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode,
                        data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "not sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                        payload);
                // Sending failed or it was canceled, show failure message to
                // the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }

    }


}
