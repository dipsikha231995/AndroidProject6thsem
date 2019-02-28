package com.example.applicationformcv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.NumericShaper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationHolder;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.common.collect.Range;

public class DeedRegistration extends AppCompatActivity implements View.OnClickListener {

    public static final String Date_array = "Date";
    public static final String JSON_ARRAY = "result";
    private JSONArray result;
    Spinner mySpinnerDate;
    private ArrayList<String> arrayList;

    private final int IMG_REQUEST = 1;

    //choose image
    ImageView imgView;
    Button chooseBtn;

    TextView imgError;

    //
    private boolean imageSelected = false;


    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    //for validation
    private EditText name, email, mobile, address, city, po, district, pin;
    private Button submit;

    TextView forSale, forSalePurchase, forLandFlat;
    EditText amount;
    RadioGroup radioGroupMaleFemale;
    RadioGroup radioUrbanRural;

    private Spinner mySpinner1;
    Spinner mySpinner2, mySpinner3, mySpinner4;
    Spinner mySpinner5;
    ArrayAdapter<String> myAdapter5;
    private Bitmap bitmap;


    private List<String> list;
    ArrayAdapter<String> myAdapter4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deed_registration);
        initializeValidation();
        mySpinner1 = findViewById(R.id.spinner1);
        mySpinner2 = findViewById(R.id.spinner2);
        mySpinner3 = findViewById(R.id.spinner3);
        mySpinner4 = findViewById(R.id.spinner4);
        mySpinner5 = findViewById(R.id.spinner5);


        mySpinnerDate = findViewById(R.id.spinnerDate);
        arrayList = new ArrayList<>();
        getdata();

        //choose image
        imgView = findViewById(R.id.imageView);
        chooseBtn = findViewById(R.id.buttonChoose);
        chooseBtn.setOnClickListener(this);

        imgError = findViewById(R.id.imageError);

        forSale = findViewById(R.id.extraForSale);
        amount = findViewById(R.id.considerationAmt);
        forSalePurchase = findViewById(R.id.extraForSalePur);
        radioGroupMaleFemale = findViewById(R.id.radioGroup1);
        forLandFlat = findViewById(R.id.landFlat);
        radioUrbanRural = findViewById(R.id.radioGroup2);

        //create array adapter
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.applicantType));
        myAdapter1.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner1.setAdapter(myAdapter1);

        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.selectOffice));
        myAdapter2.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner2.setAdapter(myAdapter2);

        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.selectDeed));
        myAdapter3.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner3.setAdapter(myAdapter3);


        //
        list = new ArrayList<>();
        myAdapter4 = new ArrayAdapter<>(this, R.layout.spinner_item_text_colour, list);
        myAdapter4.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner4.setAdapter(myAdapter4);


        mySpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();

                if (category.equalsIgnoreCase("SALE") || category.equalsIgnoreCase("GIFT")) {
                    forSale.setVisibility(View.VISIBLE);
                    //amount.setVisibility(View.VISIBLE);

                    findViewById(R.id.considerationWrapper).setVisibility(View.VISIBLE);

                    forSalePurchase.setVisibility(View.VISIBLE);
                    radioGroupMaleFemale.setVisibility(View.VISIBLE);
                    forLandFlat.setVisibility(View.VISIBLE);
                    radioUrbanRural.setVisibility(View.VISIBLE);
                }
                else {
                    forSale.setVisibility(View.GONE);
                    //amount.setVisibility(View.GONE);

                    amount.setText("0");
                    amount.setError(null);
                    findViewById(R.id.considerationWrapper).setVisibility(View.GONE);

                    //

                    forSalePurchase.setVisibility(View.GONE);
                    radioGroupMaleFemale.setVisibility(View.GONE);
                    forLandFlat.setVisibility(View.GONE);
                    radioUrbanRural.setVisibility(View.GONE);
                }


                // populate the other spinner
                if (category.equalsIgnoreCase("AFFIDAVIT")) {

                    //
                    list.clear();

                    list.add("Select Deed Sub Category *");
                    list.add("AFFIDAVIT");

                    myAdapter4.notifyDataSetChanged();
                } else if (category.equalsIgnoreCase("AGREEMENT")) {
                    //
                    list.clear();

                    list.add("Select Deed Sub Category *");
                    list.add("Sale of a bill of Exchange");
                    list.add("Sale of a Government Security");
                    list.add("Others");

                    myAdapter4.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        myAdapter5 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.doc_type));
        myAdapter5.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner5.setAdapter(myAdapter5);

        //validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.applicantName, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.number, "^[0-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.addressText, "[a-zA-Z0-9\\s\\.\\-]+", R.string.addresserror);
        awesomeValidation.addValidation(this, R.id.city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
        awesomeValidation.addValidation(this, R.id.postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
        awesomeValidation.addValidation(this, R.id.districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
        awesomeValidation.addValidation(this, R.id.pin, "(\\d{6})", R.string.pinerror);

        awesomeValidation.addValidation(this, R.id.username, "(^$|^.*@.*\\..*$)", R.string.emailerror);

        //awesomeValidation.addValidation(this, R.id.considerationAmt, Range.closed(0,10000), R.string.considerationAmterror);

    }



    private void initializeValidation() {

        name = findViewById(R.id.applicantName);
        email = findViewById(R.id.username);
        mobile = findViewById(R.id.number);
        address = findViewById(R.id.addressText);
        city = findViewById(R.id.city);
        po = findViewById(R.id.postofficeName);
        district = findViewById(R.id.districtName);
        pin = findViewById(R.id.pin);

        submit = findViewById(R.id.btnSubmit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonChoose:
                selectImage();
                break;

//            case R.id.buttonUpload:
//                uploadImage();
//                break;

        }
    }

    //choose image

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);

                //
                imageSelected = true;
                imgError.setVisibility(View.GONE);


                mySpinner5.setAdapter(null);
                mySpinner5.setAdapter(myAdapter5);

                mySpinner5.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void getdata() {

        StringRequest stringRequest = new StringRequest("http://192.168.43.210/trial/getAppointmentDate.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(JSON_ARRAY);
                            availabe_dates(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    private void availabe_dates(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                arrayList.add(json.getString(Date_array));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        arrayList.add(0, "Select Appointment Date *");

        mySpinnerDate.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour, arrayList));
    }



    private boolean validateSpinners() {

        boolean result = true;

        View selectedView = mySpinner1.getSelectedView();
        View selectedView2 = mySpinner2.getSelectedView();
        View selectedView3 = mySpinner3.getSelectedView();
        View selectedView4 = mySpinner4.getSelectedView();
        View selectedView5 = mySpinner5.getSelectedView();


        if (selectedView instanceof TextView) {

            TextView selectedTextView = (TextView) selectedView;

            if (selectedTextView.getText().toString().equalsIgnoreCase("Select Applicant Type *")) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError("Choose an item");

                result = false;
            }
        }


        if (selectedView2 instanceof TextView) {

            TextView selectedTextView2 = (TextView) selectedView2;

            if (selectedTextView2.getText().toString().equalsIgnoreCase("Select Office for Registration *")) {
                selectedTextView2.setFocusable(true);
                selectedTextView2.setClickable(true);
                selectedTextView2.setFocusableInTouchMode(true);
                selectedTextView2.setError("Choose an item");

                result = false;
            }
        }


        if (selectedView3 instanceof TextView) {

            TextView selectedTextView3 = (TextView) selectedView3;

            if (selectedTextView3.getText().toString().equalsIgnoreCase("Select Deed Category *")) {
                selectedTextView3.setFocusable(true);
                selectedTextView3.setClickable(true);
                selectedTextView3.setFocusableInTouchMode(true);
                selectedTextView3.setError("Choose an item");

                result = false;
            }
        }

        if (selectedView4 instanceof TextView) {

            TextView selectedTextView4 = (TextView) selectedView4;

            if (selectedTextView4.getText().toString().equalsIgnoreCase("Select Deed Sub Category *")) {
                selectedTextView4.setFocusable(true);
                selectedTextView4.setClickable(true);
                selectedTextView4.setFocusableInTouchMode(true);
                selectedTextView4.setError("Choose an item");

                //
                result = false;
            }
        }


        if (selectedView5 instanceof TextView) {

            TextView selectedTextView5 = (TextView) selectedView5;

            if (selectedTextView5.getText().toString().equalsIgnoreCase("Select Image Document Type *")) {
                selectedTextView5.setFocusable(true);
                selectedTextView5.setClickable(true);
                selectedTextView5.setFocusableInTouchMode(true);
                selectedTextView5.setError("Choose an item");

                //
                result = false;
            }
        }


        return result;
    }


    private boolean validateConsiderationAmount() {
        if (findViewById(R.id.considerationWrapper).getVisibility() == View.VISIBLE) {
            if (amount.length() == 0) {
                amount.setError("Enter an amount");

                return false;
            }
        }

        return true;
    }



    public void doSubmit(View view) {

       //
        boolean a = awesomeValidation.validate();


        //
       boolean b = validateSpinners();


       boolean c = validateConsiderationAmount();


        //
        if (!imageSelected) {
            imgError.setVisibility(View.VISIBLE);
           // imgError.setText("X Image not selected, Please select one!");
            imgError.setError("Image not selected, Please select one!");
        }


        if (a && b && imageSelected && c) {
            Intent intentSubmit = new Intent(this, AppointmentConfirmed.class);
            startActivity(intentSubmit);
        }
    }

}
