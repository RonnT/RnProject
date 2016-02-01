package top.titov.gas.helper;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;

import java.util.Calendar;
import java.util.Map;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.utils.CONST;

/**
 * Created by Andrew Vasilev on 24.07.2015.
 */
public class AzsHelper {

    public static void setAzsServices(Map<String, Integer> pServices, ViewGroup rootView) {
        if (pServices.get(CONST.FILTER_WASH) == CONST.SERVICE_EXIST) showServiceIcon(rootView, R.drawable.ic_service_wash);
        if (pServices.get(CONST.FILTER_TIRE) == CONST.SERVICE_EXIST) showServiceIcon(rootView, R.drawable.ic_service_service);
        if (pServices.get(CONST.FILTER_CAFE) == CONST.SERVICE_EXIST) showServiceIcon(rootView, R.drawable.ic_service_cafe);
        if (pServices.get(CONST.FILTER_SHOP) == CONST.SERVICE_EXIST) showServiceIcon(rootView, R.drawable.ic_service_shop);
        if (pServices.get(CONST.FILTER_VISA) == CONST.SERVICE_EXIST) showServiceIcon(rootView, R.drawable.ic_service_visa);
    }

    private static void showServiceIcon(ViewGroup rootView, int pDrawableId) {
        ImageView iv = (ImageView) LayoutInflater.from(rootView.getContext())
                .inflate(R.layout.view_azs_list_service_item, rootView, false);

        iv.setImageResource(pDrawableId);
        rootView.addView(iv);
    }

    public static void setDistance(AQuery pAq, float pDistance) {
        String pDistanceString = LocationHelper.getAccuracyString(pDistance);

        pAq.id(R.id.view_azs_list_item_distance).text(pDistanceString);
    }

    public static void setPrice(AQuery pAq, Azs pAzs) {
        float price = pAzs.getSelectedFuelPrice();

        setPriceToView(pAq, price);
    }

    public static void setPriceToView(AQuery pAq, float pPrice) {
        if (pPrice == 0) {
            pAq.id(R.id.view_price_tv_rur).text("");
            pAq.id(R.id.view_price_tv_kop).text("");
            return;
        }
        String[] pricesplitted = String.valueOf(pPrice).split("\\.");

        String kop = pricesplitted.length > 1
                ? pricesplitted[1] : MyApp.getStringFromRes(R.string.zeros);
        if (kop.length() == 1) kop += MyApp.getStringFromRes(R.string.zero);

        pAq.id(R.id.view_price_tv_rur).text(pricesplitted[0]);
        pAq.id(R.id.view_price_tv_kop).text(kop);
    }

    private static String getDayName() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:       return MyApp.getStringFromRes(R.string.day_1);
            case Calendar.TUESDAY:      return MyApp.getStringFromRes(R.string.day_2);
            case Calendar.WEDNESDAY:    return MyApp.getStringFromRes(R.string.day_3);
            case Calendar.THURSDAY:     return MyApp.getStringFromRes(R.string.day_4);
            case Calendar.FRIDAY:       return MyApp.getStringFromRes(R.string.day_5);
            case Calendar.SATURDAY:     return MyApp.getStringFromRes(R.string.day_6);
            default:                    return MyApp.getStringFromRes(R.string.day_7);
        }
    }
}
