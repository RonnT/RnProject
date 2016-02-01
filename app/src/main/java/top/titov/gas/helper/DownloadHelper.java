package top.titov.gas.helper;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

import java.io.File;

import top.titov.gas.MyApp;

/**
 * Created by Programmer16 on 13.08.2015.
 */
public class DownloadHelper {
    public static void downloadFile(String pURL, String pLocalPath, AjaxCallback<File> pCallBack) {
        File localFile = new File(pLocalPath);
        new AQuery(MyApp.getAppContext()).download(pURL, localFile, pCallBack);
    }
}
