package com.example.applicationformcv;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;

import id.zelory.compressor.Compressor;

public class DeedRegistration extends AppCompatActivity {

    public static final String TAG = "MY-APP";

    public static final String Date_array = "Date";
    public static final String JSON_ARRAY = "result";
    private JSONArray result;

    Spinner mySpinnerDate;

    private final int ADDRESS_PROOF_REQUEST = 1;
    private final int AGE_PROOF_REQUEST = 10;
    private final int IDENTITY_PROOF_REQUEST = 100;

    private final int PDF_REQUEST = 11;

    //choose image
    ImageView addressimageView, ageImageView, idImageView;
    TextView addressimgError, ageimgError, idimgError;

    private boolean ageProofSelected = false;
    private boolean addressProofSelected = false;
    private boolean identityProofSelected = false;


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

    StateProgressBar stateProgressBar;

    // all the three form layouts
    ViewGroup appointmentForm;
    ViewGroup uploadDocumentsForm;
    ViewGroup confirmForm;

    // header textView
    TextView header;

    CardView cardView;

    LinearLayout appointment_confirm;

    private boolean appointmentFormCompleted = false;
    private boolean uploadDocumentsCompleted = false;

    TextView address_name, age_name, id_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deed_registration);
        initializeValidation();

        appointmentForm = findViewById(R.id.appoint_form);
        uploadDocumentsForm = findViewById(R.id.document_upload_form);
        confirmForm = findViewById(R.id.confirm_form);
        header = findViewById(R.id.header_text2);

        mySpinner1 = findViewById(R.id.spinner1);
        mySpinner2 = findViewById(R.id.spinner2);
        mySpinner3 = findViewById(R.id.spinner3);
        mySpinner4 = findViewById(R.id.spinner4);

        cardView = findViewById(R.id.card1);

        appointment_confirm = findViewById(R.id.confirm_appointment);

        // select document spinner
        mySpinner5 = findViewById(R.id.spinner5);
        mySpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "" + position);

                switch (position) {
                    case 1:
                        // select address proof
                        chooseImageOrPDF(ADDRESS_PROOF_REQUEST);
                        break;

                    case 2:
                        // select age proof
                        chooseImageOrPDF(AGE_PROOF_REQUEST);
                        break;

                    case 3:
                        // select id proof
                        chooseImageOrPDF(IDENTITY_PROOF_REQUEST);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        mySpinnerDate = findViewById(R.id.spinnerDate);
        getdata();

        // imagesViews and error textViews
        addressimageView = findViewById(R.id.address_proof_imageView);
        ageImageView = findViewById(R.id.age_proof_imageView);
        idImageView = findViewById(R.id.id_proof_imageView);

        addressimgError = findViewById(R.id.address_proof_error);
        ageimgError = findViewById(R.id.age_proof_error);
        idimgError = findViewById(R.id.id_proof_error);

        address_name = findViewById(R.id.address_proof_name);
        age_name = findViewById(R.id.age_proof_name);
        id_name = findViewById(R.id.id_proof_name);

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


        stateProgressBar = findViewById(R.id.state_progress_bar);
        String[] descriptionData = {"Appointment\nForm", "Upload\nDocuments", "Confirm\nSubmission"};
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


    //choose image
    private void chooseImageOrPDF(int requestCode) {

        // choose image or pdf

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimeTypes = {"image/*", "application/pdf"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri path = data.getData();          // get the file's content uri; content://


        if (requestCode == ADDRESS_PROOF_REQUEST && resultCode == RESULT_OK && data != null) {

            decideFileType(path, ADDRESS_PROOF_REQUEST);
        }
        else if (requestCode == AGE_PROOF_REQUEST && resultCode == RESULT_OK && data != null) {

            decideFileType(path, AGE_PROOF_REQUEST);

        }
        else if (requestCode == IDENTITY_PROOF_REQUEST && resultCode == RESULT_OK && data != null) {

            decideFileType(path, IDENTITY_PROOF_REQUEST);

        }

        //
        mySpinner5.setAdapter(null);
        mySpinner5.setAdapter(myAdapter5);
        mySpinner5.setVisibility(View.VISIBLE);
    }


    private void decideFileType(Uri path, int docType) {
        // mime type
        String mimeType = getContentResolver().getType(path);

        if (mimeType.startsWith("image/")) {

            switch (docType) {
                case ADDRESS_PROOF_REQUEST:
                    loadImage(path, ADDRESS_PROOF_REQUEST);
                    break;

                case AGE_PROOF_REQUEST:
                    loadImage(path, AGE_PROOF_REQUEST);
                    break;

                case IDENTITY_PROOF_REQUEST:
                    loadImage(path, IDENTITY_PROOF_REQUEST);
                    break;
            }
        }
        else if (mimeType.equals("application/pdf")) {
            switch (docType) {
                case ADDRESS_PROOF_REQUEST:
                    loadPDF(path, ADDRESS_PROOF_REQUEST);
                    break;

                case AGE_PROOF_REQUEST:
                    loadPDF(path, AGE_PROOF_REQUEST);
                    break;

                case IDENTITY_PROOF_REQUEST:
                    loadPDF(path, IDENTITY_PROOF_REQUEST);
                    break;
            }
        }
    }

    private void loadImage(Uri uri, int docType) {
        try {

            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);


//            switch (docType) {
//                case ADDRESS_PROOF_REQUEST:
//                    addressimageView.setImageBitmap(bitmap);
//                    addressProofSelected = true;
//                    //addressimgError.setVisibility(View.GONE);
//                    addressimgError.setError(null);
//
//                    break;
//
//                case AGE_PROOF_REQUEST:
//                    ageImageView.setImageBitmap(bitmap);
//                    ageProofSelected = true;
//                    //ageimgError.setVisibility(View.GONE);
//                    ageimgError.setError(null);
//
//                    break;
//
//                case IDENTITY_PROOF_REQUEST:
//                    idImageView.setImageBitmap(bitmap);
//                    identityProofSelected = true;
//                    //idimgError.setVisibility(View.GONE);
//                    idimgError.setError(null);
//
//                    break;
//            }

        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }


    private void loadPDF(Uri path, int doc_type) {

        // content://c:/full_path/dips.pdf
        // or file://c:/full_path/dips.pdf

        String uriString = path.toString();

        Log.d(TAG, uriString);

        File myFile = new File(uriString);              // this is the pdf file to upload


        String displayName = null;                       //  actual file name

        // we are getting the file name
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;

            try {
                cursor = this.getContentResolver().query(path, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));


                    if (doc_type == ADDRESS_PROOF_REQUEST) {

                        addressimageView.setImageResource(R.drawable.pdf);
                        addressProofSelected = true;
                        //addressimgError.setText(displayName);
                        //addressimgError.setVisibility(View.VISIBLE);
                        address_name.setText(displayName);

                        addressimgError.setError(null);
                    }
                    else if (doc_type == AGE_PROOF_REQUEST) {

                        ageImageView.setImageResource(R.drawable.pdf);
                        ageProofSelected = true;
                        //ageimgError.setText(displayName);
                       //ageimgError.setVisibility(View.VISIBLE);
                        age_name.setText(displayName);

                        ageimgError.setError(null);
                    }
                    else if (doc_type == IDENTITY_PROOF_REQUEST) {

                        idImageView.setImageResource(R.drawable.pdf);
                        identityProofSelected = true;
                        //idimgError.setText(displayName);
                       // idimgError.setVisibility(View.VISIBLE);
                        id_name.setText(displayName);

                        idimgError.setError(null);
                    }
                }

            }
            finally {
                cursor.close();
            }
        }
        else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();

            Toast.makeText(this, displayName, Toast.LENGTH_SHORT).show();
        }
    }


    private void getdata() {

        final String url = "http://192.168.43.210:8080/mvcbook/getdates";

        final ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Select Appointment Date *");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //response from servlet is kept in the JSONArray array
                            JSONArray array = new JSONArray(response);

                            //empty list is declared which will hold the json array items
                            //List<String> arrayList = new ArrayList<>();

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
        mySpinnerDate.setAdapter(myDateAdapter);
    }


    private boolean validateSpinners() {

        boolean result = true;

        View selectedView = mySpinner1.getSelectedView();
        View selectedView2 = mySpinner2.getSelectedView();
        View selectedView3 = mySpinner3.getSelectedView();
        View selectedView4 = mySpinner4.getSelectedView();
        View selectedView5 = mySpinnerDate.getSelectedView();


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

            TextView selectedTextView = (TextView) selectedView5;

            if (selectedTextView.getText().toString().equalsIgnoreCase("Select Appointment Date *")) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError("Choose an item");

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


    // go to the next form
    public void submitIt(View view) {
        // get the current state number
        int curState = stateProgressBar.getCurrentStateNumber();

        showFrom(curState, curState + 1);
    }


    private void showFrom(int curState, int nextState) {
        Log.d(TAG, "" + curState + " : " + nextState);

        // hide the form associates with "curState"
        switch (curState) {
            case 1:

                if (!validateAppointmentForm()) {
                    return;
                } else if (nextState == 3 && !completeAllStages()) {
                    return;
                }

                appointmentForm.setVisibility(View.GONE);
                break;

            case 2:

                if (curState > nextState) {
                    uploadDocumentsForm.setVisibility(View.GONE);
                }
                else if (!validateUploadDocumentsForm()) {
                    return;
                }
                else if (nextState == 3 && !completeAllStages()) {
                    return;
                }
                else {
                    uploadDocumentsForm.setVisibility(View.GONE);
                }

                break;

            case 3:
                confirmForm.setVisibility(View.GONE);
                break;
        }

        // show the from associates with "nextState"
        // Change the heading
        // Change the current state number
        switch (nextState) {
            case 1:
                appointmentForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                header.setText("Appointment Form");
                break;

            case 2:
                uploadDocumentsForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                header.setText("Upload Documents");
                break;

            case 3:
                confirmForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                header.setText("Confirm Submission");
                break;
        }
    }


    private boolean validateUploadDocumentsForm() {
        if (addressProofSelected && ageProofSelected && identityProofSelected) {
            uploadDocumentsCompleted = true;
            return true;
        }

        if (!addressProofSelected) {
            addressimgError.setError("Address Proof not selected, Please select one!");
        }
        if (!ageProofSelected) {
            ageimgError.setError("Age Proof not selected, Please select one!");
        }
        if (identityProofSelected == false) {
            idimgError.setError("Identity Proof not selected, Please select one!");
        }

        return false;
    }


    private boolean completeAllStages() {
        // if all the previous stages are completed, then only
        if (!appointmentFormCompleted || !uploadDocumentsCompleted) {
            Toast.makeText(this, "Complete all the previous stages first", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private boolean validateAppointmentForm() {
        boolean a = awesomeValidation.validate();
        boolean b = validateSpinners();
        boolean c = validateConsiderationAmount();

        if (a && b && c) {
            appointmentFormCompleted = true;            // mark appointment form complete
            return true;
        } else {
            return false;
        }
    }


    public void submitData(View view) {

        Toast.makeText(this, "data submitted", Toast.LENGTH_SHORT).show();

        confirmForm.setVisibility(View.GONE);

        header.setVisibility(View.GONE);

        cardView.setVisibility(View.GONE);

        // mark all the states "done"
        stateProgressBar.setAllStatesCompleted(true);

        // make them unclickable
        stateProgressBar.setOnStateItemClickListener(null);

        appointment_confirm.setVisibility(View.VISIBLE);

    }
}
