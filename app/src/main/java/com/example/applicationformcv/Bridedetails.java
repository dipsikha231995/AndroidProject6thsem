package com.example.applicationformcv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.common.collect.Range;

public class Bridedetails extends AppCompatActivity {

    EditText eCity, ePolice, ePost, eDistrict, eState, ePin, sameCity, samePolice, samePost, sameDistrict, sameState, samePin;
    CheckBox cBox;

    EditText name, age, occupation, fName;
    Spinner mySpinner1;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridedetails);
        mySpinner1 = findViewById(R.id.spinner1);

        eCity = findViewById(R.id.city);
        ePolice = findViewById(R.id.policeStation);
        ePost = findViewById(R.id.postofficeName);
        eDistrict = findViewById(R.id.districtName);
        eState = findViewById(R.id.stateName);
        ePin = findViewById(R.id.pin);

        cBox = findViewById(R.id.checkbox);

        sameCity = findViewById(R.id.per_city);
        samePolice = findViewById(R.id.per_policeStation);
        samePost = findViewById(R.id.per_postofficeName);
        sameDistrict = findViewById(R.id.per_districtName);
        sameState = findViewById(R.id.per_stateName);
        samePin = findViewById(R.id.per_pin);

        name = findViewById(R.id.Name);
        age = findViewById(R.id.Age);
        occupation = findViewById(R.id.Occupation);
        fName = findViewById(R.id.FName);


        //create array adapter
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.maritalStatus));
        myAdapter1.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner1.setAdapter(myAdapter1);

        //same as above checkbox
        cBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cBox.isChecked()) {

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


                } else {
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
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.Name, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.Age, Range.closed(18, 60), R.string.ageerror);
        awesomeValidation.addValidation(this, R.id.Occupation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.occerror);
        awesomeValidation.addValidation(this, R.id.FName, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.fNameerror);

        awesomeValidation.addValidation(this, R.id.city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
        awesomeValidation.addValidation(this, R.id.policeStation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
        awesomeValidation.addValidation(this, R.id.postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
        awesomeValidation.addValidation(this, R.id.districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
        awesomeValidation.addValidation(this, R.id.stateName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
        awesomeValidation.addValidation(this, R.id.pin, "(\\d{6})", R.string.pinerror);
        awesomeValidation.addValidation(this, R.id.lengthResidence, "(\\d+)", R.string.lengtherror);

        awesomeValidation.addValidation(this, R.id.per_city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
        awesomeValidation.addValidation(this, R.id.per_policeStation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
        awesomeValidation.addValidation(this, R.id.per_postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
        awesomeValidation.addValidation(this, R.id.per_districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
        awesomeValidation.addValidation(this, R.id.per_stateName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
        awesomeValidation.addValidation(this, R.id.per_pin, "(\\d{6})", R.string.pinerror);

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

            Intent next2 = new Intent(this, Groomdetails.class);
            startActivity(next2);

        }
    }

}
