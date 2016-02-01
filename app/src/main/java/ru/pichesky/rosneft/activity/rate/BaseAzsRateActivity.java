package ru.pichesky.rosneft.activity.rate;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.devspark.robototextview.widget.RobotoTextView;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.activity.BaseActivity;
import ru.pichesky.rosneft.interfaces.IRatingObserver;
import ru.pichesky.rosneft.utils.CONST;

/**
 * Created by Andrew Vasilev on 30.07.2015.
 */
public abstract class BaseAzsRateActivity extends BaseActivity implements IRatingObserver, View.OnClickListener {

    protected static final short REQUEST_CODE_RATIING_ADDITIONAL = 33;

    protected static final short
            TYPE_OVERALL        = 0,
            TYPE_SERVICE        = 1,
            TYPE_WAITING_TIME   = 2,
            TYPE_FUEL_QUALITY   = 3,
            TYPE_FUEL_PRICE     = 4,
            TYPE_SHOP_PRICE     = 5;

    protected float
            mRatingOverall      = 0,
            mRatingService      = 0,
            mRatingWaitingTime  = 0,
            mRatingFuelQuality  = 0,
            mRatingFuelPrice    = 0,
            mRatingShopPrice    = 0;

    protected ViewGroup mRatingsLL;
    protected boolean mNeedAdditionalRatings  = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_azs_rate;
    }

    @Override
    protected void setData(Bundle savedInstanceState) {
        mRatingsLL = (ViewGroup) mAq.id(R.id.activity_azs_rate_ratings_LL).getView();
        mAq.id(R.id.activity_azs_rate_btn_send).clicked(this);
    }

    protected RatingBar addRatingBar(int pNameId, final short pType) {
        View ratingView = getLayoutInflater().inflate(R.layout.view_rate_item, mRatingsLL, false);

        String name = MyApp.getStringFromRes(pNameId);
        ((RobotoTextView) ratingView.findViewById(R.id.view_rate_item_name)).setText(name);

        RatingBar ratingBar = (RatingBar) ratingView.findViewById(R.id.view_rate_item_rating);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                BaseAzsRateActivity.this.onRatingChanged(pType, rating);
            }
        });
        ratingBar.setRating(mRatingOverall);
        mRatingsLL.addView(ratingView);

        return ratingBar;
    }

    @Override
    public void onRatingChanged(int pRatingType, float pRating) {
        if (pRating == 0) return;

        switch (pRatingType) {
            case TYPE_OVERALL:
                mRatingOverall = pRating;
                if (mRatingOverall <= CONST.RATING_SMALL && mNeedAdditionalRatings) {
                    showAdditionalRatings();
                }
                break;

            case TYPE_SERVICE:
                mRatingService = pRating;
                break;

            case TYPE_WAITING_TIME:
                mRatingWaitingTime = pRating;
                break;

            case TYPE_FUEL_QUALITY:
                mRatingFuelQuality = pRating;
                break;

            case TYPE_FUEL_PRICE:
                mRatingFuelPrice = pRating;
                break;

            case TYPE_SHOP_PRICE:
                mRatingShopPrice = pRating;
                break;
        }
    }

    protected void showAdditionalRatings() {}

    @Override
    public void onClick(View v) {
        callOnClick(v);
    }

    protected abstract void callOnClick(View pView);
}
