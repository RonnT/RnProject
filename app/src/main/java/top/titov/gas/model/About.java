package top.titov.gas.model;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

import top.titov.gas.MyApp;
import top.titov.gas.helper.DownloadHelper;
import top.titov.gas.helper.PathesHelper;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.helper.SizeHelper;
import top.titov.gas.interfaces.ILoadingObserver;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.api.Api;

/**
 * Created by Yagupov Ruslan on 13.08.2015.
 */
public class About {

    private String name;
    private String text;
    private String image;

    private static ILoadingObserver sLoadingObserver;
    private static PrefsHelper sPrefsHelper = PrefsHelper.getInstance();

    private static final String EMPTY_IMAGE_PATH = "";

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(String pImage) {
        this.image = pImage;
    }

    public static void loadingAbout() {
        Api.getInstance().getAboutCompany(getAboutListener(), getErrorListener());
    }

    private static Response.Listener<JSONObject> getAboutListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AboutWrapper aboutWrapper = new Gson().fromJson(response.toString(), AboutWrapper.class);
                if (aboutWrapper.getErrorCode() == CONST.ERROR_CODE_OK) {
                    saveAboutCache(aboutWrapper.getAbout());
                }
            }
        };
    }

    private static Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isCacheExist()) {
                    notifyLoadingObserver(ILoadingObserver.Status.LOADING_LOCAL);
                } else notifyLoadingObserver(ILoadingObserver.Status.LOADING_FAILED);

                error.printStackTrace();
            }
        };
    }

    public static void tryLoadingExistAbout(ILoadingObserver pLoadingObserver) {
        sLoadingObserver = pLoadingObserver;
        if (!isCacheExist()) loadingAbout();
        else notifyLoadingObserver(ILoadingObserver.Status.LOADING_LOCAL);
    }

    private static boolean isCacheExist() {
        if (sPrefsHelper.getAboutName().isEmpty()) return false;
        else if (sPrefsHelper.getAboutText().isEmpty()) return false;
        else if (sPrefsHelper.getAboutImage().isEmpty()) return false;
        else {
            String localImagePath = getLocalFilePathFromRelative(sPrefsHelper.getAboutImage());
            File cacheImage = new File(localImagePath);
            if (cacheImage.length() < 1000) return false;             // In 17 build in market was bug, was cached image 1x1 px instead of full image. After half-year it can be deleted
            return cacheImage.exists();
        }
    }

    private static void saveAboutCache(About pAbout) {
        if (!sPrefsHelper.getAboutName().equals(pAbout.getName())) {
            sPrefsHelper.setAboutName(pAbout.getName());
        }
        if (!sPrefsHelper.getAboutText().equals(pAbout.getText())) {
            sPrefsHelper.setAboutText(pAbout.getText());
        }
        if (sPrefsHelper.getAboutImage().equals(pAbout.getImage()) && isCacheExist()) {
            notifyLoadingObserver(ILoadingObserver.Status.LOADING_LOCAL);
        } else replaceImageCache(pAbout.getImage());
    }

    private static void replaceImageCache(String pImage) {
        deleteOldImageCache(sPrefsHelper.getAboutImage());
        sPrefsHelper.setAboutImage(pImage);
        downloadImage(pImage);
    }

    private static void deleteOldImageCache(String pPath) {
        if (pPath.equals(EMPTY_IMAGE_PATH)) return;
        String localPath = getLocalFilePathFromRelative(pPath);
        File oldImage = new File(localPath);
        if (oldImage.exists()) oldImage.delete();
    }

    private static void downloadImage(String pUrl) {
        String fullImageUrl = convertRelativeUrlToConcrete(pUrl);
        String localPath = getLocalFilePathFromRelative(pUrl);
        DownloadHelper.downloadFile(fullImageUrl, localPath, new AjaxCallback<File>() {
            @Override
            public void callback(String url, File object, AjaxStatus pStatus) {
                notifyLoadingObserver(convertRequestCodeToStatus(pStatus));
            }
        });
    }

    private static ILoadingObserver.Status convertRequestCodeToStatus(AjaxStatus pStatus) {
        switch (pStatus.getCode()) {
            case CONST.HTTP_REQUEST_SUCCESS:
                return ILoadingObserver.Status.LOADING_SUCCESS;
            default:
                return ILoadingObserver.Status.LOADING_FAILED;
        }
    }

    private static void notifyLoadingObserver(ILoadingObserver.Status pStatus) {
        if (sLoadingObserver != null) sLoadingObserver.onLoadingStatus(pStatus);
    }

    private static String getLocalFilePathFromRelative(String pRemotePath) {
        String localPath = PathesHelper.getStoragePath(MyApp.getAppContext(), "about");
        localPath += extractNameFromPath(pRemotePath);
        return localPath;
    }

    private static String extractNameFromPath(String pPath) {
        return pPath.replaceAll(".+/", "");
    }

    private static String convertRelativeUrlToConcrete(String pRelativeUrl) {
        SizeHelper sizeHelper = SizeHelper.getInstance(MyApp.getAppContext());
        return sizeHelper.createSizeDependedImageUrl(pRelativeUrl);
    }

    public static About getAboutFromCache() {
        if (!isCacheExist()) loadingAbout();
        About cache = new About();
        cache.setName(sPrefsHelper.getAboutName());
        cache.setText(sPrefsHelper.getAboutText());
        cache.setImage(getLocalFilePathFromRelative(sPrefsHelper.getAboutImage()));
        return cache;
    }
}