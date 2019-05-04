package com.example.applicationformcv;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    ///////language change//////
    private static TextView rev, gov;
    private static Button ins, deed, marr, viewStatus ;
    private static Locale myLocale;

    //Shared Preferences Variables
    private static final String Locale_Preference = "Locale Preference";
    private static final String Locale_KeyValue = "Saved Locale";    //this is the key which will have the value either en,hi,as
    private static SharedPreferences sharedPreferences;  //text file contains app settings in the form of key and value
    private static SharedPreferences.Editor editor;  //write in shared preference

    //////////////////////////

    public static String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadLocale();
    }

/////////////////////////////////////////////////
    private void initViews() {
        sharedPreferences = getSharedPreferences(Locale_Preference, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        rev =  findViewById(R.id.revText);
        gov = findViewById(R.id.govText);
        ins = findViewById(R.id.button);
        deed = findViewById(R.id.button2);
        marr = findViewById(R.id.button3);
        viewStatus = findViewById(R.id.button4);
    }

    private void changeLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;

        myLocale = new Locale(lang);//Set Selected Locale
        saveLocale(lang);//Save the selected locale

        //actual language changing is done here
        Locale.setDefault(myLocale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = myLocale;//set config locale as selected locale
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());//Update the config

        updateTexts();//Update texts according to locale
    }

    private void saveLocale(String lang) {
        editor.putString(Locale_KeyValue, lang);
        editor.commit();
    }

    private void loadLocale() {
        String language = sharedPreferences.getString(Locale_KeyValue, "en");
        changeLocale(language);
    }

    private void updateTexts() {
        rev.setText(R.string.rev);
        gov.setText(R.string.gov);
        ins.setText(R.string.ins);
        deed.setText(R.string.deed);
        marr.setText(R.string.marr);
        viewStatus.setText(R.string.view);
    }

///////////////////////////////////////////////////////




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String lang = "";

        switch (item.getItemId()) {

            case R.id.myprofile_menu_item:
                Intent intent = new Intent(this, myProfile.class);
                startActivity(intent);
                break;

            case R.id.language_menu_item:
                lang = "en";//Default Language
                Toast.makeText(this, "You choosed English", Toast.LENGTH_SHORT).show();
                break;


            case R.id.language_menu_item2:
                lang = "hi";
                Toast.makeText(this, "You choosed Hindi", Toast.LENGTH_SHORT).show();
                break;

            case R.id.theme_menu_item:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                startActivity(getIntent());
                finish();


                // recreate the activity
                //this.recreate();
                break;

            case R.id.theme_menu_item2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                // recreate the activity
                //this.recreate();
                startActivity(getIntent());
                finish();

                break;

//            case R.id.about_menu_item:
//                break;


            case R.id.logout_menu_item:
                // sign-out the user
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                finish();
                            }
                        });
                return true;
        }



        changeLocale(lang);//Change Locale on selection basis

        return true;
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
        Intent intent4 = new Intent(this,MakeAssessmentFee.class);
        startActivity(intent4);
    }
}