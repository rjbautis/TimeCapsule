package com.android121.timecapsule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class PaymentDetails extends AppCompatActivity {

    TextView textID, textAmount, textStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        textID = (TextView) findViewById(R.id.textID);
        textAmount = (TextView) findViewById(R.id.textAmount);
        textStatus = (TextView) findViewById(R.id.textStatus);

        //get Intent
        Intent intent = getIntent();

        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            textID.setText(response.getString("id"));
            textStatus.setText(response.getString("state"));
            textAmount.setText("$" + paymentAmount);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
