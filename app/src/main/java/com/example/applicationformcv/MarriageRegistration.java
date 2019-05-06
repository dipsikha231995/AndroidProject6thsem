package com.example.applicationformcv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;

import org.json.JSONArray;

import java.util.ArrayList;

public class MarriageRegistration extends AppCompatActivity {

    private static final String TAG = "MY_APP";

    // Marriage details form fields
    EditText editText, email, mobile;
    Spinner mySpinner1, mySpinner2, mySpinner3;

//    Spinner mySpinnerDate;

    // Bride details form fields
    EditText eCity, ePolice, ePost, eDistrict, eState, ePin, sameCity, samePolice, samePost, sameDistrict, sameState, samePin;
    CheckBox cBox;
    EditText name, age, occupation, fName;
    Spinner mySpinner4;

    // Groom details form fields
    EditText eCity2, ePolice2, ePost2, eDistrict2, eState2, ePin2, sameCity2, samePolice2, samePost2, sameDistrict2, sameState2, samePin2;
    CheckBox cBox2;
    EditText name2, age2, occupation2, fName2;
    Spinner mySpinner5;

    CardView cardView;


    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;
    private AwesomeValidation awesomeValidation2;
    private AwesomeValidation awesomeValidation3;

    StateProgressBar stateProgressBar;

    // all the four form layouts
    ViewGroup marriageDetailsForm;
    ViewGroup brideDetailsForm;
    ViewGroup groomDetailsForm;
    ViewGroup confirmForm;

    private boolean isMarriageFormCompleted = false;
    private boolean isBrideFormCompleted = false;
    private boolean isGroomFormCompleted = false;


    // the header textView
    TextView header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marriage_registration);

        // form layouts
        marriageDetailsForm = findViewById(R.id.marriage_form);
        brideDetailsForm = findViewById(R.id.bride_form);
        groomDetailsForm = findViewById(R.id.groom_form);
        confirmForm = findViewById(R.id.summary_form);
        header = findViewById(R.id.header_textView);

        stateProgressBar = findViewById(R.id.state_progress_bar);
        // state progressbar description
        String[] descriptionData = {getString(R.string.marriageDet), getString(R.string.brideDet),
                                    getString(R.string.groomDet), getString(R.string.confirm)};

        stateProgressBar.setStateDescriptionData(descriptionData);
        stateProgressBar.setOnStateItemClickListener(new OnStateItemClickListener() {
            @Override
            public void onStateItemClick(StateProgressBar stateProgressBar, StateItem stateItem,
                                         int stateNumber, boolean isCurrentState) {

                if (!isCurrentState) {
                    showFrom(stateProgressBar.getCurrentStateNumber(), stateNumber);
                }
            }
        });


        // marriage details views
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mySpinner1 = findViewById(R.id.spinner11);
        mySpinner2 = findViewById(R.id.spinner2);
        mySpinner3 = findViewById(R.id.spinner3);
        editText = findViewById(R.id.applicantName);
        email = findViewById(R.id.username);
        mobile = findViewById(R.id.number);

        cardView = findViewById(R.id.card2);

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

//        //adding validation to edittexts
//        awesomeValidation.addValidation(this, R.id.applicantName, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
//        awesomeValidation.addValidation(this, R.id.username, "(^$|^.*@.*\\..*$)", R.string.emailerror);
//        awesomeValidation.addValidation(this, R.id.number, "^[0-9]{2}[0-9]{8}$", R.string.mobileerror);



        // bride details views
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mySpinner4 = findViewById(R.id.spinner1);
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
        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.maritalStatus));
        myAdapter4.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner4.setAdapter(myAdapter4);

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

        //validation for bride details
        awesomeValidation2 = new AwesomeValidation(ValidationStyle.BASIC);

//        //adding validation to editTexts
//        awesomeValidation2.addValidation(this, R.id.Name, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
//        awesomeValidation2.addValidation(this, R.id.Age, Range.closed(18, 60), R.string.ageerror);
//        awesomeValidation2.addValidation(this, R.id.Occupation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.occerror);
//        awesomeValidation2.addValidation(this, R.id.FName, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.fNameerror);
//
//        awesomeValidation2.addValidation(this, R.id.city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
//        awesomeValidation2.addValidation(this, R.id.policeStation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
//        awesomeValidation2.addValidation(this, R.id.postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
//        awesomeValidation2.addValidation(this, R.id.districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
//        awesomeValidation2.addValidation(this, R.id.stateName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
//        awesomeValidation2.addValidation(this, R.id.pin, "(\\d{6})", R.string.pinerror);
//        awesomeValidation2.addValidation(this, R.id.lengthResidence, "(\\d+)", R.string.lengtherror);
//
//        awesomeValidation2.addValidation(this, R.id.per_city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
//        awesomeValidation2.addValidation(this, R.id.per_policeStation, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
//        awesomeValidation2.addValidation(this, R.id.per_postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
//        awesomeValidation2.addValidation(this, R.id.per_districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
//        awesomeValidation2.addValidation(this, R.id.per_stateName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
//        awesomeValidation2.addValidation(this, R.id.per_pin, "(\\d{6})", R.string.pinerror);



        // groom details views
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        mySpinner5 = findViewById(R.id.spinner12);

        eCity2 = findViewById(R.id.city2);
        ePolice2 = findViewById(R.id.policeStation2);
        ePost2 = findViewById(R.id.postofficeName2);
        eDistrict2 = findViewById(R.id.districtName2);
        eState2 = findViewById(R.id.stateName2);
        ePin2 = findViewById(R.id.pin2);
        cBox2 = findViewById(R.id.groom_checkbox);
        sameCity2 = findViewById(R.id.per_city2);
        samePolice2 = findViewById(R.id.per_policeStation2);
        samePost2 = findViewById(R.id.per_postofficeName2);
        sameDistrict2 = findViewById(R.id.per_districtName2);
        sameState2 = findViewById(R.id.per_stateName2);
        samePin2 = findViewById(R.id.per_pin2);
        name2 = findViewById(R.id.Name2);
        age2 = findViewById(R.id.Age2);
        occupation2 = findViewById(R.id.Occupation2);
        fName2 = findViewById(R.id.FName2);

        mySpinner5.setAdapter(myAdapter4);

        //same as above checkbox
        cBox2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cBox2.isChecked()) {

                    String ename1 = eCity2.getText().toString();
                    sameCity2.setText(ename1);
                    sameCity2.setError(null);

                    String ename2 = ePolice2.getText().toString();
                    samePolice2.setText(ename2);
                    samePolice2.setError(null);

                    String ename3 = ePost2.getText().toString();
                    samePost2.setText(ename3);
                    samePost2.setError(null);

                    String ename4 = eDistrict2.getText().toString();
                    sameDistrict2.setText(ename4);
                    sameDistrict2.setError(null);

                    String ename5 = eState2.getText().toString();
                    sameState2.setText(ename5);
                    sameState2.setError(null);

                    String ename6 = ePin2.getText().toString();
                    samePin2.setText(ename6);
                    samePin2.setError(null);


                }
                else {
                    sameCity2.setText("");

                    samePolice2.setText("");

                    samePost2.setText("");

                    sameDistrict2.setText("");

                    sameState2.setText("");

                    samePin2.setText("");
                }
            }
        });

        //validation
        awesomeValidation3 = new AwesomeValidation(ValidationStyle.BASIC);

//        //adding validation to editTexts
//        awesomeValidation3.addValidation(this, R.id.Name2, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
//        awesomeValidation3.addValidation(this, R.id.Age2, Range.closed(21, 60), R.string.ageGroomerr);
//        awesomeValidation3.addValidation(this, R.id.Occupation2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.occerror);
//        awesomeValidation3.addValidation(this, R.id.FName2, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.fNameerror);
//
//        awesomeValidation3.addValidation(this, R.id.city2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
//        awesomeValidation3.addValidation(this, R.id.policeStation2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
//        awesomeValidation3.addValidation(this, R.id.postofficeName2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
//        awesomeValidation3.addValidation(this, R.id.districtName2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
//        awesomeValidation3.addValidation(this, R.id.stateName2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
//        awesomeValidation3.addValidation(this, R.id.pin2, "(\\d{6})", R.string.pinerror);
//        awesomeValidation3.addValidation(this, R.id.lengthResidence2, "(\\d+)", R.string.lengtherror);
//
//        awesomeValidation3.addValidation(this, R.id.per_city2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
//        awesomeValidation3.addValidation(this, R.id.per_policeStation2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.policeerror);
//        awesomeValidation3.addValidation(this, R.id.per_postofficeName2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
//        awesomeValidation3.addValidation(this, R.id.per_districtName2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
//        awesomeValidation3.addValidation(this, R.id.per_stateName2, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.stateerror);
//        awesomeValidation3.addValidation(this, R.id.per_pin2, "(\\d{6})", R.string.pinerror);

        //populate date spinner
        getDates();

    }

    private void getDates() {

        final String url = "http://192.168.43.210:8080/mvcbook/getdates";

        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add((getString(R.string.dateSpinner)));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //response from servlet is kept in the JSONArray array
                            JSONArray array = new JSONArray(response);

                            //array list is populated from JSON array
                            for (int i = 0; i < array.length(); i++) {
                                arrayList.add(array.getString(i));
                            }

                            setUpAppointmentDateSpinner(arrayList);

                        } catch (Exception e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void setUpAppointmentDateSpinner(ArrayList<String> dataSource) {

        ArrayAdapter myDateAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour, dataSource);
        myDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner1.setAdapter(myDateAdapter);
    }


    private void showFrom(int curState, int nextState) {

        Log.d(TAG, "" + curState + " : " + nextState);


        // hide the form associates with "curState"
        switch (curState) {
            case 1:

                if (!validateMarriageDetails()) {
                    return;
                }
                else if (nextState == 4 && !completeAllStages()) {
                    return;
                }

                marriageDetailsForm.setVisibility(View.GONE);
                break;

            case 2:

                if (curState > nextState) {
                    brideDetailsForm.setVisibility(View.GONE);
                }
                else if (!validateBrideDetails()) {
                    return;
                }
                else if (nextState == 4 && !completeAllStages()) {
                    return;
                }
                else {
                    brideDetailsForm.setVisibility(View.GONE);
                }

                break;

            case 3:

                if (curState > nextState) {
                    groomDetailsForm.setVisibility(View.GONE);
                }
                else if (!validateGroomDetails()) {
                    return;
                }
                else if (nextState == 4 && !completeAllStages()) {
                    return;
                }
                else {
                    groomDetailsForm.setVisibility(View.GONE);
                }

                break;

            case 4:
                confirmForm.setVisibility(View.GONE);
                break;
        }

        // show the from associates with "nextState"
        // Change the heading
        // Change the current state number
        switch (nextState) {
            case 1:
                marriageDetailsForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                header.setText(R.string.marriage);
                break;

            case 2:
                brideDetailsForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                header.setText(R.string.bride);
                break;

            case 3:
                groomDetailsForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                header.setText(R.string.groom);
                break;

            case 4:
                confirmForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                header.setText(R.string.confirmheading);
                break;
        }
    }


    private boolean completeAllStages() {
        // if all the previous stages are completed, then only
        if (!isMarriageFormCompleted || !isBrideFormCompleted || !isGroomFormCompleted) {
            Toast.makeText(this, "Complete all the previous stages first", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }


    private boolean validateSpinners() {

        boolean result = true;

        View selectedView = mySpinner1.getSelectedView();
        View selectedView2 = mySpinner2.getSelectedView();
        View selectedView3 = mySpinner3.getSelectedView();

        if (selectedView instanceof TextView) {

            TextView selectedTextView = (TextView) selectedView;

            if ((selectedTextView.getText().toString().equalsIgnoreCase("Select Appointment Date *")) ||
                    (selectedTextView.getText().toString().equalsIgnoreCase("अपॉइंटमेंट तिथि का चयन करें *"))) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError(getString(R.string.spinnerError));

                result = false;

                Log.d(TAG, "spinner 1");

            }

        }
        if (selectedView2 instanceof TextView) {

            TextView selectedTextView2 = (TextView) selectedView2;

            if (selectedTextView2.getText().toString().equalsIgnoreCase("Select Marriage Type *")) {
                selectedTextView2.setFocusable(true);
                selectedTextView2.setClickable(true);
                selectedTextView2.setFocusableInTouchMode(true);
                selectedTextView2.setError(getString(R.string.spinnerError));

                result = false;

                Log.d(TAG, "spinner 2");

            }

        }
        if (selectedView3 instanceof TextView) {

            TextView selectedTextView3 = (TextView) selectedView3;

            if (selectedTextView3.getText().toString().equalsIgnoreCase("Select Office for Registration *")) {
                selectedTextView3.setFocusable(true);
                selectedTextView3.setClickable(true);
                selectedTextView3.setFocusableInTouchMode(true);
                selectedTextView3.setError(getString(R.string.spinnerError));

                result = false;

                Log.d(TAG, "spinner 3");
            }

        }

        Log.d(TAG, "result of spinner val: " + result);

        return result;
    }




    /////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showNextForm(View view) {
        // get the current state number
        int curState = stateProgressBar.getCurrentStateNumber();

        showFrom(curState, curState+1);
    }


    private boolean validateGroomDetails() {
        boolean a = awesomeValidation3.validate();
        boolean b = true;

        View selectedView = mySpinner5.getSelectedView();

        if (selectedView instanceof TextView) {

            TextView selectedTextView = (TextView) selectedView;

            if (selectedTextView.getText().toString().equalsIgnoreCase("Select Marital Status *")) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError(getString(R.string.spinnerError));

                b = false;
            }
        }

        if (a && b) {
            isGroomFormCompleted = true;
            return true;
        }
        else {
            return false;
        }
    }


    private boolean validateBrideDetails() {
        boolean a = awesomeValidation2.validate();
        boolean b = true;

        View selectedView = mySpinner4.getSelectedView();

        if (selectedView instanceof TextView) {

            TextView selectedTextView = (TextView) selectedView;

            if (selectedTextView.getText().toString().equalsIgnoreCase("Select Marital Status *")) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError(getString(R.string.spinnerError));

                b = false;
            }
        }


        if (a && b) {
            isBrideFormCompleted = true;
            return true;
        }
        else {
            return false;
        }
    }



    private boolean validateMarriageDetails() {
        boolean a = awesomeValidation.validate();
        boolean b = validateSpinners();

        Log.d(TAG, "awesome: " + a + " spinners: " + b);


        if (a && b) {
            isMarriageFormCompleted = true;
            return true;
        }
        else {
            return false;
        }
    }


    public void submitData(View view) {
        Toast.makeText(this, "data submitted", Toast.LENGTH_SHORT).show();

        confirmForm.setVisibility(View.GONE);
        header.setVisibility(View.GONE);

        // mark all the states "done"
        stateProgressBar.setAllStatesCompleted(true);

        // make them unclickable
        stateProgressBar.setOnStateItemClickListener(null);

        cardView.setVisibility(View.GONE);

        // network call to write info in the database and then payment activity is started

        Intent intent = new Intent(this,PaymentActivity.class);
        startActivity(intent);
        finish();
    }
}
