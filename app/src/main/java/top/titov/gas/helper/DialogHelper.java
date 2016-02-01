package top.titov.gas.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import top.titov.gas.R;
import top.titov.gas.utils.CONST;

/**
 * Created by Andrew Vasilev on 20.07.2015.
 */
public class DialogHelper {

    public static void initAlertDialog(Context pContext, int pTitleId, int pMessageId,
                                       int pPositiveTextId, int pNegativeTextId,
                                       final Runnable pPositiveRunnable,
                                       final Runnable pNegativeRunnable, boolean pCancellable) {

        initAlertDialog(pContext, null, pTitleId, pMessageId, pPositiveTextId, pNegativeTextId,
                pPositiveRunnable, pNegativeRunnable, pCancellable);
    }

    public static void initAlertDialog(Context pContext, View pContentView,  int pTitleId, int pMessageId,
                                       int pPositiveTextId, int pNegativeTextId,
                                       final Runnable pPositiveRunnable,
                                       final Runnable pNegativeRunnable, boolean pCancellable) {

        AlertDialog.Builder builder = getAlertDialog(pContext, pContentView, pTitleId, pMessageId,
                pPositiveTextId, pNegativeTextId, pPositiveRunnable, pNegativeRunnable,
                pCancellable);

        builder.show();
    }

    public static AlertDialog.Builder getAlertDialog(Context pContext, View pContentView,
                                                     int pTitleId, int pMessageId,
                                                     int pPositiveTextId, int pNegativeTextId,
                                                     final Runnable pPositiveRunnable,
                                                     final Runnable pNegativeRunnable,
                                                     boolean pCancellable) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(pContext, R.style.AppCompatAlertDialogStyle);

        if (pContentView != null) builder.setView(pContentView);

        builder.setTitle(pTitleId);
        if (pMessageId > CONST.NOT_DEFINED) builder.setMessage(pMessageId);

        builder.setCancelable(pCancellable);

        builder.setPositiveButton(pPositiveTextId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (pPositiveRunnable != null) pPositiveRunnable.run();
            }
        });
        builder.setNegativeButton(pNegativeTextId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (pNegativeRunnable != null) pNegativeRunnable.run();
            }
        });

        return builder;
    }
}
