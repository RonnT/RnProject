package top.titov.gas.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import top.titov.gas.MyApp;
import top.titov.gas.R;
import top.titov.gas.utils.CONST;

public class BitmapHelper {
    private static final int
            THUMBNAIL_SIZE = 212,
            MAX_SIDE_SIZE = 1024;

    public static final String USER_PHOTO = "userPhoto";

    public static File getWorkingPath(Context context) {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ? PathManager.getExternalPath(context)
                : PathManager.getLocalPath(context);
    }

    public static class PathManager {
        private PathManager() {
        }

        public static File getExternalPath(Context context) {
            File tmp = context.getExternalFilesDir(null);
            if (tmp != null) return tmp.getParentFile();

            return null;
        }

        public static File getLocalPath(Context context) {
            File tmp = context.getFilesDir();
            if (tmp != null) return tmp.getParentFile();

            return null;
        }
    }

    public static void saveBitmapToFile(String fileName, Bitmap bitmap,
                                        Bitmap.CompressFormat compressFormat) {
        String storePath;
        try {
            storePath = BitmapHelper.getWorkingPath(MyApp.getAppContext()) + "/files/";
            File file = new File(storePath, fileName);

            if (file.exists()) file.delete();

            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(compressFormat, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBitmapPathByName(String fileName) {
        return BitmapHelper.getWorkingPath(MyApp.getAppContext()) + "/files/" + fileName;
    }

    /*
    public static Photo parseGotPhoto(int resultCode, Intent data) {
        Bitmap bitmap = null;
        if ( resultCode == Activity.RESULT_OK ) {
            InputStream input;
            Uri originalUri = data.getData();
            Bundle bundle;
            if (originalUri == null) {
                bundle = data.getExtras();

                if (bundle != null) {
                    bitmap = (Bitmap) bundle.get("data");
                } else {
                    ToastHelper.showToast(R.string.photo_cant_add);
                    return null;
                }
            } else {
                try {
                    input = MyApp.getAppContext().getContentResolver()
                            .openInputStream(originalUri);

                    bitmap = BitmapFactory.decodeStream(input);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastHelper.showToast(R.string.photo_cant_add);
                    return null;
                }
            }

            int bWidth = bitmap.getWidth();
            int bHeight = bitmap.getHeight();
            int offsetX, offsetY;
            int croppedSize;

            if(bHeight > bWidth) {
                croppedSize = bWidth;
                offsetX = 0;
                offsetY = bHeight / 2 - bWidth / 2;
            } else {
                croppedSize = bHeight;
                offsetX = bWidth / 2 - bHeight / 2;
                offsetY = 0;
            }

            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, offsetX, offsetY,
                    croppedSize, croppedSize);
            bitmap = Bitmap.createScaledBitmap(croppedBitmap,
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            String imageFileName = System.currentTimeMillis() + ".png";
            saveBitmapToFile(imageFileName, bitmap, Bitmap.CompressFormat.PNG);

            return new Photo(bitmap, getBitmapPathByName(imageFileName), false);
        }

        return null;
    }

    public static Photo parseGotPhotoFull(int resultCode, Intent data) {
        Bitmap bitmap = null, thumbnail = null;
        String imageFileName = "";

        if ( resultCode == Activity.RESULT_OK ) {
            InputStream input;
            Uri originalUri = data.getData();
            Bundle bundle;
            if (originalUri == null) {
                bundle = data.getExtras();

                if (bundle != null) {
                    bitmap = (Bitmap) bundle.get("data");
                } else {
                    ToastHelper.showToast(R.string.photo_cant_add);
                    return null;
                }
            } else {
                try {
                    input = MyApp.getAppContext().getContentResolver()
                            .openInputStream(originalUri);

                    bitmap = BitmapFactory.decodeStream(input);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    ToastHelper.showToast(R.string.photo_cant_add);
                    return null;
                }
            }

            double divider;
            double newWidth, newHeight;
            int bWidth = bitmap.getWidth();
            int bHeight = bitmap.getHeight();

            if(bWidth > bHeight) {
                newWidth = bitmap.getWidth() > MAX_SIDE_SIZE ? MAX_SIDE_SIZE : bitmap.getWidth();
                divider = bitmap.getWidth() / newWidth;
                newHeight = bitmap.getHeight() / divider;
            } else {
                newHeight = bitmap.getHeight() > MAX_SIDE_SIZE ? MAX_SIDE_SIZE : bitmap.getHeight();
                divider = bitmap.getHeight() / newHeight;
                newWidth = bitmap.getWidth() / divider;
            }

            bitmap = Bitmap.createScaledBitmap(bitmap, (int) newWidth, (int) newHeight, false);

            int offsetX = newWidth >= newHeight ? (int) (newWidth / 2 - newHeight / 2) : 0;
            int offsetY = newHeight > newWidth ? (int) (newHeight / 2 - newWidth / 2) : 0;
            int size = (int) (newWidth >= newHeight ? newHeight : newWidth);
            thumbnail = Bitmap.createBitmap(bitmap, offsetX, offsetY, size, size);

            imageFileName = System.currentTimeMillis() + ".jpg";
            saveBitmapToFile(imageFileName, bitmap, Bitmap.CompressFormat.JPEG);

            return new Photo(thumbnail, getBitmapPathByName(imageFileName), false);
        }

        return null;
    }
    */

    public static Intent getBitmapChooserIntent() {
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        final PackageManager packageManager = MyApp.getAppContext().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            intent.setPackage(packageName);
            cameraIntents.add(intent);
        }

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Выберите приложение");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        return chooserIntent;
    }

    /*
    public static void checkCountPhotos(Activity pActivity, List<Photo> pPhotosList) {
        if (pPhotosList.size() > CONST.MAX_PHOTO_NUMBER_IN_QUERY) {
            pPhotosList.remove(0);
        }
         // TODO: check if it's needed
        else if ((pPhotosList.size() > 0 && !pPhotosList.get(pPhotosList.size() - 1).isDefault())
                || pPhotosList.size() == 0) {
            pPhotosList.add(pPhotosList.size(), new Photo(true));
        }

        pActivity.sendBroadcast(new Intent(CONST.INTENT_UPDATE_PHOTOS));
    }
    */

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return mutableBitmap;
    }

    public static Bitmap getAzsMarkerWithPrice(Context pContext, float pPrice, boolean pIsFavorite, int brand) {
        View view;
        if (brand == CONST.AZS_TYPE_0) {
            view = LayoutInflater.from(pContext).inflate(R.layout.view_map_azs_with_price_type_0, null);
        }
        else {
            view = LayoutInflater.from(pContext).inflate(R.layout.view_map_azs_with_price_type_1, null);
        }
        AQuery aq = new AQuery(view);
        aq.id(R.id.view_marker_price_favorite).visibility(pIsFavorite ? View.VISIBLE : View.GONE);

        view.setDrawingCacheEnabled(true);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(0, 0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        AzsHelper.setPriceToView(aq, pPrice);
        view.buildDrawingCache(true);

        return Bitmap.createBitmap(view.getDrawingCache());
    }

    public static Bitmap getAzsMarkerClustered(Context pContext, int pCount) {
        View view = LayoutInflater.from(pContext).inflate(R.layout.view_map_azs_clustered, null);

        AQuery aq = new AQuery(view);
        String count = MyApp.getStringFromRes(R.string.azs_count_prefix) + pCount;
        aq.id(R.id.view_map_azs_clustered_count).text(count);

        view.setDrawingCacheEnabled(true);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(0, 0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);

        return Bitmap.createBitmap(view.getDrawingCache());
    }
}
