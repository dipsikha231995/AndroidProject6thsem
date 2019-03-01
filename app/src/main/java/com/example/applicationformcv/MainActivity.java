package com.example.applicationformcv;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //floating hint
        //TextInputLayout usernameWrapper = findViewById(R.id.usernameWrapper);
        //TextInputLayout passwordWrapper = findViewById(R.id.passwordWrapper);

       // usernameWrapper.setHint("Username");
        //passwordWrapper.setHint("Password");

        //For icon in the action bar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.mipmap.badge);
    }

    public void doDeedRegister(View view) {
        Intent intent1 = new Intent(this,DeedRegistration.class);
        startActivity(intent1);
    }

    public void doMarriageRegistration(View view) {
        Intent intent2 = new Intent(this,MarriageRegistration.class);
        intent2.putExtra("name", name);
        startActivity(intent2);
    }

    public void doViewStaus(View view) {
        Intent intent3 = new Intent(this,ViewStatus.class);
        startActivity(intent3);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MY_APP", "dead M");

        // destroy
        name = "";
    }

    public void combineForm(View view) {
        Intent intent4 = new Intent(this,StateProgressBar.class);
        startActivity(intent4);
    }
}