package com.madproject.leftoverfooddonation.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckConnection {
    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }
    public static boolean checkConnection(Context context){
        if(!isOnline(context)) {
            new CustomToast(Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_SHORT)).show();
            return false;
        }
        return true;
    }
}
