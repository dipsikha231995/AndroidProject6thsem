package com.example.applicationformcv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 10;
    public static final String TAG = "MY-APP";
    public static final String KEY_APP_INTO = "app-intro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // do I show on-boarding activity or the main activity?
        // show the on-boarding activity during installation

//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        boolean appIntroShown = sharedPref.getBoolean(KEY_APP_INTO, false);
//
//        if (!appIntroShown) {
//            // show the app intro for once
//            // go to the on-boarding activity
//            // make the field as true
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putBoolean(KEY_APP_INTO, true);
//            editor.apply();
//
//            Intent intent = new Intent(getApplicationContext(), OnBoardingActivity.class);
//            startActivity(intent);
//            finish();
//
//        } else {
//            // route to either log-in or mainActivity
//            routeToAppropriateScreen();
//        }


        //routeToAppropriateScreen();

        startActivity(new Intent(this, OnBoardingActivity.class));
        finish();

    }

    private void routeToAppropriateScreen() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is already signed in
            // verify token at the backend

            signInUser();
        } else {
            // No user is signed in
            // Show the Firebase Auth UI

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build());

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.AppTheme)
                            .setLogo(R.drawable.goa)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                // verify token at the backend

                signInUser();
            } else {
                if (response != null) {
                    Toast.makeText(this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void signInUser() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}