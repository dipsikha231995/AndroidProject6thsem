package com.example.applicationformcv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

public class MarriageRegistration extends AppCompatActivity {

    private static final String TAG = "MY_APP";

    EditText editText, email, mobile;

    Spinner mySpinner1, mySpinner2, mySpinner3;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marriage_registration);
        mySpinner1 = findViewById(R.id.spinner1);
        mySpinner2 = findViewById(R.id.spinner2);
        mySpinner3 = findViewById(R.id.spinner3);

        editText = findViewById(R.id.applicantName);
        email = findViewById(R.id.username);
        mobile = findViewById(R.id.number);

        //
        Intent intent = getIntent();
        String n = intent.getStringExtra("name");
        editText.setText(n);

        //create array adapter
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.desiredDate));
        myAdapter1.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner1.setAdapter(myAdapter1);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.marriageType));
        myAdapter2.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner2.setAdapter(myAdapter2);

        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.officeRegistration));
        myAdapter3.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner3.setAdapter(myAdapter3);

        //validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.applicantName, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.username, "(^$|^.*@.*\\..*$)", R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.number, "^[0-9]{2}[0-9]{8}$", R.string.mobileerror);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //
        String applicationName = editText.getText().toString();
        if (!applicationName.isEmpty()) {
            MainActivity.name = applicationName;
        }
    }

    private boolean validateSpinners() {

        boolean result = true;

        View selectedView = mySpinner1.getSelectedView();
        View selectedView2 = mySpinner2.getSelectedView();
        View selectedView3 = mySpinner3.getSelectedView();

        if (selectedView instanceof TextView) {

            TextView selectedTextView = (TextView) selectedView;

            if (selectedTextView.getText().toString().equalsIgnoreCase("Select Desired Appointment Date *")) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError("Choose an item");

                result = false;

            }

        }
        if (selectedView2 instanceof TextView) {

            TextView selectedTextView2 = (TextView) selectedView2;

            if (selectedTextView2.getText().toString().equalsIgnoreCase("Select Marriage Type *")) {
                selectedTextView2.setFocusable(true);
                selectedTextView2.setClickable(true);
                selectedTextView2.setFocusableInTouchMode(true);
                selectedTextView2.setError("Choose an item");

                result = false;

            }

        }
        if (selectedView3 instanceof TextView) {

            TextView selectedTextView3 = (TextView) selectedView3;

            if (selectedTextView3.getText().toString().equalsIgnoreCase("Select Office for Registration *")) {
                selectedTextView3.setFocusable(true);
                selectedTextView3.setClickable(true);
                selectedTextView3.setFocusableInTouchMode(true);
                selectedTextView3.setError("Choose an item");

                result = false;
            }

        }

        return result;

    }

    public void doNext(View view) {

        boolean a = awesomeValidation.validate();

        boolean b = validateSpinners();


        if (a && b) {
            Intent intentSubmit = new Intent(this, Bridedetails.class);
            startActivity(intentSubmit);

        }

    }
}
