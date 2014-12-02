package com.technicallycovered.oneexperience;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class DataReceiver extends Activity {

    private Intent receivedIntent;
    private String receivedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_data_receiver);

        receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        receivedType = receivedIntent.getType();
        //make sure it's the intended action
        if(receivedAction.equals(OEConstants.ACTION_PEER)){
            handleData();
        }
        else {
            //app has been launched a different way. Should not happen
            finish();
        }
    }

    private void handleData() {
        if(receivedType.startsWith("text/")){
            //handle sent text
            String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        }
    }
}
