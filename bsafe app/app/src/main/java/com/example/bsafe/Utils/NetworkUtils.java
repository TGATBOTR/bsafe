package com.example.bsafe.Utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils
{
    public final Application application;

    public NetworkUtils(Application application)
    {
        this.application = application;
    }

    public boolean deviceIsConnectedToInternet()
    {
        // See https://stackoverflow.com/questions/57284582/networkinfo-has-been-deprecated-by-api-29
        boolean ret = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null)
            {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                ret = networkCapabilities != null
                        &&(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        else
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            ret = (networkInfo != null && networkInfo.isConnected());
        }

        return ret;
    }
}
