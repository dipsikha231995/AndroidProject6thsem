package com.example.applicationformcv;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.github.ybq.android.spinkit.style.Wave;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.Map;

public class ViewStatus extends AppCompatActivity {

    AlertDialog alertDialog;

    CookieBar.Builder cookieBar;

    EditText refNo;

    Button check;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);
        setTitle(getString(R.string.view));
        refNo = findViewById(R.id.number);
        check = findViewById(R.id.btnCheck);

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

        alertDialog.show();

//        if (awesomeValidation.validate()) { }

        //VOLLEY POST
        String url = "http://192.168.43.210:8080/e-Panjeeyan/viewstatusservlet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                Toast.makeText(ViewStatus.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                showErrorMessage(getVolleyErrorMessage(error));
            }
        }) {
            protected Map<String, String> getParams() {
                String number = refNo.getText().toString();
                Log.d("check", number);
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("refno", number); //Add the data you'd like to send to the server.
                return MyData;
            }
        };

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
}
