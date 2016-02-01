package ru.pichesky.rosneft.fragment.about;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import ru.pichesky.rosneft.R;
import ru.pichesky.rosneft.fragment.BaseFragment;
import ru.pichesky.rosneft.interfaces.ILoadingObserver;
import ru.pichesky.rosneft.model.About;

public class AboutFragment extends BaseFragment implements View.OnClickListener {

    private WebView mAboutView;
    private ViewGroup mErrorView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void setData() {
        initAboutView();
        loadingAboutCompany();
    }

    private void initAboutView() {
        mAboutView = mAq.id(R.id.about_web_view).getWebView();
        mErrorView = (ViewGroup) mAq.id(R.id.about_error_view).gone().getView();
        mAq.id(R.id.about_try_again_button).clicked(this);
    }

    private void loadingAboutCompany() {
            showLoadingDialog(false);
            About.tryLoadingExistAbout(getLoadingObserver());
    }

    private ILoadingObserver getLoadingObserver() {
        return new ILoadingObserver() {
            @Override
            public void onLoadingStatus(Status status) {
                hideLoadingDialog();
                switch (status) {
                    case LOADING_SUCCESS:
                    case LOADING_LOCAL:
                        hideErrorView();
                        showAboutCompany();
                        break;
                    case LOADING_FAILED:
                        showErrorView();
                        break;
                }
            }
        };
    }

    private void showAboutCompany() {
        showHtml(buildHtmlContent(About.getAboutFromCache()));
    }

    @Override
    public void onClick(View v) {
        loadingAboutCompany();
    }

    private void showErrorView() {
        mErrorView.setVisibility(View.VISIBLE);
    }

    private void hideErrorView() {
        if (mErrorView.getVisibility() == View.VISIBLE) mErrorView.setVisibility(View.GONE);
    }

    private void showHtml(String pHtml) {
        mAboutView.loadDataWithBaseURL("file:///android_asset/", pHtml, "text/html", "utf-8", null);
    }

    private String buildHtmlContent(About pAbout) {

        StringBuilder builder = new StringBuilder();

        builder.append(getStringFromRes(R.string.html_start));
        builder.append(getStringFromRes(R.string.html_style));
        builder.append(getStringFromRes(R.string.html_body_start));
        builder.append(getStringFromRes(R.string.html_div_about));

        builder.append(String.format(getStringFromRes(R.string.html_image), pAbout.getImage()));
        builder.append(getStringFromRes(R.string.html_div_end));

        String paragraph = getStringFromRes(R.string.html_div_text) + pAbout.getText()
                + getStringFromRes(R.string.html_div_end);
        builder.append(paragraph);

        builder.append(getStringFromRes(R.string.html_body_end));
        builder.append(getStringFromRes(R.string.html_end));

        return builder.toString();
    }
}
