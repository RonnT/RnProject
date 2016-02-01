package ru.pichesky.rosneft.helper;

import android.widget.Toast;

import ru.pichesky.rosneft.MyApp;

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
