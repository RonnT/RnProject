package top.titov.gas.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;

import top.titov.gas.R;
import top.titov.gas.adapter.LeftMenuAdapter;
import top.titov.gas.fragment.about.AboutFragment;
import top.titov.gas.fragment.azs.AzsTabsFragment;
import top.titov.gas.fragment.favorite.FavoriteListFragment;
import top.titov.gas.fragment.events.EventsTabsFragment;
import top.titov.gas.helper.SizeHelper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.DataManager;
import top.titov.gas.utils.Utility;


public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final int
            POSITION_AZS        = 0,
            POSITION_FAVORITE   = 1,
            POSITION_EVENTS     = 2,
            POSITION_ABOUT      = 3;

    private static final String
            ARROW_STATE         = "ARROW_STATE",
            BURGER_STATE        = "BURGER_STATE";

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";

    private DrawerLayout mDrawerLayout;
    private final Handler mDrawerActionHandler = new Handler();
    private ActionBarDrawerToggle mDrawerToggle;

    private FragmentManager mFm;

    private int mSelectedPosition = POSITION_AZS;

    private LeftMenuAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setData(Bundle savedInstanceState) {
        mFm = getSupportFragmentManager();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (null != savedInstanceState) mSelectedPosition = savedInstanceState.getInt(NAV_ITEM_ID);

        setDrawerMenu();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.syncState();

        mFm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mFm.getBackStackEntryCount() == 0) changeDrawerIcon(BURGER_STATE);
                if (mFm.getBackStackEntryCount() > 0) changeDrawerIcon(ARROW_STATE);
            }
        });

        Intent fromPush = getIntent();
        int tag = fromPush.getIntExtra(CONST.SELECTED_ITEM, 0);
        if(tag == CONST.PUSH_TYPE_EVENT || tag == CONST.PUSH_TYPE_PRODUCT) {
            mSelectedPosition = POSITION_EVENTS;
            mAdapter.setSelectedPosition(mSelectedPosition);
        }
        if(tag == CONST.PUSH_TYPE_FIRST)
            showDialog(getResources().getString(R.string.push_title),fromPush.getStringExtra(CONST.PUSH_MESSAGE));
        navigate(mSelectedPosition, String.valueOf(tag));
    }

    private void setDrawerMenu() {
        float maxDrawerWidth = getResources().getDimension(R.dimen.drawer_right_edge_margin) * 5;
        float width = SizeHelper.getInstance(this).getScreenWidth()
                - getResources().getDimension(R.dimen.drawer_right_edge_margin);

        if (width > maxDrawerWidth) width = maxDrawerWidth;

        mAq.id(R.id.left_drawer_layout).getView().getLayoutParams().width = (int) width;
        ListView lv = mAq.id(R.id.left_drawer).itemClicked(this).getListView();
        mAdapter = new LeftMenuAdapter(this);
        lv.setAdapter(mAdapter);
    }

    private void navigate(final int itemId, final String pTag) {
        setToolbarElevation(getResources().getDimension(R.dimen.toolbar_elevation));

        switch (itemId){
            case POSITION_AZS:
                setTitle(R.string.left_menu_0);
                setToolbarElevation(0);
                showFragment(new AzsTabsFragment(), pTag);
                break;

            case POSITION_FAVORITE:
                setTitle(R.string.left_menu_1);
                showFragment(new FavoriteListFragment(), pTag);
                break;

            case POSITION_EVENTS:
                setTitle(R.string.left_menu_2);
                setToolbarElevation(0);
                showFragment(new EventsTabsFragment(), pTag);
                break;

            case POSITION_ABOUT:
                setTitle(R.string.left_menu_3);
                showFragment(new AboutFragment(), pTag);
                break;
        }
    }

    private void showFragment(Fragment pFragment, String pTag){
        Utility.showFragment(mFm, pFragment, R.id.activity_content, pTag, R.anim.fragment_fade_in,
                R.anim.fragment_fade_out);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(NAV_ITEM_ID, mSelectedPosition);
    }

    public void setTitle(int pTitle) {
        setTitle(getResources().getString(pTitle));
    }

    public void clearBackStack() {
        DataManager.sDisableFragmentAnimations = true;
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        DataManager.sDisableFragmentAnimations = false;
    }

    private void showFragment(Fragment pFragment) {
        Utility.showFragment(getSupportFragmentManager(), pFragment, R.id.activity_content, null,
                R.anim.fragment_fade_in, R.anim.fragment_fade_out,
                R.anim.fragment_fade_in_back, R.anim.fragment_fade_out_back);
    }

    private void changeDrawerIcon(String pState){
        float start = pState.equals(ARROW_STATE) ? 0 : 1;
        float finish = pState.equals(ARROW_STATE) ? 1 : 0;

        ValueAnimator anim = ValueAnimator.ofFloat(start, finish);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
            }
        });
        if (pState.equals(BURGER_STATE)) mDrawerToggle.setDrawerIndicatorEnabled(true);
        else {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                }
            });
        }
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(CONST.ANIMATION_BURGER_TIME);
        anim.start();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clearBackStack();
        updateSelectedPosition(position);

        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(mSelectedPosition, null);
            }
        }, DRAWER_CLOSE_DELAY_MS);
    }

    private void updateSelectedPosition(int pPosition) {
        mSelectedPosition = pPosition;
        mAdapter.setSelectedPosition(mSelectedPosition);
        mAdapter.notifyDataSetChanged();
    }
}