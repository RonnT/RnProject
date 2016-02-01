package top.titov.gas.extension;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.helper.SizeHelper;

/**
 * Created by Roman Titov on 12.11.2015.
 */
public class IndicatorView extends HorizontalScrollView implements ViewPager.OnPageChangeListener{

    public IndicatorView(Context context) {
        super(context);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int positionX = (int) ((position + positionOffset) * 20 *
                SizeHelper.getInstance(MyApp.getAppContext()).getScreenDensity());
        smoothScrollTo(positionX, 0);
    }

    @Override
    public void onPageSelected(int position) {
        int positionX = (int) (position *20* SizeHelper.getInstance(MyApp.getAppContext()).getScreenDensity());
        smoothScrollTo(positionX, 0);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void setItemsSize(int pItems){
        LinearLayout ll = (LinearLayout) findViewById(R.id.indicator_layout);
        ll.removeAllViews();
        ll.addView(getItemView(false));
        ll.addView(getItemView(false));

        for (int i = 0; i < pItems; i++){
            ll.addView(getItemView(true));
        }
        ll.addView(getItemView(false));
        ll.addView(getItemView(false));
    }

    private View getItemView(boolean hasImage){
        ImageView result = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen.indicator_size),
                (int) getResources().getDimension(R.dimen.indicator_size));
        result.setLayoutParams(layoutParams);
        int padding = (int) getResources().getDimension(R.dimen.indicator_padding);
        result.setPadding(padding,0,padding,0);
        if (hasImage) result.setImageResource(R.drawable.back_circle);
        return result;
    }
}
