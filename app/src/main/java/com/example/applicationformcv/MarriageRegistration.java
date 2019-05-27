package com.example.applicationformcv;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarriageRegistration extends AppCompatActivity {

    Map<String, String> params;

    AlertDialog alertDialog;

    CookieBar.Builder cookieBar;

    private static final String TAG = "MY_APP";

    // Marriage details form fields
    EditText applicantName, email, mobile;
    Spinner mySpinner1, mySpinner2, mySpinner3;

    // Bride details form fields
    EditText eCity, ePolice, ePost, eDistrict, eState, ePin, perCity, perPolice, perPost, perDistrict, perState, perPin, lengthOfResidence;
    CheckBox cBox;
    EditText name, age, occupation, fName;
    Spinner mySpinner4;

    // Groom details form fields
    EditText eCity2, ePolice2, ePost2, eDistrict2, eState2, ePin2, perCity2, perPolice2, perPost2, perDistrict2, perState2, perPin2, lengthOfResidence2;
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

    List<RegistrationOfficeModel> officeList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marriage_registration);

        params = new HashMap<>();

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
        alertDialog.show();

        //No internet coonection error
        cookieBar = CookieBar.build(MarriageRegistration.this)
                .setTitle("Network Error")
                .setTitleColor(android.R.color.white)
                .setBackgroundColor(R.color.colorPrimary)
                .setIcon(R.drawable.ic_icon)
                .setEnableAutoDismiss(true)
                .setCookiePosition(CookieBar.TOP)
                .setSwipeToDismiss(true);

        setUpOfficeSpinner();

        setTitle(getString(R.string.marr));
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
        mySpinner1 = findViewById(R.id.spinner11);    //appointment dates
        mySpinner2 = findViewById(R.id.spinner2);     //marriage type
        mySpinner3 = findViewById(R.id.spinner3);     //sro offices
        applicantName = findViewById(R.id.applicantName);
        email = findViewById(R.id.username);
        mobile = findViewById(R.id.number);

        cardView = findViewById(R.id.card2);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.marriageType));
        myAdapter2.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner2.setAdapter(myAdapter2);


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
        lengthOfResidence = findViewById(R.id.lengthResidence);
        cBox = findViewById(R.id.checkbox);
        perCity = findViewById(R.id.per_city);
        perPolice = findViewById(R.id.per_policeStation);
        perPost = findViewById(R.id.per_postofficeName);
        perDistrict = findViewById(R.id.per_districtName);
        perState = findViewById(R.id.per_stateName);
        perPin = findViewById(R.id.per_pin);
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

                    Log.d(TAG, "onClick: " + cBox.isChecked());

                    String ename1 = eCity.getText().toString();
                    perCity.setText(ename1);
                    perCity.setError(null);

                    String ename2 = ePolice.getText().toString();
                    perPolice.setText(ename2);
                    perPolice.setError(null);

                    String ename3 = ePost.getText().toString();
                    perPost.setText(ename3);
                    perPost.setError(null);

                    String ename4 = eDistrict.getText().toString();
                    perDistrict.setText(ename4);
                    perDistrict.setError(null);

                    String ename5 = eState.getText().toString();
                    perState.setText(ename5);
                    perState.setError(null);

                    String ename6 = ePin.getText().toString();
                    perPin.setText(ename6);
                    perPin.setError(null);

                } else {

                    perCity.setText("");

                    perPolice.setText("");

                    perPost.setText("");

                    perDistrict.setText("");

                    perState.setText("");

                    perPin.setText("");
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
        lengthOfResidence2 = findViewById(R.id.lengthResidence2);
        cBox2 = findViewById(R.id.groom_checkbox);
        perCity2 = findViewById(R.id.per_city2);
        perPolice2 = findViewById(R.id.per_policeStation2);
        perPost2 = findViewById(R.id.per_postofficeName2);
        perDistrict2 = findViewById(R.id.per_districtName2);
        perState2 = findViewById(R.id.per_stateName2);
        perPin2 = findViewById(R.id.per_pin2);
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
                    perCity2.setText(ename1);
                    perCity2.setError(null);

                    String ename2 = ePolice2.getText().toString();
                    perPolice2.setText(ename2);
                    perPolice2.setError(null);

                    String ename3 = ePost2.getText().toString();
                    perPost2.setText(ename3);
                    perPost2.setError(null);

                    String ename4 = eDistrict2.getText().toString();
                    perDistrict2.setText(ename4);
                    perDistrict2.setError(null);

                    String ename5 = eState2.getText().toString();
                    perState2.setText(ename5);
                    perState2.setError(null);

                    String ename6 = ePin2.getText().toString();
                    perPin2.setText(ename6);
                    perPin2.setError(null);


                } else {

                    perCity2.setText("");

                    perPolice2.setText("");

                    perPost2.setText("");

                    perDistrict2.setText("");

                    perState2.setText("");

                    perPin2.setText("");
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


        mySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position).toString());
                params.put("appointment_date", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                params.put("MarriageType", parent.getItemAtPosition(position).toString());
                Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    RegistrationOfficeModel obj = officeList.get(position - 1); //getting the model from the list & storing in obj
                    int code = obj.getSroCode(); //storing the code in variable code

                    params.put("sro_office", String.valueOf(code));

                    Log.d(TAG, "onItemSelected: " + code);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                params.put("BrideMarriageCondition", parent.getItemAtPosition(position).toString());
                Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                params.put("GroomMarriageCondition", parent.getItemAtPosition(position).toString());
                Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setUpOfficeSpinner() {
        final String url = "http://192.168.43.210:8080/e-Panjeeyan/registrationoffice";

        // get the registration office from the database
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        Type collectionType = new TypeToken<List<RegistrationOfficeModel>>() {
                        }.getType();
                        officeList = new Gson().fromJson(response, collectionType);

                        ArrayList<String> list = new ArrayList<>();
                        list.add(getString(R.string.sroSpinner));

                        //array list is populated from JSON array
                        for (int i = 0; i < officeList.size(); i++) {
                            list.add(officeList.get(i).getOfficeName());
                        }

                        ArrayAdapter myTypeAdapter = new ArrayAdapter<String>(MarriageRegistration.this,
                                R.layout.spinner_item_text_colour, list);

                        myTypeAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                        mySpinner3.setAdapter(myTypeAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage(getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }

    private void getDates() {

        final String url = "http://192.168.43.210:8080/panjeeyanonline/getmarriagedates";
        final ArrayList<String> dateList = new ArrayList<>();

        // get the holidays from the database
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.d(TAG, "onResponse: " + response);

                            JSONObject object = new JSONObject(response);              //response string to object

                            if (object.getBoolean("success")) {
                                JSONArray array = object.getJSONArray("dates");  //json array dates:["25-05-2019","26-05-2019"]

                                //empty list is declared which will hold the json array items
                                List<String> dateList = new ArrayList<>();
                                dateList.add(getString(R.string.dateSpinner));

                                //array list is populated from JSON array
                                for (int i = 0; i < array.length(); i++) {
                                    dateList.add(array.getString(i));
                                }


                                ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                                        MarriageRegistration.this,
                                        R.layout.spinner_item_text_colour,
                                        dateList
                                );
                                dateAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                                mySpinner1.setAdapter(dateAdapter);
                            } else {
                                String msg = object.getString("msg");
                                showErrorMessage(msg);
                            }

                        } catch (Exception e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage(getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }


    private void showFrom(int curState, int nextState) {

        Log.d(TAG, "" + curState + " : " + nextState);


        // hide the form associates with "curState"
        switch (curState) {
            case 1:

                if (!validateMarriageDetails()) {
                    return;
                } else if (nextState == 4 && !completeAllStages()) {
                    return;
                }

                marriageDetailsForm.setVisibility(View.GONE);
                break;

            case 2:

                if (curState > nextState) {
                    brideDetailsForm.setVisibility(View.GONE);
                } else if (!validateBrideDetails()) {
                    return;
                } else if (nextState == 4 && !completeAllStages()) {
                    return;
                } else {
                    brideDetailsForm.setVisibility(View.GONE);
                }

                break;

            case 3:

                if (curState > nextState) {
                    groomDetailsForm.setVisibility(View.GONE);
                } else if (!validateGroomDetails()) {
                    return;
                } else if (nextState == 4 && !completeAllStages()) {
                    return;
                } else {
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
        } else {
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

        showFrom(curState, curState + 1);
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
        } else {
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
        } else {
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
        } else {
            return false;
        }
    }


    public void submitData(View view) {
        /*
        appointment_date : 25-05-2019
        MarriageType : Intended Marriage
        ApplicantName : Dipsikha Phukan
        sro_office : 1
        email : dipsikhaphukan09@gmail.com
        mobile : 9706672004

        BrideName : Dipsikha Phukan
        BrideAge : 19
        BrideMarriageCondition : Unmarried
        BrideOccupation : service
        BrideFathersName : Dipsikha Phukan

        BrideVillage : JORHAT
        BridePoliceStation : jorhat
        BridePostOffice : Jorhat
        BrideDistrict : jorhat
        BrideState : Assam
        BridePincode : 785001

        BridePermanentVillage : JORHAT
        BridePermanentPS : jorhat
        BridePermanentPO : Jorhat
        BridePermanentDistrict : jorhat
        BridePermanentState : Assam
        BridePermanentPincode : 785001
        BrideLengthOfResidence : 9

        GroomName : Dipsikha Phukan
        GroomAge : 22
        GroomMarriageCondition : Widower
        GroomOccupation : grg
        GroomFathersName : D hjk

        GroomVillage : JORHAT
        GroomPoliceStation : ghy
        GroomPostOffice : hijggy
        GroomDistrict : ghy
        GroomState : Assam
        GroomPincode : 785001

        GroomPermanentVillage : JORHAT
        GroomPermanentPO : ghy
        GroomPermanentPS : hijggy
        GroomPermanentDistrict : ghy
        GroomPermanentState : Assam
        GroomPermanentPincode : 785001
        gLengthOfResidence : 8
           */


//        appointment_date : 27-05-2019
//        MarriageType : Intended Marriage
//        ApplicantName : John
//        sro_office : 1
//        email : john@gmail.com
//        mobile : 9706672004
//        BrideName : Rekha
//        BrideAge : 25
//        BrideMarriageCondition : Married
//        BrideOccupation : service
//        BrideFathersName : Raj
//        BrideVillage : Ghy
//        BridePoliceStation : Ghy
//        BridePostOffice : Ghy
//        BrideDistrict : Ghy
//        BrideState : Assam
//        BridePincode : 781005
//        IfPresentPermanentBride : 0
//        BridePermanentVillage : Ghy
//        BridePermanentPS : Ghy
//        BridePermanentPO : Ghy
//        BridePermanentDistrict : Ghy
//        BridePermanentState : Assam
//        BridePermanentPincode : 781005
//        BrideLengthOfResidence : 2
//        GroomName : Kaku
//        GroomAge : 43
//        GroomMarriageCondition : Widow
//        GroomOccupation : IT
//        GroomFathersName : Kaki
//        GroomVillage : Jorhat
//        GroomPoliceStation : Jorhat
//        GroomPostOffice : Jorhat
//        GroomDistrict : Jorhat
//        GroomState : Assam
//        GroomPincode : 781002
//        IfPresentPermanentGroom : 1
//        GroomPermanentVillage : Jorhat
//        GroomPermanentPO : Jorhat
//        GroomPermanentPS : Jorhat
//        GroomPermanentDistrict : Jorhat
//        GroomPermanentState : Assam
//        GroomPermanentPincode : 781002
//        gLengthOfResidence : 2
//


        params.put("ApplicantName", applicantName.getText().toString());
        params.put("email", email.getText().toString());
        params.put("mobile", mobile.getText().toString());

        params.put("BrideName", name.getText().toString());
        params.put("BrideAge", age.getText().toString());
        params.put("BrideOccupation", occupation.getText().toString());
        params.put("BrideFathersName", fName.getText().toString());

        params.put("BrideVillage", eCity.getText().toString());
        params.put("BridePoliceStation", ePolice.getText().toString());
        params.put("BridePostOffice", ePost.getText().toString());
        params.put("BrideDistrict", eDistrict.getText().toString());
        params.put("BrideState", eState.getText().toString());
        params.put("BridePincode", ePin.getText().toString());

        params.put("BridePermanentVillage", perCity.getText().toString());
        params.put("BridePermanentPS", perPolice.getText().toString());
        params.put("BridePermanentPO", perPost.getText().toString());
        params.put("BridePermanentDistrict", perDistrict.getText().toString());
        params.put("BridePermanentState", perState.getText().toString());
        params.put("BridePermanentPincode", perPin.getText().toString());
        params.put("BrideLengthOfResidence", lengthOfResidence.getText().toString());

        params.put("GroomName", name2.getText().toString());
        params.put("GroomAge", age2.getText().toString());
        params.put("GroomOccupation", occupation2.getText().toString());
        params.put("GroomFathersName", fName2.getText().toString());

        params.put("GroomVillage", eCity2.getText().toString());
        params.put("GroomPoliceStation", ePolice2.getText().toString());
        params.put("GroomPostOffice", ePost2.getText().toString());
        params.put("GroomDistrict", eDistrict2.getText().toString());
        params.put("GroomState", eState2.getText().toString());
        params.put("GroomPincode", ePin2.getText().toString());

        params.put("GroomPermanentVillage", perCity2.getText().toString());
        params.put("GroomPermanentPS", perPolice2.getText().toString());
        params.put("GroomPermanentPO", perPost2.getText().toString());
        params.put("GroomPermanentDistrict", perDistrict2.getText().toString());
        params.put("GroomPermanentState", perState2.getText().toString());
        params.put("GroomPermanentPincode", perPin2.getText().toString());
        params.put("gLengthOfResidence", lengthOfResidence2.getText().toString());


        AndroidNetworking.post("http://192.168.43.210:8080/panjeeyanonline/addmarriagedetails")
                .addBodyParameter(params)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        Log.d(TAG, "success: " + response.toString());

                    }

                    @Override
                    public void onError(ANError error) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        cookieBar.setMessage(error.getMessage());
                        cookieBar.show();

                        Log.d(TAG, "onError: " + error.getErrorCode());
                        Log.d(TAG, "onError: " + error.getMessage());


                    }
                });



        confirmForm.setVisibility(View.GONE);
        header.setVisibility(View.GONE);

        // mark all the states "done"
        stateProgressBar.setAllStatesCompleted(true);

        // make them unclickable
        stateProgressBar.setOnStateItemClickListener(null);

        cardView.setVisibility(View.GONE);

        // network call to write info in the database and then payment activity is started

//        Intent intent = new Intent(this, PaymentActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void showErrorMessage(String msg) {
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        cookieBar.setMessage(msg);
        cookieBar.show();
    }


    private String getVolleyErrorMessage(VolleyError error) {
        String msg = "";

        if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {
            //This indicates that the request has either time out or there is no connection
            msg = "Please, check your network connection else the Server is not reachable at this moment.";
        } else if (error instanceof AuthFailureError) {
            msg = "Unauthorized access denied. ";
        } else if (error instanceof ServerError) {
            msg = "Server temporarily out of service.";
        }

        return msg;
    }
}
