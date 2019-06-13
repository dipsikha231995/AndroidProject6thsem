package com.example.applicationformcv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    // This map will contain all input parameters
    // and will get POSTed to eGras

    private Map<String, Object> parametersMap;
    EditText deptID, name, pan, block, locality, area, pin, num, remarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        deptID = findViewById(R.id.number);
        name = findViewById(R.id.name);
        pan = findViewById(R.id.pan);
        block = findViewById(R.id.block);
        locality = findViewById(R.id.locality);
        area = findViewById(R.id.area);
        pin = findViewById(R.id.pin);
        num = findViewById(R.id.mobile);
        remarks = findViewById(R.id.remarks);


        parametersMap = new HashMap<>();
        parametersMap.put("TREASURY_CODE", "BIL");
        parametersMap.put("MAJOR_HEAD", "0029");

        parametersMap.put("DEPT_CODE", "LRS");
        parametersMap.put("OFFICE_CODE","LRS000");
        parametersMap.put("SUB_SYSTEM","LRC-EMOJNI");
        parametersMap.put("DEPARTMENT_ID","Ele3000");

        parametersMap.put("PAYMENT_TYPE","01");
        parametersMap.put("REC_FIN_YEAR","2019-2020");
        parametersMap.put("PERIOD","A");
        parametersMap.put("FROM_DATE","01/04/2019");
        parametersMap.put("TO_DATE","31/03/2020");
        parametersMap.put("HOA1","0029-00-800-7094-202-01");
        parametersMap.put("AMOUNT1","12");
        parametersMap.put("CHALLAN_AMOUNT","12");

    }

    public void doSubmit(View view) {

        parametersMap.put("TAX_ID",deptID.getText().toString());
        parametersMap.put("PARTY_NAME",name.getText().toString());
        parametersMap.put("PAN_NO",pan.getText().toString());
        parametersMap.put("ADDRESS1",block.getText().toString());
        parametersMap.put("ADDRESS2",locality.getText().toString());
        parametersMap.put("ADDRESS3",area.getText().toString());
        parametersMap.put("PIN_NO",pin.getText().toString());
        parametersMap.put("MOBILE_NO",num.getText().toString());
        parametersMap.put("REMARKS",remarks.getText().toString());

        //to replace the old format of the string , { } to post to eGras URL
        String data = parametersMap.toString()
                .replaceAll(", ", "&")
                .replace("{", "")
                .replace("}", "");

        Intent intent = new Intent(getApplicationContext(), PaymentGateway.class);
        intent.putExtra("url", "http://103.8.248.139/challan/views/frmgrnfordept.php");
        intent.putExtra("bundle", data);
        startActivity(intent);

        finish();
    }
}
