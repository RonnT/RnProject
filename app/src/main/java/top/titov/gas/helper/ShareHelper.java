package top.titov.gas.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Html;

import com.androidquery.AQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.titov.gas.R;

public class ShareHelper {

    private static final int TWEET_CHARS_LIMIT = 117;

    public static void share(Context pContext, String pShareTitle, String pShareText, String pShareLink, String pImageLink) {
        pShareText = pShareText.replace("<li>", "<br>- ");
        pShareText = Html.fromHtml(pShareText).toString();
        pShareText = pShareText.replace("￼", "");

        String twitterText = getTwitterMessage(pShareTitle, pShareLink);

        List<Intent> targetedShareIntents = new ArrayList<Intent>();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        List<ResolveInfo> resInfo = pContext.getPackageManager().queryIntentActivities(shareIntent, 0);

        if (!resInfo.isEmpty()){

            for (ResolveInfo resolveInfo : resInfo) {

                String packageName = resolveInfo.activityInfo.packageName;

                Intent targetedShareIntent = getShareIntent(pContext, packageName, pShareTitle, pShareText, pShareLink, pImageLink, twitterText);
                targetedShareIntents.add(targetedShareIntent);
            }

            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), pContext.getString(R.string.share));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
            pContext.startActivity(chooserIntent);
        }
    }

    private static String getTwitterMessage(String pShareText, String pShareLink) {
        pShareText += " ";
        int textLength = pShareText.length();
        int linkLength = pShareLink.length();

        if((textLength + linkLength) > TWEET_CHARS_LIMIT) {
            return pShareText.substring(0, (TWEET_CHARS_LIMIT - linkLength) - 3) + "..." + pShareLink;
        } else {
            return pShareText + pShareLink;
        }
    }

    private static Intent getShareIntent(Context pContext, String pPackageName, String pShareTitle, String pShareText,
                                  String pShareLink, String pImageLink, String pTwitterText) {

        Intent targetedShareIntent = new Intent(Intent.ACTION_SEND);

        if (pImageLink != null && pImageLink.length() > 0 && !pPackageName.contains("facebook")) {
            File imgFile = new AQuery(pContext).makeSharedFile(pImageLink, "share.png");

            if (imgFile != null && imgFile.exists()) {
                Uri screenshotUri = Uri.fromFile(imgFile);
                targetedShareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            }
        }

        if (pPackageName.contains("vkontakte")) {
            if (pImageLink != null && pImageLink.length() > 0) {
                targetedShareIntent.setType("image/*");

            } else {
                targetedShareIntent.setType("*/*");
            }
        } else {
            targetedShareIntent.setType("text/plain");
        }

        if (pPackageName.contains("twitter")){
            targetedShareIntent.putExtra(Intent.EXTRA_TEXT, pTwitterText);

        } else {
            targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, pShareTitle);

            if (!pPackageName.contains("facebook")) {
                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, pShareText + " " + pShareLink);
            } else {
                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, pShareLink);
            }
        }

        targetedShareIntent.setPackage(pPackageName);

        return targetedShareIntent;
    }
}
