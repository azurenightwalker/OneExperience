package com.androidproductions.oneexperience;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

import com.lge.qpair.api.r1.QPairConstants;


public class DataReceiver extends Activity {

    private Intent receivedIntent;
    private String receivedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_receiver);

        receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        receivedType = receivedIntent.getType();
        //make sure it's an action and type we can handle
        if(receivedAction.equals(Intent.ACTION_SEND)){
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
        else if(receivedType.startsWith("image/")){
            //handle sent image
            Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        }
    }
}
