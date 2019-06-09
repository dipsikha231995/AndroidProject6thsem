package com.example.applicationformcv;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.github.ybq.android.spinkit.style.Wave;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewStatus extends AppCompatActivity {

    public static final String TAG = "MY-APP";
    Map<String, String> params;

    AlertDialog alertDialog;

    CookieBar.Builder cookieBar;
    TextInputLayout referenceNoLayout;

    EditText refNo;

    Button check;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;
    TextView appointmentID, showID, details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);
        setTitle(getString(R.string.view));
        referenceNoLayout = findViewById(R.id.numberWrapper);
        refNo = findViewById(R.id.number);
        check = findViewById(R.id.btnCheck);

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

        //No internet connection error
        cookieBar = CookieBar.build(ViewStatus.this)
                .setTitle("Network Error")
                .setTitleColor(android.R.color.white)
                .setBackgroundColor(R.color.colorPrimary)
                .setIcon(R.drawable.ic_icon)
                .setEnableAutoDismiss(true)
                .setCookiePosition(CookieBar.TOP)
                .setSwipeToDismiss(true);

        //validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

//        awesomeValidation.addValidation(this, R.id.number, "\\d+", R.string.refNumber);

    }

    public void checkStatus(View view) {

        if (refNo.getText().toString().isEmpty()) {
            referenceNoLayout.setError("\t\t\t\t\tPlease enter this required field");
            return;
        } else {

            alertDialog.show();

            params.put("DocRefNo", refNo.getText().toString());

//        if (awesomeValidation.validate()) { }

            AndroidNetworking.post("http://192.168.43.210:8080/panjeeyanonline/appointment_status")
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

                                Log.d(TAG, "success: " + response);

                                if (response.getBoolean("success")) {

                                    String appointment_id = response.getString("Appointment ID");
                                    String date_and_time = response.getString("Appointment Date and time");
                                    String registry_office = response.getString("Registry Office");
                                    String registration_fee = response.getString("Registration Fee");
                                    String stamp_duty = response.getString("Stamp Duty");

                                    // remove the error
                                    referenceNoLayout.setErrorEnabled(false);

                                    showInputDialog(appointment_id, date_and_time, registry_office, registration_fee, stamp_duty);

                                } else {

                                    String msg = response.getString("msg");

                                    showInputDialog(msg);
                                }

                            } catch (JSONException e) {
                            }

                        }

                        @Override
                        public void onError(ANError error) {

                            showErrorMessage(error.getMessage());

                            Log.d(TAG, "onError: " + error.getErrorCode());
                            Log.d(TAG, "onError: " + error.getMessage());

                        }

                    });
        }
    }

    private void showInputDialog(String appointment_id, String date_and_time, String registry_office,
                                 String registration_fee, String stamp_duty) {


        // layout of fee Dialog
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_view_status_dialog, null);

        appointmentID = view.findViewById(R.id.your_appointment_id);
        showID = view.findViewById(R.id.showid);
        details = view.findViewById(R.id.allDetails);

        appointmentID.setText("Your appointment id is :");
        showID.setText(appointment_id);
        details.setText("You are requested to report 15 minutes before the mentioned date and time i.e., on " + date_and_time + "." +
                " Under the " + registry_office + "sub registrar office, a registration fee of " +
                getResources().getString(R.string.Rs) + String.valueOf(registration_fee) +
                " and a stamp duty of " + getResources().getString(R.string.Rs) + String.valueOf(stamp_duty) + " is to be paid.");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewStatus.this);
        alertDialogBuilder.setView(view);

        // setup a dialog window
        alertDialogBuilder.setTitle("View Appointment Status");
        alertDialogBuilder.setCancelable(false)
                .setView(view)
                .setPositiveButton("DONE", null);

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    private void showInputDialog(String msg) {

        Log.d(TAG, "showInputDialog: " + msg);

        // layout of fee Dialog
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_view_status_dialog, null);

        appointmentID = view.findViewById(R.id.showid);
        appointmentID.setText(msg);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewStatus.this);
        alertDialogBuilder.setView(view);

        // setup a dialog window
        alertDialogBuilder.setTitle("View Appointment Status");
        alertDialogBuilder.setCancelable(false)
                .setView(view)
                .setPositiveButton("DONE", null);

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
