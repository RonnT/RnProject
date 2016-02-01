package top.titov.gas.fragment.events;

import android.os.Bundle;

import top.titov.gas.R;
import top.titov.gas.fragment.BaseFragment;
import top.titov.gas.model.event.BaseEvent;
import top.titov.gas.utils.CONST;

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
