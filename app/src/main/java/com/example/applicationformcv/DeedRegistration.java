package com.example.applicationformcv;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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

import id.zelory.compressor.Compressor;

public class DeedRegistration extends AppCompatActivity {

    private static final String TAG_UPLOAD_TEST = "uploadTest";
    Map<String, String> params;
    AlertDialog alertDialog;

    public static final String TAG = "MY-APP";
    public static final String Date_array = "Date";
    public static final String JSON_ARRAY = "result";
    CookieBar.Builder cookieBar;
    private JSONArray result;

    Spinner mySpinnerDate;

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


    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    //for validation
    TextInputLayout nameLayout, mobileLayout, addressLayout, cityLayout, poLayout, districtLayout, pinLayout;
    private EditText name, email, mobile, address, city, po, district, pin;
    private Button submit;

    TextView forSale, forSalePurchase, forLandFlat;
    EditText amount;
    RadioGroup radioGroupMaleFemale;
    RadioGroup radioUrbanRural;

    Spinner mySpinner1;
    Spinner mySpinner2, mySpinner3, mySpinner4;
    Spinner mySpinner5;
    ArrayAdapter<String> myAdapter5;
    ArrayAdapter<String> subDeedAdapter;

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

    private boolean appointmentFormCompleted = false;
    private boolean uploadDocumentsCompleted = false;

    TextView address_name, age_name, id_name;

    List<DeedCategoryModel> deedlist = new ArrayList<>();
    List<ApplicantTypeModel> applicantList = new ArrayList<>();
    List<RegistrationOfficeModel> officeList = new ArrayList<>();

    TextView show_appointmenID, show_appointmentDetails;
    TextView appointmentID, showID, details;

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

        //No internet connection error
        cookieBar = CookieBar.build(DeedRegistration.this)
                .setTitle("Network Error")
                .setTitleColor(android.R.color.white)
                .setBackgroundColor(R.color.colorPrimary)
                .setIcon(R.drawable.ic_icon)
                .setEnableAutoDismiss(true)
                .setCookiePosition(CookieBar.TOP)
                .setSwipeToDismiss(true);


        setTitle(getString(R.string.deed));
        appointmentForm = findViewById(R.id.appoint_form);
        uploadDocumentsForm = findViewById(R.id.document_upload_form);
        confirmForm = findViewById(R.id.confirm_form);
        header = findViewById(R.id.header_text2);

        mySpinner1 = findViewById(R.id.spinner1);               // applicant type spinner
        mySpinner2 = findViewById(R.id.spinner2);               // office registration spinner
        mySpinner3 = findViewById(R.id.spinner3);               // deed category spinner

        mySpinner4 = findViewById(R.id.spinner4);               // sub deed category spinner

        List<String> temp = new ArrayList<>();
        temp.add(getString(R.string.subdeedSpinner));

        subDeedAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_text_colour, temp);
        subDeedAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner4.setAdapter(subDeedAdapter);

        mySpinnerDate = findViewById(R.id.spinnerDate);         // date spinner

        setUpApplicantSpinner();
        setUpDeedCategorySpinner();
        setUpOfficeSpinner();
        getHolidays();


        cardView = findViewById(R.id.card1);

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


        // applicant type spinner listener
        mySpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    ApplicantTypeModel obj = applicantList.get(position - 1); //getting the model from the list & storing in obj
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


//        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
//                getResources().getStringArray(R.array.selectOffice));
//        myAdapter2.setDropDownViewResource(R.layout.spinner_item_text_colour);
//        mySpinner2.setAdapter(myAdapter2);


        mySpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();

                if (position > 0) {
                    Log.d(TAG, deedlist.get(position - 1).toString()); //model list and category list are not in same order

                    DeedCategoryModel obj = deedlist.get(position - 1); //getting the model from the deedlist & storing in obj
                    int code = obj.getCode(); //storing the code in variable code

                    params.put("Deedtype", String.valueOf(code));

                    final String url = "http://192.168.43.210:8080/e-Panjeeyan/getsubdeed?codeVal=" + code;


                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        //response from servlet is kept in the JSONArray array
                                        JSONArray array = new JSONArray(response);

                                        List<String> subDeed = new ArrayList<>();
                                        subDeed.add(getString(R.string.subdeedSpinner));

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

        myAdapter5 = new ArrayAdapter<String>(this, R.layout.spinner_item_text_colour,
                getResources().getStringArray(R.array.doc_type));
        myAdapter5.setDropDownViewResource(R.layout.spinner_item_text_colour);
        mySpinner5.setAdapter(myAdapter5);

        //validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

//        //adding validation to edittexts
//        awesomeValidation.addValidation(this, R.id.applicantName, "^[A-Za-z]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
//        awesomeValidation.addValidation(this, R.id.number, "^[0-9]{2}[0-9]{8}$", R.string.mobileerror);
//        awesomeValidation.addValidation(this, R.id.addressText, "[a-zA-Z0-9\\s\\.\\-]+", R.string.addresserror);
//        awesomeValidation.addValidation(this, R.id.city, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.cityerror);
//        awesomeValidation.addValidation(this, R.id.postofficeName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.poerror);
//        awesomeValidation.addValidation(this, R.id.districtName, "[A-Za-z]{1,}[A-Za-z\\s]{0,}$", R.string.districterror);
//        awesomeValidation.addValidation(this, R.id.pin, "(\\d{6})", R.string.pinerror);
//
//        awesomeValidation.addValidation(this, R.id.username, "(^$|^.*@.*\\..*$)", R.string.emailerror);


        stateProgressBar = findViewById(R.id.state_progress_bar);
        String[] descriptionData = {getString(R.string.appointform), getString(R.string.upload), getString(R.string.confirm)};
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

        //
        mySpinner5.setAdapter(null);
        mySpinner5.setAdapter(myAdapter5);
        mySpinner5.setVisibility(View.VISIBLE);
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
                        //addressimgError.setText(displayName);
                        //addressimgError.setVisibility(View.VISIBLE);
                        address_name.setText(displayName);

                        addressimgError.setError(null);
                    } else if (doc_type == AGE_PROOF_REQUEST) {
                        ageImageView.setImageResource(R.drawable.pdf);
                        ageProofSelected = true;
                        //ageimgError.setText(displayName);
                        //ageimgError.setVisibility(View.VISIBLE);
                        age_name.setText(displayName);

                        ageimgError.setError(null);
                    } else if (doc_type == IDENTITY_PROOF_REQUEST) {

                        idImageView.setImageResource(R.drawable.pdf);
                        identityProofSelected = true;
                        //idimgError.setText(displayName);
                        // idimgError.setVisibility(View.VISIBLE);
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
        final String url = "http://192.168.43.210:8080/panjeeyanonline/getAppointmentDates";
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
                                        DeedRegistration.this,
                                        R.layout.spinner_item_text_colour,
                                        dateList
                                );
                                dateAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                                mySpinnerDate.setAdapter(dateAdapter);
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


    private void setUpApplicantSpinner() {
        final String url = "http://192.168.43.210:8080/e-Panjeeyan/getapplicanttype";

        // get the applicantType from the database
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Type collectionType = new TypeToken<List<ApplicantTypeModel>>() {
                        }.getType();
                        applicantList = new Gson().fromJson(response, collectionType);

                        ArrayList<String> typeList = new ArrayList<>();
                        typeList.add(getString(R.string.apptypeSpinner));

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
                        showErrorMessage(getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }

    private void setUpOfficeSpinner() {

        final String url = "http://192.168.43.210:8080/e-Panjeeyan/registrationoffice";

        // get the registration office from the database
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Type collectionType = new TypeToken<List<RegistrationOfficeModel>>() {
                        }.getType();
                        officeList = new Gson().fromJson(response, collectionType);

                        ArrayList<String> list = new ArrayList<>();
                        list.add(getString(R.string.sroSpinner));

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
                        showErrorMessage(getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

    }


    private void showErrorMessage(String msg) {
        Log.d(TAG, "showErrorMessage");

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


    private void setUpDeedCategorySpinner() {
        final String url = "http://192.168.43.210:8080/e-Panjeeyan/getdeedcategory";

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
                        list.add(getString(R.string.deedSpinner));

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
                        showErrorMessage(getVolleyErrorMessage(error));
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);

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

            if ((selectedTextView.getText().toString().equalsIgnoreCase("Select Applicant Type *")) ||
                    (selectedTextView.getText().toString().equalsIgnoreCase("आवेदक प्रकार का चयन करें *"))) {
                selectedTextView.setFocusable(true);
                selectedTextView.setClickable(true);
                selectedTextView.setFocusableInTouchMode(true);
                selectedTextView.setError(getString(R.string.spinnerError));

                result = false;
            }
        }


        if (selectedView2 instanceof TextView) {

            TextView selectedTextView2 = (TextView) selectedView2;

            if ((selectedTextView2.getText().toString().equalsIgnoreCase("Select Office for Registration *")) ||
                    (selectedTextView2.getText().toString().equalsIgnoreCase(""))) {
                selectedTextView2.setFocusable(true);
                selectedTextView2.setClickable(true);
                selectedTextView2.setFocusableInTouchMode(true);
                selectedTextView2.setError(getString(R.string.spinnerError));

                result = false;
            }
        }


        if (selectedView3 instanceof TextView) {

            TextView selectedTextView3 = (TextView) selectedView3;

            if ((selectedTextView3.getText().toString().equalsIgnoreCase("Select Deed Category *")) ||
                    (selectedTextView3.getText().toString().equalsIgnoreCase("डीड श्रेणी का चयन करें *"))) {
                selectedTextView3.setFocusable(true);
                selectedTextView3.setClickable(true);
                selectedTextView3.setFocusableInTouchMode(true);
                selectedTextView3.setError(getString(R.string.spinnerError));

                result = false;
            }
        }

        if (selectedView4 instanceof TextView) {

            TextView selectedTextView4 = (TextView) selectedView4;

            if ((selectedTextView4.getText().toString().equalsIgnoreCase("Select Deed Sub Category *")) ||
                    (selectedTextView4.getText().toString().equalsIgnoreCase("सब डीड श्रेणी का चयन करें *"))) {
                selectedTextView4.setFocusable(true);
                selectedTextView4.setClickable(true);
                selectedTextView4.setFocusableInTouchMode(true);
                selectedTextView4.setError(getString(R.string.spinnerError));

                //
                result = false;
            }
        }

        if (selectedView5 instanceof TextView) {

            TextView selectedTextView5 = (TextView) selectedView5;

            if ((selectedTextView5.getText().toString().equalsIgnoreCase("Select Appointment Date *")) ||
                    (selectedTextView5.getText().toString().equalsIgnoreCase("अपॉइंटमेंट तिथि का चयन करें *"))) {
                selectedTextView5.setFocusable(true);
                selectedTextView5.setClickable(true);
                selectedTextView5.setFocusableInTouchMode(true);
                selectedTextView5.setError(getString(R.string.spinnerError));

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

    private void initializeValidation() {


        nameLayout = findViewById(R.id.applicantNamewrapper);
        mobileLayout = findViewById(R.id.numberWrapper);
        addressLayout = findViewById(R.id.addressWrapper);
        cityLayout = findViewById(R.id.cityNamewrapper);
        poLayout = findViewById(R.id.postofficeNamewrapper);
        districtLayout = findViewById(R.id.districtNamewrapper);
        pinLayout = findViewById(R.id.pinWrapper);

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
                } else if (!validateUploadDocumentsForm()) {
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
                header.setText(R.string.appointform);
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

    // go to the next form
    public void submitIt(View view) {

        if (name.getText().toString().isEmpty()) {
            nameLayout.setError("\t\t\t\t\tPlease enter this required field");
        } else {
            nameLayout.setError(null);
        }

        if (mobile.getText().toString().isEmpty()) {
            mobileLayout.setError("\t\t\t\t\tPlease enter this required field");
        } else {
            mobileLayout.setError(null);
        }

        if (address.getText().toString().isEmpty()) {
            addressLayout.setError("\t\t\t\t\tPlease enter this required field");
        } else {
            addressLayout.setError(null);
        }

        if (city.getText().toString().isEmpty()) {
            cityLayout.setError("\t\t\t\t\tPlease enter this required field");
        } else {
            cityLayout.setError(null);
        }

        if (po.getText().toString().isEmpty()) {
            poLayout.setError("\t\t\t\t\tPlease enter this required field");
        } else {
            poLayout.setError(null);
        }

        if (district.getText().toString().isEmpty()) {
            districtLayout.setError("\t\t\t\t\tPlease enter this required field");
        } else {
            districtLayout.setError(null);
        }

        if (pin.getText().toString().isEmpty()) {
            pinLayout.setError("\t\t\t\t\tPlease enter this required field");
        } else {
            pinLayout.setError(null);
        }

        // get the current state number
        int curState = stateProgressBar.getCurrentStateNumber();

        showFrom(curState, curState + 1);


    }

    public void submitData(View view) {

        // mark all the states "done"
        stateProgressBar.setAllStatesCompleted(true);

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


        AndroidNetworking.post("http://192.168.43.210:8080/panjeeyanonline/create_appointment")
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

                            Log.d(TAG, "onResponse: " + response);

                            if (response.getBoolean("success")) {

                                String appointment_id = response.getString("appointment_id");
                                String officer_assigned = response.getString("Officer assigned");
                                String consideration_amount = response.getString("Consideration Amount");
                                String date_and_time = response.getString("Appointment Date and time");
                                String registration_fee = response.getString("Registration Fee");
                                String stamp_duty = response.getString("Stamp Duty");


                                showInputDialog(appointment_id, officer_assigned, consideration_amount,
                                        date_and_time, registration_fee, stamp_duty);

                                uploadFiles();


                            } else {
                                showErrorMessage(response.getString("msg"));
                            }

                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        cookieBar.setMessage(error.getMessage());
                        cookieBar.show();

                        Log.d(TAG, "onError: " + error.getErrorCode());
                    }
                });
    }

    private void showInputDialog(String appointment_id, String officer_assigned, String consideration_amount,
                                 String date_and_time, String registration_fee, String stamp_duty) {

        // layout of fee Dialog
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_view_status_dialog, null);

        appointmentID = view.findViewById(R.id.your_appointment_id);
        showID = view.findViewById(R.id.showid);
        details = view.findViewById(R.id.allDetails);

        appointmentID.setText("Your appointment id is :");
        showID.setText(appointment_id);
        details.setText("You are requested to report 15 minutes before the mentioned date and time i.e., on " + date_and_time + "." +
                " Under the " + "sub registrar office, a consideration amount of " + getResources().getString(R.string.Rs) +
                String.valueOf(consideration_amount) + "," + " a registration fee of " + getResources().getString(R.string.Rs) +
                String.valueOf(registration_fee) + " and a stamp duty of " + getResources().getString(R.string.Rs) +
                String.valueOf(stamp_duty) + " is to be paid.");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeedRegistration.this);
        alertDialogBuilder.setView(view);

        // setup a dialog window
        alertDialogBuilder.setTitle("View Appointment Status");
        alertDialogBuilder.setCancelable(false)
                .setView(view)
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DeedRegistration.this, PaymentActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    private void uploadFiles() {

        // network call to write info in the database and then payment activity is started

        Map<String, String> fileNames = new HashMap<>();
        fileNames.put("address-proof", addressProofFile.getName());
        fileNames.put("age-proof", ageProofFile.getName());
        fileNames.put("id-proof", idProofFile.getName());


        Log.d(TAG, "File names: " + fileNames.toString());


        AndroidNetworking.upload("http://192.168.43.210:8080/panjeeyanonline/fileupload")
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
}