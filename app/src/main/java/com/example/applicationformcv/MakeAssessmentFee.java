package com.example.applicationformcv;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
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
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import fr.ganfra.materialspinner.MaterialSpinner;

public class MakeAssessmentFee extends AppCompatActivity {

    public static final String TAG = "MY-APP";
    private static final String TAG_FEE_ASSESSMENT = "fee-assessment";
    TextInputLayout amountLayout;
    EditText considerationAmt;
    Map<String, String> params;
    RadioGroup radioGroupPurchase, radioGroupLocation;
    AlertDialog alertDialog;
    CookieBar.Builder cookieBar;
    CardView appointmentCard;
    TextView showAmount, showStampFee, showRegFee;
    TextView forSale, forSalePurchase, forLandFlat;

    MaterialSpinner deedCategorySpinner, subDeedCategorySpinner;

    List<DeedCategoryModel> deedlist = new ArrayList<>();

    private ArrayAdapter<String> subDeedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_assessment_fee);

        // to store all the parameters
        params = new HashMap<>();

        radioGroupPurchase = findViewById(R.id.radioGroup1);
        radioGroupLocation = findViewById(R.id.radioGroup2);

        appointmentCard = findViewById(R.id.appoint_form);

        showAmount = findViewById(R.id.showAmount);
        showStampFee = findViewById(R.id.showStamp);
        showRegFee = findViewById(R.id.showRegfee);

        forSale = findViewById(R.id.extraForSale);
        forSalePurchase = findViewById(R.id.extraForSalePur);
        forLandFlat = findViewById(R.id.landFlat);


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
        cookieBar = CookieBar.build(MakeAssessmentFee.this)
                .setTitle("Network Error")
                .setTitleColor(android.R.color.white)
                .setBackgroundColor(R.color.colorPrimary)
                .setIcon(R.drawable.ic_icon)
                .setEnableAutoDismiss(true)
                .setCookiePosition(CookieBar.TOP)
                .setSwipeToDismiss(true);

        amountLayout = findViewById(R.id.considerationWrapper);
        considerationAmt = findViewById(R.id.considerationAmt);

        setTitle(getString(R.string.title_check_fee));
        deedCategorySpinner = findViewById(R.id.deedCategory);
        subDeedCategorySpinner = findViewById(R.id.subdeedCategory);

        setUpDeedCategorySpinner();

        List<String> temp = new ArrayList<>();

        subDeedAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_text_colour, temp);

        subDeedAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
        subDeedCategorySpinner.setAdapter(subDeedAdapter);

        deedCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {

                    String category = parent.getItemAtPosition(position).toString();

                    DeedCategoryModel obj = deedlist.get(position); //getting the model from the deedlist & storing in obj
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
                                        //subDeed.add(getString(R.string.subdeedSpinner));

                                        //array list is populated from JSON array
                                        for (int i = 0; i < array.length(); i++) {
                                            subDeed.add(array.getString(i));
                                        }

                                        subDeedAdapter = new ArrayAdapter<String>(MakeAssessmentFee.this,
                                                R.layout.spinner_item_text_colour,
                                                subDeed);
                                        subDeedAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                                        subDeedCategorySpinner.setAdapter(subDeedAdapter);

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


                    if (category.equalsIgnoreCase("SALE") || category.equalsIgnoreCase("GIFT")) {
                        forSale.setVisibility(View.VISIBLE);
                        //amount.setVisibility(View.VISIBLE);

                        findViewById(R.id.considerationWrapper).setVisibility(View.VISIBLE);

                        forSalePurchase.setVisibility(View.VISIBLE);
                        radioGroupPurchase.setVisibility(View.VISIBLE);
                        forLandFlat.setVisibility(View.VISIBLE);
                        radioGroupLocation.setVisibility(View.VISIBLE);
                    } else {
                        forSale.setVisibility(View.GONE);
                        //amount.setVisibility(View.GONE);

                        considerationAmt.setText("0");
                        considerationAmt.setError(null);
                        findViewById(R.id.considerationWrapper).setVisibility(View.GONE);

                        //

                        forSalePurchase.setVisibility(View.GONE);
                        radioGroupPurchase.setVisibility(View.GONE);
                        forLandFlat.setVisibility(View.GONE);
                        radioGroupLocation.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subDeedCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                params.put("Subdeedtype", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        radioGroupPurchase.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String gender = "";

                switch (checkedId) {
                    case R.id.radio_both:
                        gender = "MF";
                        break;

                    case R.id.radio_onlyFemale:
                        gender = "F";
                        break;

                    case R.id.radio_onlyMale:
                        gender = "M";
                        break;
                }

                if (!gender.isEmpty()) {
                    params.put("Gender", gender);
                }
            }
        });

        radioGroupLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String landlocated = "";

                switch (checkedId) {
                    case R.id.urbanMCA:
                        landlocated = "UG";
                        break;

                    case R.id.urbanMBA:
                        landlocated = "UM";
                        break;

                    case R.id.rural:
                        landlocated = "R";
                        break;
                }

                if (!landlocated.isEmpty()) {
                    params.put("UrbanRural", landlocated);
                }
            }
        });


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
//                        list.add(getString(R.string.deedSpinner));

                        for (int i = 0; i < deedlist.size(); i++) {
                            list.add(deedlist.get(i).getSection());
                        }

                        ArrayAdapter myTypeAdapter = new ArrayAdapter<String>(MakeAssessmentFee.this,
                                R.layout.spinner_item_text_colour, list);

                        myTypeAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
                        deedCategorySpinner.setAdapter(myTypeAdapter);
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


    public void submitIt(View view) {
        boolean[] isOk = new boolean[3];

        if (deedCategorySpinner.getSelectedItem() == null) {
            isOk[0] = false;
            deedCategorySpinner.setError(getString(R.string.spinnerError));
        } else {
            isOk[0] = true;
            deedCategorySpinner.setError(null);
        }

        if (subDeedCategorySpinner.getSelectedItem() == null) {
            isOk[1] = false;
            subDeedCategorySpinner.setError(getString(R.string.spinnerError));
        } else {
            isOk[1] = true;
            subDeedCategorySpinner.setError(null);
        }


        String amount = considerationAmt.getText().toString().trim();
        if (!Pattern.matches("\\d+", amount)) {
            isOk[2] = false;
            amountLayout.setError("\t\t\t\t\tPlease enter this required field");
            return;
        } else {
            isOk[2] = true;
            amountLayout.setErrorEnabled(false);
        }

        // if any array item is false, then don't let the user submit
        for (boolean item : isOk) {
            if (!item) {
                return;
            }
        }


        alertDialog.show();

        params.put("ConsiderationAmt", amount);

        Log.d(TAG, "submitIt: " + params.toString());

        AndroidNetworking.post("http://192.168.43.210:8080/panjeeyanonline/enquiry_api_demo")
                .addBodyParameter(params)
                .setTag(TAG_FEE_ASSESSMENT)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }

                        try {

                            Log.d(TAG, "success: " + response.getInt("success"));

                            JSONArray parentArray = response.getJSONArray("Result");  //Result array in json response

                            JSONObject finalObj = parentArray.getJSONObject(0);  // The object in 0th position in Result

                            int conAmount = finalObj.getInt("conAmount");
                            int stampDuty = finalObj.getInt("stampDuty");
                            int regFee = finalObj.getInt("regFee");

                            // remove the error
                            amountLayout.setErrorEnabled(false);

                            showInputDialog(conAmount, stampDuty, regFee);

                        } catch (JSONException e1) {
                            Log.d(TAG, "onResponse: " + e1.getMessage());
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

    private void showInputDialog(int conAmount, int stampDuty, int regFee) {

        // layout of fee Dialog
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_fee_dialog, null);

        TextView giveAmount, giveStampFee, giveRegFee;
        giveAmount = view.findViewById(R.id.giveAmount);
        giveStampFee = view.findViewById(R.id.giveStamp);
        giveRegFee = view.findViewById(R.id.giveRegFee);

        giveAmount.setText(getResources().getString(R.string.Rs) + String.valueOf(conAmount));
        giveStampFee.setText(getResources().getString(R.string.Rs) + String.valueOf(stampDuty));
        giveRegFee.setText(getResources().getString(R.string.Rs) + String.valueOf(regFee));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MakeAssessmentFee.this);
        alertDialogBuilder.setView(view);

        // setup a dialog window
        alertDialogBuilder.setTitle(R.string.title_check_fee);
        alertDialogBuilder.setCancelable(false)
                .setView(view)
                .setPositiveButton(R.string.done, null);

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
