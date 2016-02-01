package top.titov.gas.extension;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman Titov on 12.08.2015.
 */
public class CustomScrollView extends ScrollView {

    List<View> mInterceptScrollViews = new ArrayList<View>();

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addInterceptScrollView(View view) {
        mInterceptScrollViews.add(view);
    }

    public void removeInterceptScrollView(View view) {
        mInterceptScrollViews.remove(view);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (mInterceptScrollViews.size() > 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Rect bounds = new Rect();

            for (View view : mInterceptScrollViews) {
                view.getHitRect(bounds);
                if (bounds.contains(x, y)) {
                    return false;
                }
            }
        }
        return super.onInterceptTouchEvent(event);
    }
}