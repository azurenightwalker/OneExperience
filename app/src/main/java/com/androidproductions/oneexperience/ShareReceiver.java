package com.androidproductions.oneexperience;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;


public class ShareReceiver extends Activity {

    static private String ACTION_CALLBACK = "com.androidproductions.oneexperience.ACTION_CALLBACK";
    static private String ACTION_PEER = "com.androidproductions.oneexperience.ACTION_PEER";
    static private String TAG = "ONEEXPERIENCE";

    private Intent receivedIntent;
    private String receivedType;
    private BroadcastReceiver callbackReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        receivedType = receivedIntent.getType();
        //make sure it's an action and type we can handle
        if(receivedAction.equals(Intent.ACTION_SEND)){
            Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
            bindService(intent, new QPairServiceConnection(), 0);
        }
        else {
            //app has been launched a different way. Should not happen
            finish();
        }
    }

    public class QPairServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            constructIntent(service);
            unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Do something when the connection is lost.
        }
    }

    @Override
    protected void onResume() {
        registerCallback();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(callbackReceiver);
    }

    private void constructIntent(IBinder service) {

        IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

        try {
            IPeerIntent peerIntent = peerContext.newPeerIntent();

            // set the Activity class to be invoked on the peer.
            peerIntent.setClassName(getPackageName(), ACTION_PEER);

            // create an IPeerIntent for the callback.
            IPeerIntent callback = peerContext.newPeerIntent();

            // set the action for the callback.
            callback.setAction(ACTION_CALLBACK);

            handleData(peerIntent);

            // call startActivityOnPeer() with an IPeerIntent using IPeerContext.
            peerContext.startActivityOnPeer(peerIntent, callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void handleData(IPeerIntent peerIntent) throws RemoteException {

        peerIntent.putStringExtra("MIME_TYPE", receivedType);

        if(receivedType.startsWith("text/")){
            //handle sent text
            String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        }
        else if(receivedType.startsWith("image/")){
            //handle sent image
            Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        }
    }

    private void registerCallback()
    {
        callbackReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // retrieve the error cause
                String errorMessage = intent.getStringExtra(QPairConstants.EXTRA_CAUSE);
                Log.e(TAG, errorMessage);
            }
        };

        //Register callback receiver for call back intent
        registerReceiver(callbackReceiver,
                new IntentFilter(ACTION_CALLBACK));
    }
}
