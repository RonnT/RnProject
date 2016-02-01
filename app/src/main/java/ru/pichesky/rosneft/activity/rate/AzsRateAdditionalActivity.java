package ru.pichesky.rosneft.activity.rate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.helper.ToastHelper;
import ru.pichesky.rosneft.utils.CONST;

/**
 * Created by Andrew Vasilev on 30.07.2015.
 */
public class AzsRateAdditionalActivity extends BaseAzsRateActivity {

    @Override
    protected void setData(Bundle savedInstanceState) {
        super.setData(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b == null) {
            ToastHelper.showToast(R.string.error_unknown);
            finish();
            return;
        }

        setTitle(R.string.rate_add_title);

        mAq.id(R.id.activity_azs_rate_et_LL).gone();
        mAq.id(R.id.activity_azs_rate_btn_send).text(R.string.caps_save);
        mAq.id(R.id.activity_azs_rate_address).text(b.getString(CONST.TAG_AZS_ADDRESS, ""));
        mRatingOverall = b.getFloat(CONST.TAG_AZS_RATING_OVERALL, 0);

        initViews();
    }

    private void initViews() {
        addRatingBar(R.string.rating_name_service, TYPE_SERVICE);
        addRatingBar(R.string.rating_name_waiting_time, TYPE_WAITING_TIME);
        addRatingBar(R.string.rating_name_fuel_quality, TYPE_FUEL_QUALITY);
        addRatingBar(R.string.rating_name_fuel_price,  TYPE_FUEL_PRICE);
        addRatingBar(R.string.rating_name_shop_price, TYPE_SHOP_PRICE);
    }

    @Override
    protected void callOnClick(View pView) {
        switch (pView.getId()) {
            case R.id.activity_azs_rate_btn_send:
                setResultAndFinish();
                break;
        }
    }

    private void setResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(CONST.RATE_SERVICE, mRatingService);
        resultIntent.putExtra(CONST.RATE_WAITING_TIME, mRatingWaitingTime);
        resultIntent.putExtra(CONST.RATE_FUEL_QUALITY, mRatingFuelQuality);
        resultIntent.putExtra(CONST.RATE_FUEL_PRICE, mRatingFuelPrice);
        resultIntent.putExtra(CONST.RATE_SHOP_PRICE, mRatingShopPrice);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
