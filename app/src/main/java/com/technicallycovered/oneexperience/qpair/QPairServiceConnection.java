package com.technicallycovered.oneexperience.qpair;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;

public class QPairServiceConnection implements ServiceConnection {
    private final Context mContext;
    private final QPairIntent mCallback;

    public QPairServiceConnection(Context context, QPairIntent callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mCallback.handle(service);
        mContext.unbindService(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // Do something when the connection is lost.
    }
}
