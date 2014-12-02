package com.technicallycovered.oneexperience.qpair;

import android.os.RemoteException;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;

public interface ICallback
{
    public void callback(IPeerContext peerContext, IPeerIntent peerIntent, IPeerIntent callback) throws RemoteException;
}
