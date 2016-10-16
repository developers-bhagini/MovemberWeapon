package com.app.movemberweapon.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.movemberweapon.R;
import com.app.movemberweapon.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class OpenCameraFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "OpenCameraFragment";

    private View mRootView;
    private ImageView mOpenCamera;
    private Bundle mBundle;
    private Uri imageUri = null;

    public OpenCameraFragment() {
        // Required empty public constructor
    }


    public static OpenCameraFragment newInstance() {
        OpenCameraFragment fragment = new OpenCameraFragment();
        return fragment;
    }

    public static String convertImageUriToFile(Uri imageUri, Activity activity) {

        Cursor cursor = null;
        int imageID = 0;

        try {

            /*********** Which columns values want to get *******/
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.getContentResolver().query(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            //  Get Query Data
            if (null != cursor) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int size = cursor.getCount();

                /*******  If size is 0, there are no images on the SD Card. *****/

                if (size == 0) {


                    Log.d(TAG, "size is 0");
                } else {

                    int thumbID = 0;
                    if (cursor.moveToFirst()) {
                        imageID = cursor.getInt(columnIndex);
                    }
                }
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "" + imageID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBundle = new Bundle();
        mRootView = inflater.inflate(R.layout.open_camera_fragment, container, false);
        mOpenCamera = (ImageView) mRootView.findViewById(R.id.open_camera_id);
        mOpenCamera.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_camera_id:
                selectImage();

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == Constants.REQUEST_CAMERA) {
                String lImageId = convertImageUriToFile(imageUri, getActivity());
                if (lImageId != null) {
                    new LoadImagesFromSDCard().execute("" + lImageId);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.device_not_supported_text), Toast.LENGTH_SHORT).show();
                }
            } else {
                /*if (null != mCallbackManager) {
                    mCallbackManager.onActivityResult(requestCode, resultCode, data);
                }*/
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo_text), getString(R.string.select_from_gallery_text)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_photo_dialog__title_text));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo_text))) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                    imageUri = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(intent, Constants.REQUEST_CAMERA);
                } else if (items[item].equals(getString(R.string.select_from_gallery_text))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            Constants.SELECT_FILE);
                }
            }
        });
        builder.show();
    }

    //TODO:needs to be more generic code for xiomi
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null,
                null);
        Log.d(TAG, "data cursor");
        if (null != cursor && cursor.moveToNext()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);

            Bitmap thumbnail;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 400;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;

            thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);
            //Giving device width to the square image
            thumbnail = Bitmap.createScaledBitmap(thumbnail, getDeviceWidth(), getDeviceWidth(), true);
            mBundle.putParcelable("Photo", thumbnail);
            goToNextScreen();
        } else {
            Toast.makeText(getActivity(), getString(R.string.device_not_supported_text), Toast.LENGTH_SHORT).show();
        }
    }

    private int getDeviceWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void goToNextScreen() {
        PreviewCameraFragment previewFragment = new PreviewCameraFragment();
        previewFragment.setArguments(mBundle);
        FragmentTransaction lTransaction = getFragmentManager().beginTransaction();
        lTransaction.replace(R.id.container, previewFragment);
        lTransaction.commitAllowingStateLoss();
    }

    public class LoadImagesFromSDCard extends AsyncTask<String, Void, Void> {

        Bitmap mBitmap;
        private ProgressDialog Dialog = new ProgressDialog(getActivity());

        protected void onPreExecute() {
            Dialog.setMessage(" Loading image from Sdcard..");
            Dialog.show();
        }


        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;


            try {

                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));

                if (bitmap != null) {
                    //taking device width for both height and width for the square image
                    newBitmap = Bitmap.createScaledBitmap(bitmap, getDeviceWidth(), getDeviceWidth(), true);

                    bitmap.recycle();

                    if (newBitmap != null) {
                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                cancel(true);
            }

            return null;
        }


        protected void onPostExecute(Void unused) {
            if (Dialog != null) {
                Dialog.dismiss();
            }
            if (mBitmap != null) {
                // Set Image to ImageView
                mBundle.putParcelable("Photo", mBitmap);
                goToNextScreen();
            }

        }

    }
}
