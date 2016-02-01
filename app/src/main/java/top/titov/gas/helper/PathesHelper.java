package top.titov.gas.helper;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class PathesHelper {

    public static String getStoragePath(Context context, String subPath) {
        String state = Environment.getExternalStorageState();

        String localPath = "/data/data/" + context.getPackageName() + "/";
        String externalPath = Environment.getExternalStorageDirectory().toString() + "/Android/data/" + context.getPackageName() + "/";

        String resultPath = Environment.MEDIA_MOUNTED.equals(state) ? externalPath : localPath;

        if ( ! TextUtils.isEmpty(subPath)){
            resultPath =  resultPath + subPath + "/";
        }

        File filePath = new File(resultPath);
        if ( ! filePath.isDirectory() ) { filePath.mkdirs(); }

        return  resultPath;
    }

    public static String getStoragePath(Context context) {
        return getStoragePath(context, "");
    }
}
