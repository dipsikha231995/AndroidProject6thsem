package com.example.applicationformcv;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // version info
        Element versionElement = new Element();
        versionElement.setTitle(getResources().getString(R.string.version));
        versionElement.setIconDrawable(R.drawable.ic_version);

        // developer info
        Element developerElement = new Element();
        developerElement.setTitle(getString(R.string.developer));
        developerElement.setIconDrawable(R.drawable.ic_developer);


        // about page view
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.goa)
                .setDescription(getResources().getString(R.string.about_page_description))
                .addItem(versionElement)
                .addItem(developerElement)
                .addGroup(getString(R.string.connect_us_label))
                .addWebsite(getString(R.string.epanjeeyan_web_address))
                .create();

        setContentView(aboutPage);
        setTitle(R.string.title_about_epanjeeyan);

    }
}
