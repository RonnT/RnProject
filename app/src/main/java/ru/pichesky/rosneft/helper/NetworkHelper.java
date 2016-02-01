package ru.pichesky.rosneft.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;

public class NetworkHelper {
    private NetworkHelper(){}
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MyApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNetworkAvailableWToast(){
        if (!isNetworkAvailable()){
            ToastHelper.showToast(R.string.error_check_connection);
            return false;
        }
        return true;
    }
}
