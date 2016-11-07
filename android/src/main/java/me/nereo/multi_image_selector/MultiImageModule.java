package me.nereo.multi_image_selector;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by 90927 on 2016/8/29.
 */
public class MultiImageModule extends ReactContextBaseJavaModule implements ActivityEventListener{
    private static final int REQUEST_IMAGE = 2;
    private final ReactApplicationContext reactContext;
    private Activity activity;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private boolean mShowCamera = true;
    private int maxNum = 5;
    private boolean multiple = true;
    private ArrayList<String> mSelectPath;
    private Promise mPromise;
    private boolean cropping = false;
    private int width = 100;
    private int height = 100;

    public MultiImageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
        this.reactContext = reactContext;
    }
    @Override
    public String getName() {
        return "MultiImage";
    }
    @ReactMethod
    public void pickImage(ReadableMap readableMap, Promise promise){
        mPromise = promise;
        activity = getCurrentActivity();
        setConfiguration(readableMap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, activity.getString(R.string.mis_permission_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        }else {
            MultiImageSelector selector = MultiImageSelector.create();
            selector.showCamera(mShowCamera);
            selector.count(maxNum);
            if (!multiple) {
                selector.single();
            } else {
                selector.multi();
            }
            selector.origin(mSelectPath);
            selector.start(activity, REQUEST_IMAGE);
        }
    }
    private void setConfiguration(final ReadableMap options) {
        mShowCamera = options.hasKey("showCamera") ? options.getBoolean("showCamera"):mShowCamera;
        maxNum = options.hasKey("maxNum") ? options.getInt("maxNum"): maxNum;
        multiple = options.hasKey("multiple") ? options.getBoolean("multiple"):multiple;
        cropping = options.hasKey("cropping") ? options.getBoolean("cropping"):cropping;
        width = options.hasKey("width") ? options.getInt("width"): width;
        height = options.hasKey("height") ? options.getInt("height"): height;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK){
            mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
            WritableArray writableArray = Arguments.createArray();
            String path = "";
            for(String p: mSelectPath){
                writableArray.pushString("file://"+p);
                path = p;
            }
            if(cropping && multiple == false){
                startCropping(path);
            }else{
                mPromise.resolve(writableArray);
            }
        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK){
            mPromise.resolve("file://"+UCrop.getOutput(data).getPath());
        }
    }
    public void startCropping(String path){
        Uri mUri = Uri.parse("content://media/external/images/media");
        Uri mImageUri = null;
        Cursor cursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String d = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            if (path.equals(d)) {
                int ringtoneID = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.MediaColumns._ID));
                mImageUri = Uri.withAppendedPath(mUri, ""
                        + ringtoneID);
                break;
            }
            cursor.moveToNext();
        }
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        UCrop.of(mImageUri, Uri.fromFile(new File(activity.getCacheDir(), UUID.randomUUID().toString() + ".jpg")))
                .withMaxResultSize(width, height)
                .withAspectRatio(width, height)
                .withOptions(options)
                .start(activity);
    }
    private void requestPermission(final String permission, String rationale, final int requestCode){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }
    @Override
    public void onNewIntent(Intent intent) {

    }
}
