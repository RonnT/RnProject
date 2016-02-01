package ru.pichesky.rosneft.fragment.events;

import android.os.Bundle;

import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.fragment.BaseFragment;
import ru.pichesky.rosneft.model.event.BaseEvent;
import ru.pichesky.rosneft.utils.CONST;

/**
 * Created by Roman Titov on 11.11.2015.
 */
public class EventPageFragment<T> extends BaseFragment{

    private BaseEvent mEventItem;

    @Override
    protected int getLayoutId() {
        return R.layout.view_pager_event;
    }

    @Override
    protected void setData() {
        Bundle args = getArguments();
        mEventItem = (BaseEvent) args.getSerializable(CONST.TAG_EVENT);

        fillFields();
    }

    private void fillFields(){
        mAq.id(R.id.event_text).text(mEventItem.getName());
        mAq.id(R.id.event_image_preview).image(mEventItem.getImagePreview(mAq.id(R.id.event_image_preview).getView()));
    }
}
