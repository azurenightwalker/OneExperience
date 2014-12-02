package com.technicallycovered.oneexperience.qpair;

import android.content.Context;
import android.content.Intent;

import com.lge.qpair.api.r1.QPairConstants;

public final class PeerHelper {
    private PeerHelper(){}

    public static void Send(Context context, ICallback callback)
    {
        Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
        context.bindService(intent, new QPairServiceConnection(context, new QPairIntent(callback)), 0);
    }




}

