package com.sifast.gps.tracking;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;

/**
 * Created by ghassen.ati on 25/03/2016.
 */
public class NetworkingManager {
    private static final String TAG = "NetworkingManager";
    private Activity activity;

    public NetworkingManager(Activity activity){
        this.activity = activity;
    }
    public boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager
                = (android.net.ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
