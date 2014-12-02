package com.technicallycovered.oneexperience;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lge.qpair.api.r1.QPairConstants;

public class CallbackReceiver extends BroadcastReceiver {
    public CallbackReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String errorMessage = intent.getStringExtra(QPairConstants.EXTRA_CAUSE);
        Log.e(OEConstants.TAG, errorMessage);
    }
}
