package com.example.applicationformcv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class myProfile extends AppCompatActivity {

    CardView myprofile, myprofiledetails, update, headingCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        myprofile = findViewById(R.id.myprofile);
        myprofiledetails = findViewById(R.id.myprofileDetails);
        update = findViewById(R.id.myprofileEdit2);
        headingCard = findViewById(R.id.cardEdit);
    }

    public void editProfile(View view) {

        myprofile.setVisibility(View.GONE);
        myprofiledetails.setVisibility(View.GONE);
        update.setVisibility(View.VISIBLE);
        headingCard.setVisibility(View.VISIBLE);

    }
}
