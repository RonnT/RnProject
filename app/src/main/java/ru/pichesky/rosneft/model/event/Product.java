package ru.pichesky.rosneft.model.event;

import android.view.View;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ru.pichesky.rosneft.MyApp;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.helper.SizeHelper;
import ru.pichesky.rosneft.utils.api.Api_Url;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
public class Product extends BaseEvent implements Serializable {

    private String announcement;
    private String icon;
}
