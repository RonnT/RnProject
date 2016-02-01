package top.titov.gas.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import top.titov.gas.MyApp;
import top.titov.gas.R;

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
