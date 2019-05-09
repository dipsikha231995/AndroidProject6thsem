package com.example.applicationformcv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

public class ViewStatus extends AppCompatActivity {

    EditText refNo;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);
        setTitle(getString(R.string.view));
        refNo = findViewById(R.id.number);

        //validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

//        awesomeValidation.addValidation(this, R.id.number, "\\d+", R.string.refNumber);

    }

    public void doReset(View view) {
        refNo.setText("");
    }

    public void checkStatus(View view) {

        if(awesomeValidation.validate()){

       Toast.makeText(this, "Appointment Status is:", Toast.LENGTH_SHORT).show();
//            Intent next2 = new Intent(this, AppointmentConfirmed.class);
//            startActivity(next2);

        }
    }
}
