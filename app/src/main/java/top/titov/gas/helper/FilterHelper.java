package top.titov.gas.helper;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.androidquery.AQuery;

import java.util.List;
import java.util.Map;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.interfaces.IFuelObserver;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.utils.CONST;

/**
 * Created by Andrew Vasilev on 20.07.2015.
 */
public class FilterHelper {

    public static String getFuelTypeForApi() {
        return getFuelTypeForApi(PrefsHelper.getInstance().getFilterFuelType());
    }

    public static String getFuelTypeForApi(String pFuelForHuman) {
        return pFuelForHuman;
    }

    public static void setFilterBtnImage(AQuery pAq, int pBtnId, boolean isActive) {
        pAq.id(pBtnId).image(getFilterBtnImageId(pBtnId, isActive));
    }

    public static void setFilterBtnImage(AQuery pAq, int pBtnId) {
        pAq.id(pBtnId).image(getFilterBtnImageId(pBtnId, isFilterActive(pBtnId)));
    }

    public static void setFuelSpinner(final Activity pActivity, AQuery pAq, int pSpinnerId,
                                      final IFuelObserver itemSelected) {
        pAq.id(pSpinnerId).getSpinner();

        String[] fuelTypes = MyApp.getAppContext().getResources()
                .getStringArray(R.array.fuel_types);

        final ArrayAdapter<String> fuelAdapter = new ArrayAdapter<>(pActivity,
                android.R.layout.simple_list_item_1, fuelTypes);

        pAq.id(pSpinnerId).adapter(fuelAdapter).itemSelected(
                new AdapterView.OnItemSelectedListener() {
                    private boolean isFirstSetup = true;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {

                        if (itemSelected != null) {
                            itemSelected.updateFuelType(fuelAdapter.getItem(position));

                        } else if (!isFirstSetup){
                            PrefsHelper.getInstance()
                                    .setFilterFuelType(fuelAdapter.getItem(position));
                            pActivity.sendBroadcast(new Intent(CONST.INTENT_UPDATE_AZS));

                        } else isFirstSetup = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}

                }).getSpinner()
                .setSelection(fuelAdapter.getPosition(
                        PrefsHelper.getInstance().getFilterFuelType()));
    }

    public static void setFilterBtn(AQuery pAq, int pBtnId, boolean isActive,
                                    View.OnClickListener pClickListener) {
        pAq.id(pBtnId).image(getFilterBtnImageId(pBtnId, isActive)).clicked(pClickListener);
    }

    public static void setFilterBtn(AQuery pAq, int pBtnId, View.OnClickListener pClickListener) {
        pAq.id(pBtnId).image(getFilterBtnImageId(pBtnId, isFilterActive(pBtnId)))
                .clicked(pClickListener);
    }

    public static boolean isFilterActive(int pBtnId) {
        PrefsHelper prefsHelper = PrefsHelper.getInstance();

        switch (pBtnId) {
            case R.id.view_filters_btn_wash:    return prefsHelper.getFilterWash();
            case R.id.view_filters_btn_service: return prefsHelper.getFilterService();
            case R.id.view_filters_btn_cafe:    return prefsHelper.getFilterCafe();
            default:                            return prefsHelper.getFilterShop();
        }
    }

    public static int getFilterBtnImageId(int pBtnId, boolean isActive) {

        switch (pBtnId) {
            case R.id.view_filters_btn_wash:
                return isActive ? R.drawable.ic_filter_wash_active : R.drawable.ic_filter_wash;

            case R.id.view_filters_btn_service:
                return isActive ? R.drawable.ic_filter_service_active : R.drawable.ic_filter_service;

            case R.id.view_filters_btn_cafe:
                return isActive ? R.drawable.ic_filter_cafe_active : R.drawable.ic_filter_cafe;

            default:
                return isActive ? R.drawable.ic_filter_shop_active : R.drawable.ic_filter_shop;
        }
    }

    public static boolean hasAllFilters(Azs pAzs, List<String> pFilters, String pFuelType, int pIndex) {

        if (pFilters.isEmpty() || pIndex >= pFilters.size()) return checkFuels(pAzs, pFuelType);

        return checkServices(pAzs, pFilters.get(pIndex)) && hasAllFilters(pAzs, pFilters, pFuelType, ++pIndex);
    }

    private static boolean checkServices(Azs pAzs, String pFilter) {
        Map<String, Integer> services = pAzs.getServiceAvailable();
        return services.containsKey(pFilter) &&
                services.get(pFilter) == CONST.SERVICE_EXIST;
    }

    private static boolean checkFuels(Azs pAzs, String pFilter) {
        Map<String, Integer> fuels = pAzs.getFuelAvailable();
        String fuelApiName = getFuelTypeForApi(pFilter);
        return fuels.containsKey(fuelApiName) &&
                fuels.get(fuelApiName) == CONST.FUEL_AVAILABLE;
    }
}

