package top.titov.gas.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidquery.AQuery;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.fragment.dialog.LoadingDialogFragment;
import top.titov.gas.helper.SynchronizeHelper;
import top.titov.gas.utils.CONST;

/**
 * Created by Alexander Smirnov on 13.01.2015.
 * Base activity for other activities
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected CharSequence mTitle;
    protected AQuery mAq;
    protected FragmentManager mFm;

    private BroadcastReceiver mBr;
    protected LoadingDialogFragment mloadingDialog = null;

    public static boolean isAppWentToBg = false;
    public static boolean isWindowFocused = false;
    public static boolean isBackPressed = false;

    @Override
    protected void onStart() {
        applicationWillEnterForeground();
        super.onStart();
    }

    protected void applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            //SynchronizeHelper.synchronize();
            actionOnForeground();
            //Toast.makeText(getApplicationContext(), "App is in foreground", Toast.LENGTH_SHORT).show();
        }
    }

    protected void actionOnForeground(){}

    @Override
    protected void onStop() {
        super.onStop();
        applicationDidEnterBackground();
    }

    protected void applicationDidEnterBackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            //Toast.makeText(getApplicationContext(), "App is Going to Background", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (this instanceof MainActivity) {
        } else {
            isBackPressed = true;
        }
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isWindowFocused = hasFocus;
        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mAq = new AQuery(this);
        mFm = getSupportFragmentManager();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            setSupportActionBar(mToolbar);
        }

        mTitle = getTitle();
        setData(savedInstanceState);

        initBr();
    }

    private void initBr(){
        mBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case CONST.INTENT_SHOW_SNACK_NOTIF:
                        showPushNotifSnackbar(intent);
                        break;
                }
            }
        };
    }

    private void registerBr() {
        IntentFilter filter = new IntentFilter(CONST.INTENT_SHOW_SNACK_NOTIF);
        registerReceiver(mBr, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBr();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBr != null) this.unregisterReceiver(mBr);
    }

    private void showPushNotifSnackbar(Intent pIntent) {
        String message = pIntent.getExtras().getString(CONST.PUSH_MESSAGE);
        int type = pIntent.getExtras().getInt(CONST.PUSH_TYPE);
        if (message.length() == 0) return;

        final int itemId = pIntent.getExtras().getInt(CONST.PUSH_ITEM_ID);
        Snackbar snack = getSnack(message, type, itemId);
        snack.show();
    }

    private Snackbar getSnack(String pText, int type, int itemId){
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        Snackbar snack = Snackbar.make(rootView, pText, Snackbar.LENGTH_LONG);
        snack.setAction(R.string.snack_detail, getSnackClickListener(itemId, type, pText));
        snack.setActionTextColor(getResources().getColor(R.color.color_accent));
        View snackBarView = snack.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.color_primary));
        snackBarView.setMinimumHeight((int) getResources().getDimension(R.dimen.snack_min_height));
        TextView tv = (TextView) snackBarView.findViewById(
                android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.BLACK);
        return snack;
    }

    private View.OnClickListener getSnackClickListener(final int itemId, final int type, final String message){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (type){
                    case CONST.PUSH_TYPE_FIRST:
                        showDialog(getString(R.string.left_menu_0) , message);
                        break;

                    case CONST.PUSH_TYPE_EVENT:
                        intent = new Intent(getApplicationContext(), EventItemActivity.class);
                        intent.putExtra(CONST.PUSH_ITEM_ID, itemId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;

                    case CONST.PUSH_TYPE_PRODUCT:
                        intent = new Intent(getApplicationContext(), ProductItemActivity.class);
                        intent.putExtra(CONST.PUSH_ITEM_ID, itemId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                }
            }
        };
    }

    public void setBackIconVisible() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setActionBarIcon(int iconRes) {
        if (mToolbar != null) mToolbar.setNavigationIcon(iconRes);
    }

    protected void hideToolbar() {
        if (mToolbar != null) mToolbar.setVisibility(View.GONE);
    }

    protected void showToolbar(int pTitleResId) {
        if (mToolbar != null) {
            mToolbar.setBackgroundColor(MyApp.getColorFromRes(R.color.color_primary));
            mToolbar.setVisibility(View.VISIBLE);
            setTitle(getString(pTitleResId));
        }
    }

    protected void setToolbarElevation(float pElevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(pElevation);
        }
    }

    protected void showloadingDialog(String pText) {
        if (pText == null) mloadingDialog = new LoadingDialogFragment();
        else mloadingDialog = LoadingDialogFragment.newInstance(pText);
        mloadingDialog.show(getSupportFragmentManager(), null);
    }

    protected void hideloadingDialog() {
        if (mloadingDialog != null) {
            mloadingDialog.dismiss();
            mloadingDialog = null;
        }
    }

    protected OnClickListener getDialogListener(){
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialogInterface.cancel();
                        break;
                }
            }
        };
    }

    protected void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, getDialogListener());
        builder.show();
    }

    protected abstract void setData(Bundle savedInstanceState);
    protected abstract int getLayoutId();
}
