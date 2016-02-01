package ru.pichesky.rosneft.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.androidquery.AQuery;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.fragment.dialog.LoadingDialogFragment;
import ru.pichesky.rosneft.utils.DataManager;

/**
 * Created by Alexander Smirnov on 13.11.2014.
 * Base fragment for other fragments
 */
public abstract class BaseFragment extends Fragment {

    protected static LoadingDialogFragment mLoadingDialog;

    protected View mView;
    protected AQuery mAq;

    protected FragmentManager mFm;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) onVisible();
        else onInvisible();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutId(), container, false);
        mFm = getActivity().getSupportFragmentManager();
        mAq = new AQuery(mView);
        setData();

        return mView;
    }

    protected abstract int getLayoutId();
    protected abstract void setData();
    protected void onVisible() {}
    protected void onInvisible() {}
    public boolean allowBackPressed() {
        return true;
    }

    protected void showLoadingDialog(boolean pIsCancelable){
        showLoadingDialog(MyApp.getStringFromRes(R.string.loading), pIsCancelable);
    }

    protected void showLoadingDialog(int pTextId, boolean pIsCancelable){
        showLoadingDialog(MyApp.getStringFromRes(pTextId), pIsCancelable);
    }

    protected void showLoadingDialog(String pText, boolean pIsCancelable){
        if (!isResumed() || getActivity() == null) return;

        mLoadingDialog = LoadingDialogFragment.newInstance(pText);
        mLoadingDialog.setCancelable(pIsCancelable);
        mLoadingDialog.show(mFm, null);
    }

    public static void hideLoadingDialog(){
        if (null != mLoadingDialog) mLoadingDialog.dismiss();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (DataManager.sDisableFragmentAnimations) {
            Animation a = new Animation() {};
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    public String getStringFromRes(int pId) {
        return MyApp.getStringFromRes(pId);
    }
}
