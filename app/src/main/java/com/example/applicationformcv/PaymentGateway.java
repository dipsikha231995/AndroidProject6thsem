package com.example.applicationformcv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.Wave;

import java.nio.charset.StandardCharsets;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentGateway extends AppCompatActivity {

    AlertDialog alertDialog;

    private static final String TAG = "MY-APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);

        //Loading spin kit
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_loading_dialog, null);
        ProgressBar progressBar = view.findViewById(R.id.spin_kit);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);

        alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create();

        String url = getIntent().getStringExtra("url");
        String data = getIntent().getStringExtra("bundle");


        Log.d(TAG, data);


        WebView webView = findViewById(R.id.my_web_view);

        // configure settings
        webView.setWebChromeClient(new WebChromeClient());          // So that any pop-ups/alerts get displayed

        webView.setWebViewClient(new WebViewClient() {              // to show and hide progress dialog
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                // Display the progress dialog
                alertDialog.show();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //  close progress dialog
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // Enable responsive layout
        webView.getSettings().setUseWideViewPort(true);
        // Zoom out if the content width is greater than the width of the viewport
        webView.getSettings().setLoadWithOverviewMode(true);


        // zoom control settings
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
        webView.getSettings().setDisplayZoomControls(false); // disable the default zoom controls on the page

        // post to the url
        try {
            webView.postUrl(url, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
