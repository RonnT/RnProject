package top.titov.gas.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import top.titov.gas.MyApp;
import top.titov.gas.R;

/**
 * Created by Roman Titov on 30.06.2015.
 */
public class DrawableHelper {

    public static Drawable getCircleDrawable(int pColor){
        ShapeDrawable result = new ShapeDrawable(new OvalShape());
        result.getPaint().setColor(pColor);
        result.getPaint().setStyle(Paint.Style.FILL);
        return result;
    }

    public static Drawable createDrawerNotifIcon(Drawable backgroundImage, String text,
                                      int pSize, int pTextSize) {

        Bitmap canvasBitmap = Bitmap.createBitmap(pSize, pSize, Bitmap.Config.ARGB_8888);

        Canvas imageCanvas = new Canvas(canvasBitmap);

        Paint textPaint = new Paint();
        textPaint.setColor(MyApp.getColorFromRes(R.color.white));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(pTextSize);

        backgroundImage.draw(imageCanvas);

        int xPos = pSize / 2;
        float yPos = pSize / 2 - ((textPaint.descent() + textPaint.ascent()) / 2);

        imageCanvas.drawText(text, xPos, (int) yPos, textPaint);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApp.getAppContext().getResources(),
                canvasBitmap);

        LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[]{backgroundImage, bitmapDrawable});
        return layerDrawable;
    }
}
