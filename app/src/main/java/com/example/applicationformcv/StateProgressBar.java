package com.example.applicationformcv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class StateProgressBar extends AppCompatActivity {

    LinearLayout linearLayout1, linearLayout2,linearLayout3, linearLayout4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_progress_bar);
        linearLayout1 = findViewById(R.id.marriageDetailsLayout);
        linearLayout2 = findViewById(R.id.brideLayout);
        linearLayout3 = findViewById(R.id.groomLayout);
        linearLayout4 = findViewById(R.id.summaryLayout);

    }



    public void doNext(View view) {
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.VISIBLE);

    }

    public void doNext2(View view) {
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.VISIBLE);
    }

    public void doNext3(View view) {
        linearLayout3.setVisibility(View.GONE);
        linearLayout4.setVisibility(View.VISIBLE);
    }
}
