package ru.pichesky.rosneft.activity.rate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.helper.NetworkHelper;
import ru.pichesky.rosneft.helper.ToastHelper;
import ru.pichesky.rosneft.interfaces.IRatingObserver;
import ru.pichesky.rosneft.model.azs.Azs;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.DataParser;
import ru.pichesky.rosneft.utils.Utility;
import ru.pichesky.rosneft.utils.api.Api;

/**
 * Created by Andrew Vasilev on 30.07.2015.
 */
public class AzsRateActivity extends BaseAzsRateActivity implements IRatingObserver,
        Response.Listener<JSONObject>, Response.ErrorListener {

    private static final int ADDITIONAL_RATINGS_COUNT = 5;

    private Azs mAzsItem = null;
    private AppCompatEditText mEtPhone, mEtComment;
    private RatingBar mRatingBarOverall;


    public static void launch(Activity pActivityFrom, View pViewFrom, Azs pAzsItem) {
        Intent intent = new Intent(pActivityFrom, AzsRateActivity.class);
        intent.putExtra(CONST.TAG_AZS, pAzsItem);

        Utility.launchActivity(pActivityFrom, pViewFrom, intent);
    }

    @Override
    protected void setData(Bundle savedInstanceState) {
        super.setData(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b != null) mAzsItem = (Azs) b.getSerializable(CONST.TAG_AZS);

        if (mAzsItem == null) {
            ToastHelper.showToast(R.string.error_unknown);
            finish();
            return;
        }

        setTitle(R.string.send_rate);
        initViews();
    }

    private void initViews() {
        mAq.id(R.id.activity_azs_rate_address).text(mAzsItem.getAddress());

        initEtPhone();

        mEtComment = (AppCompatEditText) mAq.id(R.id.activity_azs_rate_comment).getView();
        mEtComment.addTextChangedListener(getCommentTextWatcher());

        mRatingBarOverall = addRatingBar(R.string.rating_name_overall, TYPE_OVERALL);
    }

    private void initEtPhone() {
        mEtPhone = (AppCompatEditText) mAq.id(R.id.activity_azs_rate_phone).getView();

        mEtPhone.addTextChangedListener(getPhoneTextWatcher());
        mEtPhone.setText(MyApp.getStringFromRes(R.string.phone_code_starts));
        mEtPhone.setSelection(mEtPhone.getText().length());
    }

    private void sendRate() {
        if (!NetworkHelper.isNetworkAvailable()) {
            ToastHelper.showToast(R.string.error_check_connection);
            return;
        }

        showloadingDialog(getString(R.string.sending));
        Api.getInstance().rate(mAzsItem.getId(),
                mRatingOverall,
                mRatingService,
                mRatingWaitingTime,
                mRatingFuelQuality,
                mRatingFuelPrice,
                mRatingShopPrice,
                mEtPhone.getText().toString(),
                mEtComment.getText().toString(),
                this, this);
    }

    private boolean hasEmptyFields() {

        boolean hasEmpty = false;

        if (mRatingOverall == 0) {
            ToastHelper.showToast(R.string.error_need_rate);
            hasEmpty = true;
        }

        if (!isCorrectPhone(mEtPhone)) {
            showEtError(mEtPhone, R.string.error_phone_format);
            hasEmpty = true;
        }

        if (isEmptyEt(mEtPhone)) {
            showEtError(mEtPhone, R.string.error_field_empty);
            hasEmpty = true;
        }

        if (isEmptyEt(mEtComment)) {
            showEtError(mEtComment, R.string.error_field_empty);
            hasEmpty = true;
        }

        return hasEmpty;
    }

    private boolean isCorrectPhone(EditText pPhone) {
        String clearPhone = pPhone.getText().toString().replaceAll("\\s+|(\\(|\\))", "");
        return clearPhone.matches("(\\+\\d|\\d)\\d{10}");
    }

    private boolean isEmptyEt(EditText pEt) {
        return pEt.getText() == null || pEt.getText().toString().isEmpty();
    }

    private void showEtError(AppCompatEditText pEt, int pStringErrorId) {
        TextInputLayout tIl = getEtLayout(pEt);
        tIl.setErrorEnabled(true);
        tIl.setError(MyApp.getStringFromRes(pStringErrorId));
    }

    private TextInputLayout getEtLayout(AppCompatEditText pEt) {
        return (TextInputLayout) mAq.id(pEt).getView().getParent();
    }

    private TextWatcher getCommentTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getEtLayout(mEtComment).setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private TextWatcher getPhoneTextWatcher() {
        return new TextWatcher() {
            private static final short
                    CLOSING_BRACKET_POSITION = 7,
                    MAX_LENGTH_RUS_NUMBER = 18,
                    MAX_LENGTH_OTHER_NUMBER = 12;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getEtLayout(mEtPhone).setErrorEnabled(false);

                if (isRussianCode(s)) {
                    addClosingBracketIfNeeded(s, start);
                    addSpaceIfNeeded(start, s.length());
                    checkMaxLength(s, MAX_LENGTH_RUS_NUMBER);
                } else checkMaxLength(s, MAX_LENGTH_OTHER_NUMBER);
            }

            private void checkMaxLength(CharSequence pSequence, int pMaxLength) {
                if (mEtPhone.getText().toString().length() > pMaxLength) {
                    mEtPhone.setText(pSequence.subSequence(0, pMaxLength));
                    mEtPhone.setSelection(pMaxLength);
                }
            }

            private boolean needClosingBracket(CharSequence pSequence, int pStart) {
                return isRussianCode(pSequence) && pSequence.length() == CLOSING_BRACKET_POSITION
                        && pStart != CLOSING_BRACKET_POSITION;
            }

            private void addSpaceIfNeeded(int pStart, int pLength) {
                if (pStart != pLength) {
                    if (pLength == 8 || pLength == 12 || pLength == 15) {
                        mEtPhone.getText().replace(pLength, pLength,
                                MyApp.getStringFromRes(R.string.phone_space));
                    }
                }
            }

            private void addClosingBracketIfNeeded(CharSequence pSequence, int pStart) {
                if (needClosingBracket(pSequence, pStart)) {
                    mEtPhone.getText().replace(CLOSING_BRACKET_POSITION, CLOSING_BRACKET_POSITION,
                            MyApp.getStringFromRes(R.string.phone_code_ends));
                }
            }

            private boolean isRussianCode(CharSequence pSequence) {
                return pSequence.toString()
                        .contains(MyApp.getStringFromRes(R.string.phone_code_starts));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    @Override
    protected void showAdditionalRatings() {
        Intent intent = new Intent(this, AzsRateAdditionalActivity.class);
        intent.putExtra(CONST.TAG_AZS_ADDRESS, mAzsItem.getAddress());
        intent.putExtra(CONST.TAG_AZS_RATING_OVERALL, mRatingOverall);

        startActivityForResult(intent, REQUEST_CODE_RATIING_ADDITIONAL);
    }

    @Override
    protected void callOnClick(View pView) {
        switch (pView.getId()) {
            case R.id.activity_azs_rate_btn_send:
                if (!hasEmptyFields()) sendRate();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RATIING_ADDITIONAL && resultCode == CONST.RESULT_CODE_OK) {

            mRatingService = data.getExtras().getFloat(CONST.RATE_SERVICE, mRatingOverall);
            mRatingWaitingTime = data.getExtras().getFloat(CONST.RATE_WAITING_TIME, mRatingOverall);
            mRatingFuelQuality = data.getExtras().getFloat(CONST.RATE_FUEL_QUALITY, mRatingOverall);
            mRatingFuelPrice = data.getExtras().getFloat(CONST.RATE_FUEL_PRICE, mRatingOverall);
            mRatingShopPrice = data.getExtras().getFloat(CONST.RATE_SHOP_PRICE, mRatingOverall);

            mNeedAdditionalRatings = false;
            mRatingBarOverall.setRating(getMiddleLineRating());
            mNeedAdditionalRatings = true;
        }
    }

    private float getMiddleLineRating() {
        return (mRatingService + mRatingWaitingTime + mRatingFuelQuality + mRatingFuelPrice
                + mRatingShopPrice) / ADDITIONAL_RATINGS_COUNT;
    }

    @Override
    public void onResponse(JSONObject response) {
        hideloadingDialog();
        try {
            if (DataParser.getErrorCode(response) == 0) {
                onSendSuccess();

            } else ToastHelper.showToast(DataParser.getErrorName(response));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onSendSuccess() {
        ToastHelper.showToast(R.string.rate_sent);
        finish();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        hideloadingDialog();
        ToastHelper.showToast(R.string.error_try_again);
    }
}
