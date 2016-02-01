package top.titov.gas.fragment.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.androidquery.AQuery;

import top.titov.gas.MyApp;
import top.titov.gas.R;


/**
 * Created by Andrew Vasilev on 23.01.2015.
 */
public abstract class BaseDialogFragment extends DialogFragment
        implements View.OnClickListener {

    protected static final String
            TITLE   = "dialog_title",
            BTN_POSITIVE_TEXT = "dialog_btn_positive_text",
            BTN_NEGATIVE_TEXT = "dialog_btn_negative_text";

    protected Runnable
            mRunnablePositive = null,
            mRunnableNegative = null;

    protected AQuery mAq;
    protected static Bundle args;

    protected static void initInstance(int pTitleRes, int pTextPositive, int pTextNegative) {
        args = new Bundle();

        args.putString(TITLE, MyApp.getStringFromRes(pTitleRes));

        if (pTextPositive > -1) {
            args.putString(BTN_POSITIVE_TEXT, MyApp.getStringFromRes(pTextPositive));
        }

        if (pTextNegative > -1) {
            args.putString(BTN_NEGATIVE_TEXT, MyApp.getStringFromRes(pTextNegative));
        }
    }

    protected static void initInstance(int pTitleRes) {
        args = new Bundle();
        args.putString(TITLE, MyApp.getStringFromRes(pTitleRes));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        args = getArguments();
        if (null == args) return null;

        mAq = new AQuery(view);
        mAq.id(R.id.fragment_dialog_title).text(args.getString(TITLE));

        if (null != args.getString(BTN_POSITIVE_TEXT)) {
            mAq.id(R.id.fragment_dialog_positive_btn)
                    .text(args.getString(BTN_POSITIVE_TEXT))
                    .clicked(this);
        }

        if (null != args.getString(BTN_POSITIVE_TEXT)) {
            mAq.id(R.id.fragment_dialog_negative_btn)
                    .text(args.getString(BTN_NEGATIVE_TEXT))
                    .clicked(this);
        }

        initViews();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_dialog_positive_btn:
                if (null != mRunnablePositive) mRunnablePositive.run();
                break;
            case R.id.fragment_dialog_negative_btn:
                if (null != mRunnableNegative) mRunnableNegative.run();
                break;
        }
        getDialog().dismiss();
    }

    protected void setRunnablePositive(Runnable pRunnable) {
        mRunnablePositive = pRunnable;
    }

    protected void setRunnableNegative(Runnable pRunnable) {
        mRunnableNegative = pRunnable;
    }

    protected abstract int getLayoutId();
    protected abstract void initViews();
}
