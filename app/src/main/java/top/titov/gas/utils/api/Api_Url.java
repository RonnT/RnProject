package top.titov.gas.utils.api;

import top.titov.gas.MyApp;
import top.titov.gas.R;

/**
 * Created by Alexander Smirnov on 12.12.2014.
 * Class using for auto getting URL for current build version
 */
public class Api_Url {
    private static final String SERVER_URL_RELEASE = "http://rosneft-azs.ru"; //TODO: узнать и заменить
    private static final String SERVER_URL_DEBUG = "http://pre.rn-brand.sitesoft.ru";

    private static final String RELEASE_API_URL = SERVER_URL_RELEASE
            + MyApp.getStringFromRes(R.string.prefix_api);
    private static final String TEST_API_URL = SERVER_URL_DEBUG
            + MyApp.getStringFromRes(R.string.prefix_api);

    private static final int IS_RELEASE = 0;
    private static final boolean IS_ALWAYS_RELEASE = false;
    private static final boolean IS_ALWAYS_DEBUG = true;

    public static String getApiUrl() {
        if (IS_ALWAYS_RELEASE) return RELEASE_API_URL;
        if (IS_ALWAYS_DEBUG) return TEST_API_URL;

        return IS_RELEASE == 1
                ? RELEASE_API_URL : TEST_API_URL;
    }

    private static String getServerUrl() {
        if (IS_ALWAYS_RELEASE) return SERVER_URL_RELEASE;
        if (IS_ALWAYS_DEBUG) return SERVER_URL_DEBUG;

        return IS_RELEASE == 1
                ? SERVER_URL_RELEASE : SERVER_URL_DEBUG;
    }

    public static String getImageUrl(){
        return getServerUrl() + MyApp.getStringFromRes(R.string.prefix_image);
    }

}
