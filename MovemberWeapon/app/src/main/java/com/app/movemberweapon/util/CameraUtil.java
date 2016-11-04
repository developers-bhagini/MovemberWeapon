package com.app.movemberweapon.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by aarokiax on 11/4/2016.
 */

public class CameraUtil {

    /**
     * Handles pre V19 uri's
     * @param context
     * @param contentUri
     * @return
     */
    public static String getPathForPreV19(Context context, Uri contentUri) {
        String res = null;

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();

        return res;
    }

    /**
     * Handles V19 and up uri's
     * @param context
     * @param contentUri
     * @return path
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathForV19AndUp(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    public static String getRealPathFromURI(Context context,
                                            Uri contentUri) {
        String uriString = String.valueOf(contentUri);
        boolean goForKitKat= uriString.contains("com.android.providers");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && goForKitKat) {
            Log.i("KIKAT","YES");
            return getPathForV19AndUp(context, contentUri);
        } else {

            return getPathForPreV19(context, contentUri);
        }
    }


    public static Bitmap changeOrientationIfRequired(Uri photoUri, Bitmap bitmap, Context mContext){
        try {
            ExifInterface exif = new ExifInterface(getRealPathFromURI(mContext,photoUri));
            int exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (exifRotation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        }catch(Exception e){
            Log.e("CameraUtil",e.getMessage());
        }
        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }
}
