package ru.pichesky.rosneft.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;

import ru.pichesky.rosneft.R;

/**
 * Created by Alexander Smirnov on 30.01.2015.
 * OutputLoadingFragment
 */
public class LoadingDialogFragment extends DialogFragment {
    private String mText = "";

    public static LoadingDialogFragment newInstance(String pText) {
        LoadingDialogFragment f = new LoadingDialogFragment();
        f.mText = pText;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Translucent);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.dialog_loading, container, false);

        if (mText.length() > 0) {
            ((RobotoTextView) v.findViewById(R.id.loading_dialog_text)).setText(mText);
        }

        return v;
    }
}