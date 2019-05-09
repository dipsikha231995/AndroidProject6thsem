package com.example.applicationformcv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //////Navigational Drawer//////
    Advance3DDrawerLayout drawer;

    ///////language change//////
    private static TextView rev, gov;
    private static Button ins, deed, marr, viewStatus ;
    private static Locale myLocale;

    //Shared Preferences Variables
    private static final String Locale_Preference = "Locale Preference"; //name of the shared preference
    private static final String Locale_KeyValue = "Language key"; //this is the key which will have the value either en,hi
    private static final String ThemeKey = "Theme key"; //this is the key which will have the value either li,da

    private static SharedPreferences sharedPreferences;  //text file contains app settings in the form of key and value
    private static SharedPreferences.Editor editor;  //write in shared preference

    //////////////////////////

    public static String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////Navigation drawer////
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        drawer.useCustomBehavior(Gravity.END); //assign custom behavior for "Right" drawer

        // customisation of the drawer
        drawer.setViewRotation(Gravity.END, 15); // set degree of Y-rotation ( value : 0 -> 45)
        drawer.setViewScale(Gravity.END, 1f); //set height scale for main view (0f to 1f)
        drawer.setViewElevation(Gravity.END, 20); //set main view elevation when drawer open (dimension)
        drawer.setViewScrimColor(Gravity.END, Color.TRANSPARENT); //set drawer overlay (color)
        drawer.setDrawerElevation(Gravity.END, 20); //set drawer elevation (dimension)
        drawer.setRadius(Gravity.END, 25); //set end container's corner radius (dimension)

        // setting NavigationView menu item click listener
        NavigationView navigationView = findViewById(R.id.nav_view_notification);
        navigationView.setNavigationItemSelectedListener(this);
        /////////////////////////

        initViews();

        loadSettings();
    }

    //Navigational Drawer//
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

/////////////////////////////////////////////////
    private void initViews() {
        rev =  findViewById(R.id.revText);
        gov = findViewById(R.id.govText);
        ins = findViewById(R.id.button);
        deed = findViewById(R.id.button2);
        marr = findViewById(R.id.button3);
        viewStatus = findViewById(R.id.button4);
    }

    private void changeLanguage(String lang) {
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

    private void loadSettings() {
        sharedPreferences = getSharedPreferences(Locale_Preference, AppCompatActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String language = sharedPreferences.getString(Locale_KeyValue, "en"); //read the default language
        String theme = sharedPreferences.getString(ThemeKey, "li");           //read the default theme

        changeLanguage(language);
        changeTheme(theme);

    }

    private void changeTheme(String theme) {

        if (theme.equalsIgnoreCase("li")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (theme.equalsIgnoreCase("da")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
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


    private void saveTheme(String theme) {
        editor.putString(ThemeKey, theme);
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.settings_menu_item) {
            // open the right navigation drawer

            drawer.openDrawer(Gravity.END, true);
        }

//        String lang = "";
//        String theme = "";

//        switch (item.getItemId()) {
//
//            case R.id.myprofile_menu_item:
//                Intent intent = new Intent(this, myProfile.class);
//                startActivity(intent);
//                break;
//
//            case R.id.language_menu_item:
//                lang = "en";//Default Language
//                changeLanguage(lang);//Change Locale on selection basis
//                Toast.makeText(this, "You choosed English", Toast.LENGTH_SHORT).show();
//                break;
//
//
//            case R.id.language_menu_item2:
//                lang = "hi";
//                changeLanguage(lang);//Change Locale on selection basis
//                Toast.makeText(this, "You choosed Hindi", Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.theme_menu_item:
//                theme = "li";
//                saveTheme(theme);
//
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                startActivity(getIntent());
//                finish();
//                break;
//
//            case R.id.theme_menu_item2:
//                theme = "da";
//                saveTheme(theme);
//
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                startActivity(getIntent());
//                finish();
//                break;
//
//            case R.id.about_menu_item:
//                Intent aboutIntent = new Intent(this, AboutActivity.class);
//                startActivity(aboutIntent);
//                break;
//
//
//            case R.id.logout_menu_item:
//                // sign-out the user
//                AuthUI.getInstance()
//                        .signOut(this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
//                                finish();
//                            }
//                        });
//        }

        return true;
    }


    // handling navigation menu item clicks here...
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        String lang = "";
        String theme = "";

        switch (item.getItemId()) {

            case R.id.myprofile_menu_item:
                Intent intent = new Intent(this, myProfile.class);
                startActivity(intent);
                break;

            case R.id.language_menu_item:
                lang = "en";//Default Language
                changeLanguage(lang);//Change Locale on selection basis
                Toast.makeText(this, "You choosed English", Toast.LENGTH_SHORT).show();
                break;


            case R.id.language_menu_item2:
                lang = "hi";
                changeLanguage(lang);//Change Locale on selection basis
                Toast.makeText(this, "You choosed Hindi", Toast.LENGTH_SHORT).show();
                break;

            case R.id.theme_menu_item:
                theme = "li";
                saveTheme(theme);

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                startActivity(getIntent());
                finish();
                break;

            case R.id.theme_menu_item2:
                theme = "da";
                saveTheme(theme);

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                startActivity(getIntent());
                finish();
                break;

            case R.id.about_menu_item:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;


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
        }

        /////
//        int id = item.getItemId();
//
//        if (id == R.id.language_menu_item) {
//            Toast.makeText(this, "Change language", Toast.LENGTH_SHORT).show();
//        }
//        else if (id == R.id.theme_menu_item) {
//            Toast.makeText(this, "Change Theme", Toast.LENGTH_SHORT).show();
//        }
//        else if (id == R.id.nav_about) {
//            Toast.makeText(this, "About e-panjeeyan", Toast.LENGTH_SHORT).show();
//        }
//        else if (id == R.id.nav_help) {
//            Toast.makeText(this, "Help Dany!", Toast.LENGTH_SHORT).show();
//        }
//        else if (id == R.id.nav_logout) {
//            Toast.makeText(this, "Logout user", Toast.LENGTH_SHORT).show();
//        }

        drawer.closeDrawer(GravityCompat.END);
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