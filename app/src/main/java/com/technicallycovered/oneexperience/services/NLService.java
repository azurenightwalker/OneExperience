package com.technicallycovered.oneexperience.services;

import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.google.gson.Gson;
import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.technicallycovered.oneexperience.OEConstants;
import com.technicallycovered.oneexperience.qpair.ICallback;
import com.technicallycovered.oneexperience.qpair.PeerHelper;

public class NLService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        ICallback callback = new ICallback() {
            @Override
            public void callback(IPeerContext peerContext, IPeerIntent peerIntent, IPeerIntent callback) throws RemoteException {
                peerIntent.putStringExtra(OEConstants.MIMETYPE, "NOTIFICATION");
                peerIntent.putBooleanExtra(OEConstants.ADDED, true);
                Gson gson = new Gson();
                peerIntent.putStringExtra(OEConstants.CONTECT, gson.toJson(new ShareableNotification(sbn)));
                peerIntent.setPackage(OEConstants.SERVICE_PEER);
                peerContext.startServiceOnPeer(peerIntent, callback);
            }
        };
        PeerHelper.Send(this, callback);
    }

    @Override
    public void onNotificationRemoved(final StatusBarNotification sbn) {
        ICallback callback = new ICallback() {
            @Override
            public void callback(IPeerContext peerContext, IPeerIntent peerIntent, IPeerIntent callback) throws RemoteException {
                peerIntent.putStringExtra(OEConstants.MIMETYPE, "NOTIFICATION");
                peerIntent.putBooleanExtra(OEConstants.ADDED, true);
                Gson gson = new Gson();
                peerIntent.putStringExtra(OEConstants.CONTECT, gson.toJson(new ShareableNotification(sbn)));
                peerIntent.setPackage(OEConstants.SERVICE_PEER);
                peerContext.startServiceOnPeer(peerIntent, callback);
            }
        };
        PeerHelper.Send(this, callback);
    }
}