package com.example.applicationformcv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MakeAssessmentFee extends AppCompatActivity {

    Spinner deedCategorySpinner, subDeedCategorySpinner;

    List<DeedCategoryModel> deedlist = new ArrayList<>();
    private ArrayAdapter<String> subDeedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_assessment_fee);
        deedCategorySpinner = findViewById(R.id.deedCategory);
        subDeedCategorySpinner = findViewById(R.id.subdeedCategory);

        setUpDeedCategorySpinner();

        List<String> temp = new ArrayList<>();
        temp.add("Select Sub Deed Category *");

        subDeedAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item_text_colour, temp);
        subDeedAdapter.setDropDownViewResource(R.layout.spinner_item_text_colour);
        subDeedCategorySpinner.setAdapter(subDeedAdapter);

        deedCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    DeedCategoryModel obj = deedlist.get(position - 1); //getting the model from the deedlist & storing in obj
                    int code = obj.getCode(); //storing the code in variable code

                    final String url = "http://192.168.43.210:8080/e-Panjeeyan/getsubdeed?codeVal=" + code;

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        //response from servlet is kept in the JSONArray array
                                        JSONArray array = new JSONArray(response);

                                        List<String> subDeed = new ArrayList<>();
                                        subDeed.add("Select Sub Deed Category *");

                                        //array list is populated from JSON array
                                        for (int i = 0; i < array.length(); i++) {
                                            subDeed.add(array.getString(i));
                                        }

                                        subDeedAdapter.clear();
                                        subDeedAdapter.addAll(subDeed);
                                        subDeedAdapter.notifyDataSetChanged();


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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setUpDeedCategorySpinner() {

        final String url = "http://192.168.43.210:8080/e-Panjeeyan/getdeedcategory";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Type collectionType = new TypeToken<List<DeedCategoryModel>>() {
                        }.getType();
                        deedlist = new Gson().fromJson(response, collectionType);

                        // ArrayList of String which contains the category names
                        List<String> list = new ArrayList<>();
                        list.add("Select Deed Category *");

                        Iterator<DeedCategoryModel> iterator = deedlist.iterator();

                        while (iterator.hasNext()) {
                            DeedCategoryModel model = iterator.next();
                            list.add(model.getSection());
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
                    }
                });

        // Add request to the Request queue
        MySingleton.getInstance(getApplicationContext())
                .addToRequestQueue(stringRequest);


    }
}
