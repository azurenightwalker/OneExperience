package com.androidproductions.oneexperience;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ShareReceiver extends Activity {



    private Intent receivedIntent;
    private String receivedType;

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

    private void constructIntent(IBinder service) {

        IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

        try {
            IPeerIntent peerIntent = peerContext.newPeerIntent();
            IPeerIntent callback = initCallbackIntent(peerContext);


            handleData(peerContext, peerIntent, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IPeerIntent initCallbackIntent(IPeerContext peerContext) throws RemoteException {
        // create an IPeerIntent for the callback.
        IPeerIntent callback = peerContext.newPeerIntent();
        // set the action for the callback.
        callback.setAction(OEConstants.ACTION_CALLBACK);
        return callback;
    }

    private void handleData(IPeerContext peerContext, IPeerIntent peerIntent, IPeerIntent callback) throws RemoteException, IOException {

        peerIntent.putStringExtra(OEConstants.MIMETYPE, receivedType);

        if(receivedType.startsWith("text/")){
            //handle sent text
            peerIntent.setClassName(getPackageName(), OEConstants.ACTION_PEER);
            peerIntent.putStringExtra(Intent.EXTRA_TEXT,
                    receivedIntent.getStringExtra(Intent.EXTRA_TEXT));
            peerContext.startActivityOnPeer(peerIntent, callback);
        }
        else if(receivedType.startsWith("image/")){
            //handle sent image
            Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            peerIntent.setDataAndType(saveImage(receivedUri), "image/*");

            // set service to be started on peer
            peerIntent.setPackage(OEConstants.SERVICE_PEER);

            // call APIs to send the file and Peer Intent
            peerContext.startServiceOnPeerWithFile(peerIntent,OEConstants.Folder, callback);
        }
    }

    private String saveImage(Uri receivedUri) throws IOException {
        InputStream is = getContentResolver().openInputStream(receivedUri);
        Bitmap bitmap = BitmapFactory.decodeStream(is);

        //create a file to write bitmap data
        File f = new File(getCacheDir(), receivedUri.getLastPathSegment());
        if (f.exists())
            f.delete();
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        return f.getAbsolutePath();
    }
}
