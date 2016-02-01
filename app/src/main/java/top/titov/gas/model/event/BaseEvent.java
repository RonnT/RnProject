package top.titov.gas.model.event;

import android.view.View;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.helper.SizeHelper;
import top.titov.gas.utils.api.Api_Url;

/**
 * Created by Roman Titov on 11.11.2015.
 */
public abstract class BaseEvent implements Serializable {

    protected int id;
    protected String name;
    protected String text;
    protected String image;

    @SerializedName("is_federal")
    protected short isFederal;

    @SerializedName("region_code")
    protected String regionCode;

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public short getIsFederal() {
        return isFederal;
    }

    public String getImage() {
        float maxWidth = SizeHelper.getInstance(MyApp.getAppContext()).getScreenWidth();

        float maxHeight = SizeHelper.getInstance(MyApp.getAppContext()).getScreenHeight();

        String result = Api_Url.getImageUrl() + (int) maxWidth
                + MyApp.getStringFromRes(R.string.image_lock_ratio_suffix) + (int) maxHeight + image;
        return result;
    }

    public String getImagePreview(View pView) {
        float maxWidth = SizeHelper.getInstance(MyApp.getAppContext()).getScreenWidth()
                - MyApp.getDimenFromRes(R.dimen.standart_margin)*2;
        float maxHeight = maxWidth;
        String result = Api_Url.getImageUrl() + (int) maxWidth
                + MyApp.getStringFromRes(R.string.image_lock_ratio_suffix) + (int) maxHeight + image;
        return result;
    }
}
