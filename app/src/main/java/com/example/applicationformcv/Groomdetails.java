package com.example.applicationformcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;

import androidx.appcompat.app.AppCompatActivity;

public class Groomdetails extends AppCompatActivity {

    EditText eCity, ePolice, ePost, eDistrict, eState, ePin, sameCity, samePolice, samePost, sameDistrict, sameState, samePin;
    CheckBox cBox2;

    EditText name, age, occupation, fName;
    Spinner mySpinner1;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groomdetails);

        mySpinner1 = findViewById(R.id.spinner1);

        eCity = findViewById(R.id.city);
        ePolice = findViewById(R.id.policeStation);
        ePost = findViewById(R.id.postofficeName);
        eDistrict = findViewById(R.id.districtName);
        eState = findViewById(R.id.stateName);
        ePin = findViewById(R.id.pin);

        cBox2 = findViewById(R.id.groomcheckbox);

        sameCity = findViewById(R.id.per_city);
        samePolice = findViewById(R.id.per_policeStation);
        samePost = findViewById(R.id.per_postofficeName);
        sameDistrict = findViewById(R.id.per_districtName);
        sameState = findViewById(R.id.per_stateName);
        samePin = findViewById(R.id.per_pin);

        //create array adapter
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.maritalStatus));
        myAdapter1.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner1.setAdapter(myAdapter1);

        //same as above checkbox
        cBox2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cBox2.isChecked()) {

                    String ename1 = eCity.getText().toString();
                    sameCity.setText(ename1);
                    sameCity.setError(null);

                    String ename2 = ePolice.getText().toString();
                    samePolice.setText(ename2);
                    samePolice.setError(null);

                    String ename3 = ePost.getText().toString();
                    samePost.setText(ename3);
                    samePost.setError(null);

                    String ename4 = eDistrict.getText().toString();
                    sameDistrict.setText(ename4);
                    sameDistrict.setError(null);

                    String ename5 = eState.getText().toString();
                    sameState.setText(ename5);
                    sameState.setError(null);

                    String ename6 = ePin.getText().toString();
                    samePin.setText(ename6);
                    samePin.setError(null);


                }
                else {
                    sameCity.setText("");

                    samePolice.setText("");

                    samePost.setText("");

                    sameDistrict.setText("");

                    sameState.setText("");

                    samePin.setText("");
                }
            }
        });

        //validation
//        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
//
//        //adding validation to edittexts
//        awesomeValidation.addValidation(this, R.id.Name, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
//        awesomeValidation.addValidation(this, R.id.Age, Range.closed(21, 60), R.string.ageGroomerr);
//        awesomeValidation.addValidation(this, R.id.Occupation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.occerror);
//        awesomeValidation.addValidation(this, R.id.FName, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.fNameerror);
//
//        awesomeValidation.addValidation(this, R.id.city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
//        awesomeValidation.addValidation(this, R.id.policeStation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
//        awesomeValidation.addValidation(this, R.id.postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
//        awesomeValidation.addValidation(this, R.id.districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
//        awesomeValidation.addValidation(this, R.id.stateName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
//        awesomeValidation.addValidation(this, R.id.pin, "(\\d{6})", R.string.pinerror);
//        awesomeValidation.addValidation(this, R.id.lengthResidence, "(\\d+)", R.string.lengtherror);
//
//        awesomeValidation.addValidation(this, R.id.per_city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
//        awesomeValidation.addValidation(this, R.id.per_policeStation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
//        awesomeValidation.addValidation(this, R.id.per_postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
//        awesomeValidation.addValidation(this, R.id.per_districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
//        awesomeValidation.addValidation(this, R.id.per_stateName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
//        awesomeValidation.addValidation(this, R.id.per_pin, "(\\d{6})", R.string.pinerror);

    }


    private boolean validateSpinners() {

        boolean result = true;

        View selectedView = mySpinner1.getSelectedView();

        if (selectedView instanceof TextView) {

            TextView selectedTextView = (TextView) selectedView;

            if (selectedTextView.getText().toString().equalsIgnoreCase("Select Marital Status *")) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError("Choose an item");

                result = false;

            }

        }

        return result;
    }


    public void doNext(View view) {

        boolean a = awesomeValidation.validate();

        boolean b = validateSpinners();

        if (a && b) {
            Intent next2 = new Intent(this, AppointmentConfirmed.class);
            startActivity(next2);
        }
    }
}
