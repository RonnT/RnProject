package top.titov.gas.helper;

import android.widget.Toast;

import top.titov.gas.MyApp;

public class ToastHelper {

    public static void showToast(int text) {
        Toast.makeText(MyApp.getAppContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String text) {
        Toast.makeText(MyApp.getAppContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String text, int duration) {
        Toast.makeText(MyApp.getAppContext(), text, duration).show();
    }

    public static void showToast(int text, int duration) {
        Toast.makeText(MyApp.getAppContext(), text, duration).show();
    }

}
