package com.example.applicationformcv;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import fr.ganfra.materialspinner.MaterialSpinner;
import id.zelory.compressor.Compressor;

public class DeedRegistration extends AppCompatActivity {

    private static final String TAG_UPLOAD_TEST = "uploadTest";
    Map<String, String> params;
    AlertDialog alertDialog;

    public static final String TAG = "MY-APP";
    CookieBar.Builder cookieBar;

    MaterialSpinner mySpinnerDate;

    private final int ADDRESS_PROOF_REQUEST = 1;
    private final int AGE_PROOF_REQUEST = 10;
    private final int IDENTITY_PROOF_REQUEST = 100;
    File idProofFile, addressProofFile, ageProofFile;


    //choose image
    ImageView addressimageView, ageImageView, idImageView;
    TextView addressimgError, ageimgError, idimgError;

    private boolean ageProofSelected = false;
    private boolean addressProofSelected = false;
    private boolean identityProofSelected = false;

    //for validation
    TextInputLayout nameLayout, mobileLayout, addressLayout, cityLayout, poLayout, districtLayout, pinLayout, emailLayout;
    private EditText name, email, mobile, address, city, po, district, pin;
    private Button submit;

    TextView forSale, forSalePurchase, forLandFlat;
    EditText amount;
    RadioGroup radioGroupMaleFemale;
    RadioGroup radioUrbanRural;

    MaterialSpinner mySpinner1, mySpinner2, mySpinner3, mySpinner4;
    MaterialSpinner mySpinner5;
    ArrayAdapter<String> subDeedAdapter;

    StateProgressBar stateProgressBar;

    // all the four form layouts
    ViewGroup appointmentForm;
    ViewGroup uploadDocumentsForm;
    ViewGroup confirmForm;
    ViewGroup paymentForm;
    ViewGroup success_message;
    ViewGroup noInternetView;
    ScrollView myScrollView;


    // header textView
    TextView header;

    CardView headerCard;


    private boolean appointmentFormCompleted = false;
    private boolean uploadDocumentsCompleted = false;

    TextView address_name, age_name, id_name;

    List<DeedCategoryModel> deedlist = new ArrayList<>();
    List<ApplicantTypeModel> applicantList = new ArrayList<>();
    List<RegistrationOfficeModel> officeList = new ArrayList<>();

    TextView appointmentID, showID, details;

    // Payment Form
    TextInputLayout deptID, payerName, pan, block, locality, area, payerPin, payerPhone, remarks;

    // this is the amount to be paid for...
    int registrationFee;
    String appointment_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deed_registration);
        initializeValidation();

        // to store all the parameters
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

        /// Payment form
        deptID = findViewById(R.id.deptTaxIdWrapper);
        payerName = findViewById(R.id.Namewrapper);
        pan = findViewById(R.id.panWrapper);
        block = findViewById(R.id.blockwrapper);
        locality = findViewById(R.id.localitywrapper);
        area = findViewById(R.id.areaWrapper);
        payerPin = findViewById(R.id.pinCodeWrapper);
        payerPhone = findViewById(R.id.mobileWrapper);
        remarks = findViewById(R.id.remarksWrapper);


        //No internet connection error
        cookieBar = CookieBar.build(DeedRegistration.this)
                .setTitleColor(android.R.color.white)
                .setBackgroundColor(R.color.colorPrimary)
                .setIcon(R.drawable.ic_icon)
                .setEnableAutoDismiss(true)
                .setDuration(5000)                      // 5 seconds
                .setCookiePosition(CookieBar.TOP)
                .setSwipeToDismiss(true);

        setTitle(getString(R.string.deed));
        appointmentForm = findViewById(R.id.appoint_form);
        uploadDocumentsForm = findViewById(R.id.document_upload_form);
        confirmForm = findViewById(R.id.confirm_form);
        paymentForm = findViewById(R.id.makePayment);
        header = findViewById(R.id.header_text2);
        success_message = findViewById(R.id.card2);

        mySpinner1 = findViewById(R.id.spinner1);               // applicant type spinner
        mySpinner2 = findViewById(R.id.spinner2);               // office registration spinner
        mySpinner3 = findViewById(R.id.spinner3);               // deed category spinner

        mySpinner4 = findViewById(R.id.spinner4);               // sub deed category spinner

        List<String> temp = new ArrayList<>();

        subDeedAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_text_colour, temp);
        subDeedAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner4.setAdapter(subDeedAdapter);

        mySpinnerDate = findViewById(R.id.spinnerDate);         // date spinner

        setUpApplicantSpinner();
        setUpDeedCategorySpinner();
        setUpOfficeSpinner();
        getHolidays();

        headerCard = findViewById(R.id.card1);
        noInternetView = findViewById(R.id.no_internet_view);
        myScrollView = findViewById(R.id.my_scroll_view);

        // select document spinner
        mySpinner5 = findViewById(R.id.spinner5);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.doc_type));
        adapter.setDropDownViewResource(R.layout.spinner_item_text_colour);

        mySpinner5.setAdapter(adapter);
        mySpinner5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // select address proof
                        chooseImageOrPDF(ADDRESS_PROOF_REQUEST);
                        break;

                    case 1:
                        // select age proof
                        chooseImageOrPDF(AGE_PROOF_REQUEST);
                        break;

                    case 2:
                        // select id proof
                        chooseImageOrPDF(IDENTITY_PROOF_REQUEST);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // applicant type spinner listener
        mySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {

                    ApplicantTypeModel obj = applicantList.get(position); //getting the model from the list & storing in obj
                    int code = obj.getExempted(); //storing the code in variable code

                    params.put("applicant_type", String.valueOf(code));

                    Log.d(TAG, "exempted: " + code);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {

                    RegistrationOfficeModel obj = officeList.get(position); //getting the model from the list & storing in obj
                    int code = obj.getSroCode(); //storing the code in variable code

                    params.put("sro_office", String.valueOf(code));

                    Log.d(TAG, "onItemSelected: " + code);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                params.put("appointment_date", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

        mySpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();

                if (position >= 0) {
                    Log.d(TAG, deedlist.get(position).toString()); //model list and category list are not in same order

                    DeedCategoryModel obj = deedlist.get(position); //getting the model from the deedlist & storing in obj
                    int code = obj.getCode(); //storing the code in variable code

                    params.put("Deedtype", String.valueOf(code));

                    final String url = MyFileUtil.TOMCAT_URL + "e-Panjeeyan/getsubdeed?codeVal=" + code;


                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        //response from servlet is kept in the JSONArray array
                                        JSONArray array = new JSONArray(response);

                                        List<String> subDeed = new ArrayList<>();

                                        //array list is populated from JSON array
                                        for (int i = 0; i < array.length(); i++) {
                                            subDeed.add(array.getString(i));
                                        }

                                        subDeedAdapter = new ArrayAdapter<String>(DeedRegistration.this,
                                                R.layout.spinner_item_text_colour,
                                                subDeed);
                                        subDeedAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                                        mySpinner4.setAdapter(subDeedAdapter);


                                    } catch (Exception e) {
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });

                    // Add request to the Request queue
                    MySingleton.getInstance(getApplicationContext())
                            .addToRequestQueue(stringRequest);

                }


                if (category.equalsIgnoreCase("SALE") || category.equalsIgnoreCase("GIFT")) {
                    forSale.setVisibility(View.VISIBLE);
                    //amount.setVisibility(View.VISIBLE);

                    findViewById(R.id.considerationWrapper).setVisibility(View.VISIBLE);

                    forSalePurchase.setVisibility(View.VISIBLE);
                    radioGroupMaleFemale.setVisibility(View.VISIBLE);
                    forLandFlat.setVisibility(View.VISIBLE);
                    radioUrbanRural.setVisibility(View.VISIBLE);
                } else {
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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mySpinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                params.put("Subdeedtype", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateProgressBar = findViewById(R.id.state_progress_bar);
        String[] descriptionData = {getString(R.string.appointform), getString(R.string.upload), getString(R.string.confirm),
                getString(R.string.payment)};
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
        } else if (requestCode == AGE_PROOF_REQUEST && resultCode == RESULT_OK && data != null) {

            decideFileType(path, AGE_PROOF_REQUEST);

        } else if (requestCode == IDENTITY_PROOF_REQUEST && resultCode == RESULT_OK && data != null) {

            decideFileType(path, IDENTITY_PROOF_REQUEST);
        }
    }


    private void decideFileType(Uri path, int docType) {
        // mime type
        String type = getContentResolver().getType(path);

        if (type.startsWith("image/")) {

            switch (docType) {
                case ADDRESS_PROOF_REQUEST:
                    executeAsyncTask(path, ADDRESS_PROOF_REQUEST, type);
                    break;

                case AGE_PROOF_REQUEST:
                    executeAsyncTask(path, AGE_PROOF_REQUEST, type);
                    break;

                case IDENTITY_PROOF_REQUEST:
                    executeAsyncTask(path, IDENTITY_PROOF_REQUEST, type);
                    break;
            }
        } else if (type.equals("application/pdf")) {
            switch (docType) {
                case ADDRESS_PROOF_REQUEST:
                    loadPDF(path, ADDRESS_PROOF_REQUEST, type);
                    break;

                case AGE_PROOF_REQUEST:
                    loadPDF(path, AGE_PROOF_REQUEST, type);
                    break;

                case IDENTITY_PROOF_REQUEST:
                    loadPDF(path, IDENTITY_PROOF_REQUEST, type);
                    break;
            }
        }
    }


    private void executeAsyncTask(Uri uri, final int requestCode, final String type) {
        // run the async task to generate the temp file

        new AsyncTask<Uri, Void, File>() {
            @Override
            protected File doInBackground(Uri... uris) {
                String mimeType = getContentResolver().getType(uris[0]);

                // get the temp file
                // and compress it
                File tempFile = null;

                try {
                    tempFile = new Compressor(getApplicationContext())
                            .compressToFile(
                                    MyFileUtil.createTempFile(getApplicationContext(), uris[0], type)
                            );
                } catch (Exception ex) {
                }

                return tempFile;            // return the compressed file
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);

                displayImage(file, requestCode);
            }

        }.execute(uri);
    }


    private void displayImage(File imageFile, int requestCode) {

        if (imageFile != null) {
            // choose the imageView
            ImageView imageView = null;

            if (requestCode == ADDRESS_PROOF_REQUEST) {
                imageView = addressimageView;

                addressProofFile = imageFile;
                addressProofSelected = true;

                // clear the textview associated with the image view
                // and the error message
                address_name.setText("");
                addressimgError.setError(null);

            } else if (requestCode == AGE_PROOF_REQUEST) {
                imageView = ageImageView;

                ageProofFile = imageFile;
                ageProofSelected = true;

                age_name.setText("");
                ageimgError.setError(null);
            } else if (requestCode == IDENTITY_PROOF_REQUEST) {
                imageView = idImageView;

                idProofFile = imageFile;
                identityProofSelected = true;

                id_name.setText("");
                idimgError.setError(null);
            }


            //Loading image from file location into imageView
            Glide.with(DeedRegistration.this)
                    .load(imageFile)
                    .centerCrop()
                    .into(imageView);
        }
    }


    private void loadPDF(Uri path, final int doc_type, final String type) {
        String uriString = path.toString();

        String displayName = "";        //  actual file name

        // we are getting the file name
        if (uriString.startsWith("content://")) {
            Cursor cursor = null;

            try {
                cursor = this.getContentResolver().query(path, null, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    // load the actual pdf file
                    new AsyncTask<Uri, Void, File>() {
                        @Override
                        protected File doInBackground(Uri... uris) {
                            // get the temp file
                            File tempFile = MyFileUtil.createTempFile(getApplicationContext(), uris[0], type);

                            Log.d(TAG, "pdf size: " + (tempFile.length() / 1024) + " KB");

                            return tempFile;            // return the compressed file
                        }

                        @Override
                        protected void onPostExecute(File file) {
                            super.onPostExecute(file);

                            switch (doc_type) {
                                case ADDRESS_PROOF_REQUEST:
                                    addressProofFile = file;
                                    break;

                                case IDENTITY_PROOF_REQUEST:
                                    idProofFile = file;
                                    break;

                                case AGE_PROOF_REQUEST:
                                    ageProofFile = file;
                                    break;
                            }
                        }

                    }.execute(path);


                    if (doc_type == ADDRESS_PROOF_REQUEST) {

                        addressimageView.setImageResource(R.drawable.pdf);
                        addressProofSelected = true;
                        address_name.setText(displayName);

                        addressimgError.setError(null);
                    } else if (doc_type == AGE_PROOF_REQUEST) {
                        ageImageView.setImageResource(R.drawable.pdf);
                        ageProofSelected = true;
                        age_name.setText(displayName);

                        ageimgError.setError(null);
                    } else if (doc_type == IDENTITY_PROOF_REQUEST) {

                        idImageView.setImageResource(R.drawable.pdf);
                        identityProofSelected = true;
                        id_name.setText(displayName);

                        idimgError.setError(null);
                    }
                }

            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            //displayName = myFile.getName();
            Log.d(TAG, "loadPDF: " + displayName);
        }
    }


    private void getHolidays() {
        final String url = MyFileUtil.TOMCAT_URL + "panjeeyanonline/getAppointmentDates";
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

                                //array list is populated from JSON array
                                for (int i = 0; i < array.length(); i++) {
                                    dateList.add(array.getString(i));
                                }


                                ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                                        DeedRegistration.this,
                                        R.layout.spinner_item_text_colour,
                                        dateList
                                );
                                dateAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                                mySpinnerDate.setAdapter(dateAdapter);

                                // hide no_internet_error view
                                noInternetView.setVisibility(View.GONE);
                                stateProgressBar.setVisibility(View.VISIBLE);
                                headerCard.setVisibility(View.VISIBLE);
                                myScrollView.setVisibility(View.VISIBLE);

                                // Now, highlight the * mark
                                // add the bubble showcase
                                new BubbleShowCaseBuilder(DeedRegistration.this)
                                        .title(getString(R.string.label_attention))
                                        .description(getString(R.string.label_field_mandatory))
                                        .targetView(nameLayout)
                                        .backgroundColorResourceId(R.color.colorAccent)
                                        .textColorResourceId(R.color.white)
                                        .imageResourceId(R.drawable.ic_warn)
                                        .show();

                            } else {
                                String msg = object.getString("msg");
                                showErrorMessage(getString(R.string.net_err), msg);
                            }

                        } catch (Exception e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage(getString(R.string.net_err), getVolleyErrorMessage(error));
                    }
                });


        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }


    private void setUpApplicantSpinner() {
        final String url = MyFileUtil.TOMCAT_URL + "e-Panjeeyan/getapplicanttype";

        // get the applicantType from the database
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Type collectionType = new TypeToken<List<ApplicantTypeModel>>() {
                        }.getType();
                        applicantList = new Gson().fromJson(response, collectionType);

                        ArrayList<String> typeList = new ArrayList<>();

                        //array list is populated from JSON array
                        for (int i = 0; i < applicantList.size(); i++) {
                            typeList.add(applicantList.get(i).getType());
                        }


                        ArrayAdapter myTypeAdapter = new ArrayAdapter<String>(DeedRegistration.this,
                                R.layout.spinner_item_text_colour, typeList);

                        myTypeAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                        mySpinner1.setAdapter(myTypeAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage(getString(R.string.net_err), getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }

    private void setUpOfficeSpinner() {

        final String url = MyFileUtil.TOMCAT_URL + "e-Panjeeyan/registrationoffice";

        // get the registration office from the database
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Type collectionType = new TypeToken<List<RegistrationOfficeModel>>() {
                        }.getType();
                        officeList = new Gson().fromJson(response, collectionType);

                        ArrayList<String> list = new ArrayList<>();

                        //array list is populated from JSON array
                        for (int i = 0; i < officeList.size(); i++) {
                            list.add(officeList.get(i).getOfficeName());
                        }


                        ArrayAdapter myTypeAdapter = new ArrayAdapter<String>(DeedRegistration.this,
                                R.layout.spinner_item_text_colour, list);

                        myTypeAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                        mySpinner2.setAdapter(myTypeAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage(getString(R.string.net_err), getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }


    private void showErrorMessage(String title, String msg) {
        Log.d(TAG, "showErrorMessage");

        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        cookieBar.setTitle(title);
        cookieBar.setMessage(msg);
        cookieBar.show();
    }

    private String getVolleyErrorMessage(VolleyError error) {
        String msg = "";

        Log.d(TAG, "msg: " + error.getMessage());

        if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {
            //This indicates that the request has either time out or there is no connection
            msg = getString(R.string.net_server_err);
        } else if (error instanceof AuthFailureError) {
            msg = getString(R.string.unauthorized_access);
        } else if (error instanceof ServerError) {
            msg = getString(R.string.server_err);
        }

        return msg;
    }


    private void setUpDeedCategorySpinner() {
        final String url = MyFileUtil.TOMCAT_URL + "e-Panjeeyan/getdeedcategory";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        Type collectionType = new TypeToken<List<DeedCategoryModel>>() {
                        }.getType();
                        deedlist = new Gson().fromJson(response, collectionType);

                        // ArrayList of String which contains the category names
                        List<String> list = new ArrayList<>();

                        Iterator<DeedCategoryModel> iterator = deedlist.iterator();

                        while (iterator.hasNext()) {
                            DeedCategoryModel model = iterator.next();
                            list.add(model.getSection());
                        }

                        ArrayAdapter myTypeAdapter = new ArrayAdapter<String>(DeedRegistration.this,
                                R.layout.spinner_item_text_colour, list);

                        myTypeAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                        mySpinner3.setAdapter(myTypeAdapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorMessage(getString(R.string.net_err), getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }


    private void initializeValidation() {
        nameLayout = findViewById(R.id.applicantNamewrapper);
        mobileLayout = findViewById(R.id.deptNumWrapper);
        addressLayout = findViewById(R.id.addressWrapper);
        cityLayout = findViewById(R.id.cityNamewrapper);
        poLayout = findViewById(R.id.postofficeNamewrapper);
        districtLayout = findViewById(R.id.districtNamewrapper);
        pinLayout = findViewById(R.id.pinWrapper);
        emailLayout = findViewById(R.id.emailWrapper);

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


    private void showFrom(int curState, int nextState) {

        if (nextState == 4) {
            return;
        }

        // hide the form associates with "curState"
        switch (curState) {
            case 1:

                if (!validateStageOne()) {
                    return;
                } else if (nextState == 3 && !completeAllStages()) {
                    return;
                }

                appointmentForm.setVisibility(View.GONE);
                break;

            case 2:

                if (curState > nextState) {
                    uploadDocumentsForm.setVisibility(View.GONE);
                } else if (!validateStageTwo()) {
                    return;
                } else if (nextState == 3 && !completeAllStages()) {
                    return;
                } else {
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
                header.setText(R.string.appoint);
                break;

            case 2:
                uploadDocumentsForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                header.setText(R.string.uploadheading);
                break;

            case 3:
                confirmForm.setVisibility(View.VISIBLE);
                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                header.setText(R.string.confirmheading);
                break;
        }
    }


    private boolean validateStageOne() {
        boolean[] isOk = new boolean[13];

        if (!Pattern.matches("^([a-zA-Z +0-9~%.,:_\\-@&()]+)$", nameLayout.getEditText().getText().toString().trim())) {
            isOk[0] = false;
            nameLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[0] = true;
            nameLayout.setError(null);
        }


        if (!Pattern.matches("(^$|^.*@.*\\..*$)", emailLayout.getEditText().getText().toString().trim())) {
            isOk[12] = false;
            emailLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[12] = true;
            emailLayout.setError(null);
        }


        if (!Pattern.matches("^[0-9]{10,10}$", mobileLayout.getEditText().getText().toString().trim())) {
            isOk[1] = false;
            mobileLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[1] = true;
            mobileLayout.setError(null);
        }

        if (!Pattern.matches("^([a-zA-Z +0-9~%.,:_\\-@&()]+)$", addressLayout.getEditText().getText().toString().trim())) {
            isOk[2] = false;
            addressLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[2] = true;
            addressLayout.setError(null);
        }

        if (!Pattern.matches("^([a-zA-Z +0-9~%.,:_\\-@&()]+)$", cityLayout.getEditText().getText().toString().trim())) {
            isOk[3] = false;
            cityLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[3] = true;
            cityLayout.setError(null);
        }

        if (!Pattern.matches("^([a-zA-Z +0-9~%.,:_\\-@&()]+)$", poLayout.getEditText().getText().toString().trim())) {
            isOk[4] = false;
            poLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[4] = true;
            poLayout.setError(null);
        }

        if (!Pattern.matches("^([a-zA-Z +0-9~%.,:_\\-@&()]+)$", districtLayout.getEditText().getText().toString().trim())) {
            isOk[5] = false;
            districtLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[5] = true;
            districtLayout.setError(null);
        }


        if (!Pattern.matches("^([1-9])([0-9]){5}$", pinLayout.getEditText().getText().toString().trim())) {
            isOk[6] = false;
            pinLayout.setError("\t\t\t\t\t" + getString(R.string.editTextError));
        } else {
            isOk[6] = true;
            pinLayout.setError(null);
        }


        if (mySpinner1.getSelectedItem() == null) {
            isOk[7] = false;
            mySpinner1.setError(getString(R.string.spinnerError));
        } else {
            isOk[7] = true;
            mySpinner1.setError(null);
        }

        if (mySpinnerDate.getSelectedItem() == null) {
            isOk[10] = false;
            mySpinnerDate.setError(getString(R.string.spinnerError));
        } else {
            isOk[10] = true;
            mySpinnerDate.setError(null);
        }


        if (mySpinner2.getSelectedItem() == null) {
            isOk[8] = false;
            mySpinner2.setError(getString(R.string.spinnerError));
        } else {
            isOk[8] = true;
            mySpinner2.setError(null);
        }


        if (mySpinner3.getSelectedItem() == null) {
            isOk[9] = false;
            mySpinner3.setError(getString(R.string.spinnerError));
        } else {
            isOk[9] = true;
            mySpinner3.setError(null);
        }

        if (mySpinner4.getSelectedItem() == null) {
            isOk[11] = false;
            mySpinner4.setError(getString(R.string.spinnerError));
        } else {
            isOk[11] = true;
            mySpinner4.setError(null);
        }

        for (boolean item : isOk) {
            if (!item) {
                // mark this stage incomplete
                appointmentFormCompleted = false;
                return false;
            }
        }

        // mark this stage complete
        appointmentFormCompleted = true;
        return true;
    }


    private boolean validateStageTwo() {
        String errorMsg = "";

        if (!addressProofSelected) {
            errorMsg += getString(R.string.addressDocError);
        }
        if (!ageProofSelected) {
            errorMsg += getString(R.string.ageDocError);
        }
        if (!identityProofSelected) {
            errorMsg += getString(R.string.identityDocError);
        }


        if (addressProofSelected && ageProofSelected && identityProofSelected) {
            uploadDocumentsCompleted = true;
            mySpinner5.setError(null);
            return true;
        } else {
            mySpinner5.setError(errorMsg);
            uploadDocumentsCompleted = false;
            return false;
        }

    }


    private boolean completeAllStages() {
        // if all the previous stages are completed, then only
        if (!appointmentFormCompleted || !uploadDocumentsCompleted) {

            // add the bubble showcase
            new BubbleShowCaseBuilder(this)
                    .title(getString(R.string.label_attention))
                    .description(getString(R.string.label_complete_all_stages))
                    .targetView(stateProgressBar)
                    .backgroundColorResourceId(R.color.colorAccent)
                    .textColorResourceId(R.color.white)
                    .imageResourceId(R.drawable.ic_warn)
                    .show();

            return false;
        } else {
            return true;
        }
    }


    // go to the next form
    public void submitIt(View view) {
        // get the current state number
        int curState = stateProgressBar.getCurrentStateNumber();

        showFrom(curState, curState + 1);
    }


    public void submitData(View view) {

        // mark all the states "done"
        //stateProgressBar.setAllStatesCompleted(true);

        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);

        // make them unclickable
        stateProgressBar.setOnStateItemClickListener(null);


        if (amount.getText().toString().isEmpty()) {
            params.put("ConsiderationAmt", "0");
        } else {
            params.put("ConsiderationAmt", amount.getText().toString());
        }
        alertDialog.show();

        Log.d(TAG, "params" + params.toString());

        params.put("ApplicantName", name.getText().toString());
        params.put("email", email.getText().toString());
        params.put("mobile_number", mobile.getText().toString());
        params.put("applicant_address", address.getText().toString());
        params.put("applicant_city_vill", city.getText().toString());
        params.put("applicant_post_office", po.getText().toString());
        params.put("applicant_district", district.getText().toString());
        params.put("applicant_pin", pin.getText().toString());


        AndroidNetworking.post(MyFileUtil.TOMCAT_URL + "panjeeyanonline/create_appointment")
                .addBodyParameter(params)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }

                            if (response.getBoolean("success")) {

                                String appointment_id = response.getString("appointment_id");
                                String officer_assigned = response.getString("Officer assigned");
                                String consideration_amount = response.getString("Consideration Amount");
                                String date_and_time = response.getString("Appointment Date and time");
                                String registration_fee = response.getString("Registration Fee");

                                try {
                                    registrationFee = Integer.parseInt(registration_fee);
                                    appointment_ID = appointment_id;

                                } catch (Exception ex) {
                                }


                                String stamp_duty = response.getString("Stamp Duty");


                                showInputDialog(appointment_id, officer_assigned, consideration_amount,
                                        date_and_time, registration_fee, stamp_duty);

                                uploadFiles();
                            } else {
                                StringBuilder stringBuilder = new StringBuilder();
                                JSONArray jsonArray = response.getJSONArray("errormsg");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    stringBuilder.append(jsonArray.get(i));
                                }

                                showErrorMessage(getString(R.string.err), stringBuilder.toString());
                            }

                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        showErrorMessage(getString(R.string.net_err), error.getMessage());
                    }
                });
    }

    private void showInputDialog(final String appointment_id, String officer_assigned, String consideration_amount,
                                 String date_and_time, String registration_fee, String stamp_duty) {

        // layout of fee Dialog
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_view_status_dialog, null);

        appointmentID = view.findViewById(R.id.your_appointment_id);
        showID = view.findViewById(R.id.showid);
        details = view.findViewById(R.id.allDetails);

        appointmentID.setText(getString(R.string.appointment_id_msg));
        showID.setText(appointment_id);
        details.setText(getString(R.string.details1) + " " + date_and_time + " " + getString(R.string.details2) + " " +
                getString(R.string.details3) + " " + getString(R.string.details4) + " " + getResources().getString(R.string.Rs) +
                String.valueOf(consideration_amount) + getString(R.string.details5) + " " + getString(R.string.details6) + " " +
                getResources().getString(R.string.Rs) + String.valueOf(registration_fee) + " " + getString(R.string.details7) + " "
                + getResources().getString(R.string.Rs) + String.valueOf(stamp_duty) + " " + getString(R.string.details8));

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeedRegistration.this);
        alertDialogBuilder.setView(view);

        // setup a dialog window
        alertDialogBuilder.setTitle(getString(R.string.deed));
        alertDialogBuilder.setCancelable(false)
                .setView(view)
                .setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // go to stage 4: Payment page
                        confirmForm.setVisibility(View.GONE);

                        if (registrationFee > 0) {
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
                            paymentForm.setVisibility(View.VISIBLE);
                            header.setText(R.string.paymentheading);
                        } else {
                            confirmAppointment(appointment_id);
                        }
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }


    private void confirmAppointment(String appointment_id) {
        alertDialog.show();

        AndroidNetworking.post(MyFileUtil.TOMCAT_URL + "panjeeyanonline/confirmAppointment")
                .addBodyParameter("appointment_id", appointment_id.trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: " + response);

                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        try {
                            if (response.getBoolean("success") && response.getInt("update") > 0) {
                                // appointment confirmed
                                stateProgressBar.setAllStatesCompleted(true);
                                headerCard.setVisibility(View.GONE);
                                success_message.setVisibility(View.VISIBLE);


                            } else {
                                Log.d(TAG, "error: " + response.getInt("update"));
                                /// error
                            }
                        } catch (Exception ex) {
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        showErrorMessage(getString(R.string.net_err), error.getMessage());
                    }
                });
    }


    private void uploadFiles() {

        // network call to write info in the database and then payment activity is started

        Map<String, String> fileNames = new HashMap<>();
        fileNames.put("address-proof", addressProofFile.getName());
        fileNames.put("age-proof", ageProofFile.getName());
        fileNames.put("id-proof", idProofFile.getName());


        Log.d(TAG, "File names: " + fileNames.toString());


        AndroidNetworking.upload(MyFileUtil.TOMCAT_URL + "panjeeyanonline/fileupload")
                .addMultipartFile("id", idProofFile)
                .addMultipartFile("age", ageProofFile)
                .addMultipartFile("address", addressProofFile)
                .addMultipartParameter(fileNames)
                .setTag(TAG_UPLOAD_TEST)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        //Log.d(TAG, "uploaded: " + bytesUploaded + "\n total: " + totalBytes);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        Log.d(TAG, "onResponse: " + response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        cookieBar.setMessage(anError.getMessage());
                        cookieBar.show();
                    }
                });
    }


    public void doPay(View view) {
        Map<String, Object> parametersMap = new HashMap<>();

        String tax = deptID.getEditText().getText().toString().trim();
        String name = payerName.getEditText().getText().toString().trim();
        String pan_no = pan.getEditText().getText().toString().trim();
        String add1 = block.getEditText().getText().toString().trim();
        String add2 = locality.getEditText().getText().toString().trim();
        String add3 = area.getEditText().getText().toString().trim();
        String pin = payerPin.getEditText().getText().toString().trim();
        String phone = payerPhone.getEditText().getText().toString().trim();
        String rem = remarks.getEditText().getText().toString().trim();

        // validate input

        parametersMap.put("TAX_ID", tax);
        parametersMap.put("PARTY_NAME", name);
        parametersMap.put("PAN_NO", pan_no);
        parametersMap.put("ADDRESS1", add1);
        parametersMap.put("ADDRESS2", add2);
        parametersMap.put("ADDRESS3", add3);
        parametersMap.put("PIN_NO", pin);
        parametersMap.put("MOBILE_NO", phone);
        parametersMap.put("REMARKS", rem);

        // amount
        parametersMap.put("AMOUNT1", String.valueOf(registrationFee));
        parametersMap.put("CHALLAN_AMOUNT", String.valueOf(registrationFee));

        // appointment id
        parametersMap.put("appointment_id", appointment_ID);


        // POST data to my backend
        AndroidNetworking.post(MyFileUtil.UWAMP_URL + "submit_payment.php")
                .addBodyParameter(parametersMap)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        try {
                            if (response.getBoolean("success")) {
                                Intent intent = new Intent(getApplicationContext(), PaymentGateway.class);
                                intent.putExtra("url", response.getString("url"));
                                intent.putExtra("bundle", response.getString("data"));
                                startActivity(intent);

                                finish();
                            }
                        } catch (Exception ex) {
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        showErrorMessage(getString(R.string.net_err), error.getMessage());
                    }
                });
    }


    public void reloadData(View view) {
        setUpApplicantSpinner();
        setUpDeedCategorySpinner();
        setUpOfficeSpinner();
        getHolidays();
    }
}