package com.example.applicationformcv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AhoyOnboarderActivity {

    private static final String TAG = "MY-APP";

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
        showNavigationControls(true);
        setGradientBackground();

        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));

        setOnboardPages(pages);

    }

    public void onFinishButtonPressed() {
        Intent gotoMain = new Intent(this, MainActivity.class);
        startActivity(gotoMain);
        finish();
    }
}
