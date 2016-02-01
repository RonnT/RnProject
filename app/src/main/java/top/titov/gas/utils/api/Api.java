package top.titov.gas.utils.api;

import android.location.Location;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.titov.gas.MyApp;
import top.titov.gas.helper.FilterHelper;
import top.titov.gas.helper.Logger;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.DataParser;
import top.titov.gas.utils.Utility;
import top.titov.gas.utils.api.request.CustomRequest;
import top.titov.gas.utils.api.ssl.SslHttpStack;

public class Api {

    private static final String
            TRUE                            = String.valueOf(1),
            FALSE                            = String.valueOf(0),

            REQUEST_TAG                     = "REQUEST_TAG",

            INDEX_ID                        = "id",

            INDEX_LAT                       = "lat",
            INDEX_LNG                       = "lng",
            INDEX_FUEL_TYPE                 = "fuel_type",
            INDEX_IDS                       = "ids",
            INDEX_NEED_DISTANCE             = "need_distance",
            INDEX_AS_SYNCHRONISE            = "as_synchronise",

            INDEX_FILTER_CAFE               = "cafe_filter",
            INDEX_FILTER_SHOP               = "shop_filter",
            INDEX_FILTER_WASH               = "wash_filter",
            INDEX_FILTER_SERVICE            = "service_filter",
            INDEX_STATION_IDS               = "station_ids[]",
            INTEX_TIMESTAMP                 = "type[stations]",
            STATIONS                        = "stations",

            RATE_STATION_ID                 = "station_id",
            RATE_OVERALL                    = "overall_rate",
            RATE_SERVICE                    = "service",
            RATE_WAITING_TIME               = "waiting_time",
            RATE_QUALITY                    = "quality",
            RATE_FUEL_PRICE                 = "fuel_price",
            RATE_SHOP_PRICE                 = "shop_price",
            RATE_PHONE_NUMBER               = "number",
            RATE_COMMENT                    = "comment",

            INDEX_TOKEN                     = "token",
            INDEX_DEVICE_ID                 = "deviceid",
            INDEX_REG_ID                    = "reg_id",

            INDEX_LIMIT                     = "limit",
            INDEX_OFFSET                    = "offset",

            INDEX_REGION                    = "region_code",
            INDEX_REGION_ID                 = "region_id",

            INDEX_TYPE                      = "type";

    private static final int GET = Request.Method.GET;

    private RequestQueue mRequestQueue;

    public void stopRequest() {
        if (null != mRequestQueue) mRequestQueue.cancelAll(REQUEST_TAG);
    }

    private static class SingletonHolder {
        public static final Api INSTANCE = new Api();
    }

    public static Api getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public RequestQueue getRequestQueue() {
        setupRequestQueueIfNeed();

        return mRequestQueue;
    }

    private void setupRequestQueueIfNeed() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(MyApp.getAppContext(), new SslHttpStack(true));
        }
    }

    private String buildUrl(String action, Map<String, String> param) {
        return Api_Url.getApiUrl() + action +  "?" + getParamStr(param);
    }

    /**
     * Получение строки параметров для GET - запроса
     */
    private String getParamStr(Map<String, String> p) {
        String paramStr = "";

        if (p != null) {
            for (String key : p.keySet()) {
                try {
                    paramStr = paramStr + "&" + key + "=" +
                            URLEncoder.encode(p.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return paramStr;
    }

    public void sendRequest(int pMethod, String pUrl, Response.Listener<JSONObject> pRL,
                            Response.ErrorListener pEL) {
        Logger.d("URL: " + pUrl);
        sendRequest(pMethod, pUrl, null, pRL, pEL);
    }

    public void sendRequest(int pMethod, String pUrl, Map<String, String> pParams,
                            Response.Listener<JSONObject> pRL,
                            Response.ErrorListener pEL) {

        if (!Utility.checkConnection(pEL)) return;

        CustomRequest request = new CustomRequest(pMethod, pUrl, pParams, pRL, pEL);

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, // 10sec
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().add(request);
    }

    private Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(INDEX_DEVICE_ID, Utility.getAndroidID());
        String token = PrefsHelper.getInstance().getToken();

        if (token.length() > 0)
            params.put(INDEX_TOKEN, token);

        /* TODO: do not delete this comment
        String deviceId = MyApp.getPrefHelper().getDeviceId();

        if (deviceId.length() > 0)
            params.put(DataParser.INDEX_DEVICE_ID, deviceId);
        */

        return params;
    }

    private String buildStationsUrl(String action,List<Azs> favorites) {

        String finalUrl = Api_Url.getApiUrl() + action + "?";

        for (Azs fav : favorites) {
            finalUrl += "&" + INDEX_STATION_IDS + "=" + fav.getId();
        }

        return finalUrl;
    }

    /**
     * Добавить к параметрам Offset и Limit.
     * Вынесен в отдельный метод, в связи с повторным использованием кода.
     *
     * @param pParams   список параметров Map<String, String>
     * @param pOffset   сдвиг (offset)
     * @param pLimit    лимит
     */
    private void addOffsetLimit(Map<String, String> pParams, int pOffset, int pLimit) {
        pOffset = pOffset != -1 ? pOffset : CONST.DEFALUT_OFFSET;
        pLimit = pLimit != -1 ? pLimit : CONST.DEFALUT_LIMIT;

        pParams.put(DataParser.INDEX_OFFSET, String.valueOf(pOffset));
        pParams.put(DataParser.INDEX_LIMIT, String.valueOf(pLimit));
    }

    public void getClosestAzs(Location pLoc, Response.Listener<JSONObject> pRL,
                              Response.ErrorListener pEL) {
        Map<String, String> params = getParams();
        PrefsHelper prefsHelper = PrefsHelper.getInstance();

        params.put(INDEX_LAT, String.valueOf(pLoc.getLatitude()));
        params.put(INDEX_LNG, String.valueOf(pLoc.getLongitude()));

        params.put(INDEX_FUEL_TYPE, FilterHelper.getFuelTypeForApi());
        params.put(INDEX_NEED_DISTANCE, CONST.NEED_DISTANCE);
        params.put(INDEX_AS_SYNCHRONISE, CONST.NEED_AS_SYNCHRONISE);

        params.put(INDEX_FILTER_CAFE, prefsHelper.getFilterCafe() ? TRUE : FALSE);
        params.put(INDEX_FILTER_SHOP, prefsHelper.getFilterShop() ? TRUE : FALSE);
        params.put(INDEX_FILTER_WASH, prefsHelper.getFilterWash() ? TRUE : FALSE);
        params.put(INDEX_FILTER_SERVICE, prefsHelper.getFilterService() ? TRUE : FALSE);

        sendRequest(GET, buildUrl("stations", params), pRL, pEL);
    }

    public void getNearestStation(Location pLoc, Response.Listener<JSONObject> pRL,
                                  Response.ErrorListener pEL) {
        Map<String, String> params = getParams();
        PrefsHelper prefsHelper = PrefsHelper.getInstance();

        params.put(INDEX_LAT, String.valueOf(pLoc.getLatitude()));
        params.put(INDEX_LNG, String.valueOf(pLoc.getLongitude()));

        params.put(INDEX_FUEL_TYPE, FilterHelper.getFuelTypeForApi());

        params.put(INDEX_FILTER_CAFE, prefsHelper.getFilterCafe() ? TRUE : FALSE);
        params.put(INDEX_FILTER_SHOP, prefsHelper.getFilterShop() ? TRUE : FALSE);
        params.put(INDEX_FILTER_WASH, prefsHelper.getFilterWash() ? TRUE : FALSE);
        params.put(INDEX_FILTER_SERVICE, prefsHelper.getFilterService() ? TRUE : FALSE);

        sendRequest(GET, buildUrl("stations/nearest_station", params), pRL, pEL);
    }

    public void getProductList(int regionId, Response.Listener<JSONObject> pRL,
                               Response.ErrorListener pEL){
        Map<String, String> params = getParams();
        params.put(INDEX_REGION, String.valueOf(regionId));
        sendRequest(GET, buildUrl("products", params), pRL, pEL);
    }

    public void getProduct(int pId, Response.Listener<JSONObject> pRL,
                           Response.ErrorListener pEL){
        Map<String, String> params = getParams();
        params.put(INDEX_ID, String.valueOf(pId));
        sendRequest(GET, buildUrl("products", params), pRL, pEL);
    }

    public void getEventsList(int regionId, Response.Listener<JSONObject> pRL,
                              Response.ErrorListener pEL){
        Map<String, String> params = getParams();
        params.put(INDEX_REGION, String.valueOf(regionId));
        sendRequest(GET, buildUrl("products/events", params), pRL, pEL);
    }

    public void getEvent(int id,Response.Listener<JSONObject> pRL,
                              Response.ErrorListener pEL){
        Map<String, String> params = getParams();
        params.put(INDEX_ID, String.valueOf(id));
        sendRequest(GET, buildUrl("products/events", params), pRL, pEL);
    }

    public void rate(int pStationId, int pOverall, Response.Listener<JSONObject> pRL,
                     Response.ErrorListener pEL){

        rate(pStationId, pOverall, CONST.NOT_DEFINED, CONST.NOT_DEFINED, CONST.NOT_DEFINED,
                CONST.NOT_DEFINED, CONST.NOT_DEFINED, null, null, pRL, pEL);
    }

    /**
     * Отзыв о АЗС
     * https://goo.gl/qpW2sW
     */
    public void rate(int pStationId, float pOverall, float pService, float pWaitingTime, float pQuality,
                     float pFuelPrice, float pShopPrice, String pPhoneNumber, String pComment,
                     Response.Listener<JSONObject> pRL, Response.ErrorListener pEL) {
        Map<String, String> params = getParams();

        params.put(RATE_STATION_ID, String.valueOf(pStationId));
        params.put(RATE_OVERALL, String.valueOf(pOverall));

        //if (pOverall <= 3) {
        params.put(RATE_SERVICE, String.valueOf(pService));
        params.put(RATE_WAITING_TIME, String.valueOf(pWaitingTime));
        params.put(RATE_QUALITY, String.valueOf(pQuality));
        params.put(RATE_FUEL_PRICE, String.valueOf(pFuelPrice));
        params.put(RATE_SHOP_PRICE, String.valueOf(pShopPrice));
        params.put(RATE_PHONE_NUMBER, pPhoneNumber);
        params.put(RATE_COMMENT, pComment);
        //}

        sendRequest(GET, buildUrl("stations/rate", params), pRL, pEL);
    }

    public void getCurrentRegionId(double pLat, double pLng, Response.Listener<JSONObject> pRL,
                                   Response.ErrorListener pEL){
        Map<String, String> params = getParams();
        params.put(INDEX_LAT, String.valueOf(pLat));
        params.put(INDEX_LNG, String.valueOf(pLng));
        sendRequest(GET, buildUrl("location", params), pRL, pEL);
    }

    public void getFavoriteAzs(List<Azs> pIds, Response.Listener<JSONObject> pRL,
                               Response.ErrorListener pEL) {

        sendRequest(GET, buildStationsUrl("stations/items", pIds), pRL, pEL);
    }

    public void getAboutCompany(Response.Listener<JSONObject> pRL,
                                Response.ErrorListener pEL) {

        sendRequest(GET, buildUrl("about", getParams()), pRL, pEL);
    }

    public void subscribePush(Response.Listener<JSONObject> pRL, Response.ErrorListener pEL){
        Map<String,String> params = getParams();
        params.put(INDEX_REG_ID, PrefsHelper.getInstance().getRegId());
        params.put(INDEX_REGION_ID, String.valueOf(PrefsHelper.getInstance().getUserRegionId()));
        sendRequest(GET, buildUrl("push/android", params), pRL, pEL);
    }

    public void setRegion(int regionId, Response.Listener<JSONObject> pRL, Response.ErrorListener pEL){
        Map<String,String> params = getParams();
        params.put(INDEX_REGION_ID, String.valueOf(regionId));
        sendRequest(GET, buildUrl("push/set_region_android", params), pRL, pEL);
    }

    public void getSynchronizeLastTime(Response.Listener<JSONObject> pRL, Response.ErrorListener pEL) {
        Map<String,String> params = getParams();
        params.put(INDEX_TYPE, STATIONS);
        sendRequest(GET, buildUrl("synchronise/last", params), pRL, pEL);
    }

    public void syncronize(long timeStamp ,Response.Listener<JSONObject> pRL, Response.ErrorListener pEL) {
        Map<String,String> params = getParams();
        params.put(INTEX_TIMESTAMP, String.valueOf(timeStamp));
        sendRequest(GET, buildUrl("synchronise", params), pRL, pEL);
    }

    public void getPrices(List<Integer> pAzsIdList, Response.Listener<JSONObject> pRL, Response.ErrorListener pEL) {
        Map<String,String> params = getParams();
        String ids = "";
        for(int id : pAzsIdList)
            ids += String.valueOf(id) + ",";
        params.put(INDEX_IDS, ids);
        sendRequest(GET, buildUrl("fuel", params), pRL, pEL);
    }

    public void getPrice(int pAzsId, Response.Listener<JSONObject> pRL, Response.ErrorListener pEL) {
        Map<String,String> params = getParams();
        params.put(INDEX_IDS, String.valueOf(pAzsId));
        sendRequest(GET, buildUrl("fuel", params), pRL, pEL);
    }

    /* TODO: do not delete this
    public void sendPhotosRequest(String pUrl, Map<String, String> pParams,
                                  List<Photo> pPhotos, Response.Listener<JSONObject> pRL,
                                  Response.ErrorListener pEL) {

        if (!Utility.checkConnection(pEL)) return;

        MultiPartRequest request = getMultiPartRequest(pUrl, pParams, pRL, pEL);
        request.addPhoto(pPhotos);

        getRequestQueue().add(request);
    }

    public MultiPartRequest getMultiPartRequest(String pUrl, Map<String, String> pParams,
                                                Response.Listener<JSONObject> pRL,
                                                Response.ErrorListener pEL) {
        MultiPartRequest request = new MultiPartRequest(buildUrl(pUrl, null),
                new JSONObject(), pRL, pEL);
        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4, // 10sec
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.addStrings(pParams);

        return request;
    }
    */
}
