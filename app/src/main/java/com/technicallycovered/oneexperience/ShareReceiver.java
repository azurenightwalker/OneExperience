package com.technicallycovered.oneexperience;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.technicallycovered.oneexperience.qpair.ICallback;
import com.technicallycovered.oneexperience.qpair.PeerHelper;

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
            PeerHelper.Send(this, mCallback);
        }
        else {
            //app has been launched a different way. Should not happen
            finish();
        }
    }

    private ICallback mCallback = new ICallback() {
        @Override
        public void callback(IPeerContext peerContext, IPeerIntent peerIntent, IPeerIntent callback) throws RemoteException {

            peerIntent.putStringExtra(OEConstants.MIMETYPE, receivedType);
            peerIntent.setPackage(OEConstants.SERVICE_PEER);

            if (receivedType.startsWith("text/")) {
                peerIntent.putStringExtra(Intent.EXTRA_TEXT,
                        receivedIntent.getStringExtra(Intent.EXTRA_TEXT));
                peerContext.startServiceOnPeer(peerIntent, callback);
            } else if (receivedType.startsWith("image/")) {
                //handle sent image
                Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

                try {
                    peerIntent.setDataAndType(saveImage(receivedUri), "image/*");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // set service to be started on peer
                peerIntent.setPackage(OEConstants.SERVICE_PEER);

                // call APIs to send the file and Peer Intent
                peerContext.startServiceOnPeerWithFile(peerIntent, OEConstants.Folder, callback);
            }
        }
    };

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
