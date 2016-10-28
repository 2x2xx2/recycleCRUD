package com.dinkominfo.recyclecrud.util;
import android.content.Context;
import android.net.ConnectivityManager;

public class InternetConnection {

    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(Context context) {
        return  ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
