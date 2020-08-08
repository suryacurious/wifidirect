package com.example.android.wifidirect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

public class ActionConfirm extends AppCompatActivity {

    Button send,receive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_confirm);
        send = (Button)findViewById(R.id.btn_send);
        receive = (Button)findViewById(R.id.btn_receive);

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent sendintent = new Intent(ActionConfirm.this, MainActivity.class);
                startActivity(sendintent);

            }
        });

        receive.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent receiveintent = new Intent(ActionConfirm.this, WiFiDirectActivity.class);
                startActivity(receiveintent);

            }
        });
    }
}
