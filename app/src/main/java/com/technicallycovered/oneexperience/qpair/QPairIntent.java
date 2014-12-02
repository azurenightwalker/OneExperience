package com.technicallycovered.oneexperience.qpair;

import android.os.IBinder;
import android.os.RemoteException;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.technicallycovered.oneexperience.OEConstants;

class QPairIntent
{

    private final ICallback mCallback;

    public QPairIntent(ICallback callback) {
        mCallback = callback;
    }

    public void handle(IBinder service) {
        IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

        try {
            IPeerIntent peerIntent = peerContext.newPeerIntent();
            IPeerIntent callback = initCallbackIntent(peerContext);
            mCallback.callback(peerContext, peerIntent, callback);
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
}
