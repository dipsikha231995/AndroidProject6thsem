package com.example.applicationformcv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnBoardingActivity extends AhoyOnboarderActivity {

    private static final String TAG = "MY-APP";
    private static final int RC_SIGN_IN = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Error", "Checking the working in OnBoard");

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("e-Panjeeyan", "An android app for Revenue & Disaster Management, Govt of Assam.", R.drawable.goablue2);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Make registration", "Registration system which enables online deed registration and marriage registration.", R.drawable.iconregister);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Fee assessment", "Calculate the fee for registration.", R.drawable.fee);
        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard("View Status", "Check your appointment status after registration.", R.drawable.viewstatusicon);

        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard4.setBackgroundColor(R.color.black_transparent);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.grey_200);
            // page.setTitleTextSize(dpToPixels(8, this));
            // page.setDescriptionTextSize(dpToPixels(5, this));
            // page.setIconLayoutParams(400, 400, 50, 20,20, 30);
        }

        setFinishButtonTitle("Finish");
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
        showNavigationControls(true);
        setGradientBackground();

        setOnboardPages(pages);

    }

    public void onFinishButtonPressed() {
//        Intent gotoMain = new Intent(this, MainActivity.class);
//        startActivity(gotoMain);
//        finish();

        routeToAppropriateScreen();
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
