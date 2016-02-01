package top.titov.gas.fragment.dialog;


import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.utils.CONST;

/**
 * Created by Andrew Vasilev on 23.01.2015.
 */
public class QuestionDialogFragment extends BaseDialogFragment {
    private static final String
            TEXT_BODY = "dialog_text_body";

    public static QuestionDialogFragment newInstance(int pTitleRes, int pTextRes, int pTextPositive,
                                        int pTextNegative, Runnable pRunnablePositive,
                                        Runnable pRunnablNegative) {
        QuestionDialogFragment f = new QuestionDialogFragment();

        initInstance(pTitleRes, pTextPositive, pTextNegative);

        if (pTextRes > CONST.NOT_DEFINED) {
            args.putString(TEXT_BODY, MyApp.getStringFromRes(pTextRes));
        }
        f.setArguments(args);

        f.setRunnablePositive(pRunnablePositive);
        f.setRunnableNegative(pRunnablNegative);

        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_question;
    }

    @Override
    protected void initViews() {
        mAq.id(R.id.fragment_dialog_text).text(args.getString(TEXT_BODY));
    }
}
