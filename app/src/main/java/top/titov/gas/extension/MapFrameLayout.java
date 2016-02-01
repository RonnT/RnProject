package top.titov.gas.extension;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import top.titov.gas.interfaces.IMapTouchObserver;

/**
 * Created by Andrew Vasilev on 28.07.2015.
 */
public class MapFrameLayout extends FrameLayout {

    IMapTouchObserver mMapTouchObserver = null;

    public MapFrameLayout(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mMapTouchObserver != null) {
            mMapTouchObserver.onMapTouch();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mMapTouchObserver != null) mMapTouchObserver.onMapTouchEventUp();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void setMapTouchObserver(IMapTouchObserver pTouchObserver) {
        mMapTouchObserver = pTouchObserver;
    }
}
