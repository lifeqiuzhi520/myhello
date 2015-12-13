package com.camera.www.camera.activity.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.camera.www.camera.BaseAct;
import com.camera.www.camera.MessageWhats;
import com.camera.www.camera.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TakePhotoAct extends BaseAct implements MessageWhats {


    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Button startButton;
    private boolean mIsRecording = false;
    private MediaRecorder mMediaRecorder;
    private File mPhotoFile;
    private int mCameraCount = 0;
    private SurfaceHolder.Callback mCallback;
    private ImageView mToggleFlash;
    private int mWaterResIds[];
    private ListView mWaterListView;
    private View mBrightJustContainer;
    private ScrollView mScrollView;
    private ImageView mAutoFocusView;
    private View mAutoFocusContainer;
    private Handler mHandler;


    private int mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken");
            mPhotoFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (mPhotoFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            try {
                FileOutputStream fos = new FileOutputStream(mPhotoFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);//
                fos.flush();
                fos.close();
                Log.d(TAG, "save picture success");
                //notify
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(mPhotoFile); //out is your output file
                    mediaScanIntent.setData(contentUri);
                    TakePhotoAct.this.sendBroadcast(mediaScanIntent);
                } else {
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://"
                                    + Environment.getExternalStorageDirectory())));
                }
                releaseCamera();
                initPreview();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        initHandler();
        initViews();
        initData();
        initAdapter();
        initListeners();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_START_AUTO_FOCUS:
                        mAutoFocusView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "handleMessage:  mAutoFocusContainer.setVisibility(View" +
                                ".VISIBLE);");
                        break;
                    case WHAT_AUTO_FOCUS_COMPLETE_SUCCES:
                        mAutoFocusView.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "handleMessage:WHAT_AUTO_FOCUS_COMPLETE_SUCCES ");
                        break;
                }
            }
        };
    }

    private void initListeners() {
        mWaterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mWaterListView.setVisibility(View.GONE);
            }
        });
        mAutoFocusContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP) {
                    int[] location = new int[2];
                    Log.d(TAG, "onTouch: location" + location[0] + "location 1 " + location[1]);
                    mAutoFocusView.getLocationOnScreen(location);
                    int Xoffset = (int) (event.getX() - location[0]);
                    int Yoffset = (int) (event.getY() - location[1]);
                    int height = mAutoFocusView.getHeight();
                    int width = mAutoFocusView.getWidth();
                    Xoffset = Xoffset - width / 2;
                    Yoffset = Yoffset - height / 2;
                    mAutoFocusView.offsetLeftAndRight(Xoffset);
                    mAutoFocusView.offsetTopAndBottom(Yoffset);
                    startAutoFocus();
                    return true;
                }
                return true;

            }
        });
    }

    private void initData() {
        mWaterResIds = new int[]{R.drawable.water_mark_01, R.drawable.water_mark_02,
                R.drawable.water_mark_03,
                R.drawable.water_mark_04,
                R.drawable.water_mark_05,
                R.drawable.water_mark_08,
                R.drawable.water_mark_06,
                R.drawable.water_mark_default,
                R.drawable.water_mark_empty,
        };
    }

    private void initAdapter() {
        mWaterListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mWaterResIds.length;
            }

            @Override
            public Integer getItem(int position) {
                return mWaterResIds[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout
                            .water_list_view_item, parent, false);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.imageView.setImageResource(getItem(position));
                return convertView;
            }
        });
    }

    private static class ViewHolder {
        public View mItem;
        public ImageView imageView;

        public ViewHolder(View root) {
            mItem = root;
            imageView = (ImageView) root.findViewById(R.id.water_mark);

        }
    }

    private void initViews() {
        mAutoFocusView = findView(R.id.auto_focus);
        mWaterListView = findView(R.id.water_list_view);
        mAutoFocusContainer = findView(R.id.auto_focus_container);
        mAutoFocusView = findView(R.id.auto_focus);
        mBrightJustContainer = findView(R.id.bright_ajust_container);
        mScrollView = findView(R.id.bright_scroll_view);
        mCameraCount = Camera.getNumberOfCameras();
        mSurfaceView = (SurfaceView) findViewById(R.id.suface_view);
        mSurfaceHolder = mSurfaceView.getHolder();
        mToggleFlash = findView(R.id.toggle_flash);
        mWaterListView = findView(R.id.water_list_view);
        mCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCamera();
            }
        };
        mSurfaceHolder.addCallback(mCallback);
    }


    private void startAutoFocus() {
        mHandler.sendEmptyMessage(WHAT_START_AUTO_FOCUS);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(parameters);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    onAutoFocusSeccess();
                }
            }
        });
    }

    private void onAutoFocusSeccess() {
        mHandler.removeMessages(WHAT_AUTO_FOCUS_COMPLETE_SUCCES);
        mHandler.sendEmptyMessageDelayed(WHAT_AUTO_FOCUS_COMPLETE_SUCCES, 500);
//        mHandler.sendEmptyMessage(WHAT_AUTO_FOCUS_COMPLETE_SUCCES);
    }

    private void initCamera() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        mCamera.setParameters(parameters);
        startAutoFocus();
    }

    protected void initPreview() {
        mCamera = Camera.open(mCurrentCameraId);
        initCamera();
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setCameraDisplayOrientation(this, mCurrentCameraId, mCamera);
        mCamera.startPreview();
    }

    protected void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void onPhotoClicked(View view) {
        mCamera.takePicture(null, null, mPicture);
    }

    public void toggleCamera(View view) {

        if (mCameraCount == 2) {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera
                    .CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
            initPreview();

        } else {
            return;
        }

    }

    /**
     * 打开或关闭闪光灯
     *
     * @param view
     */
    public void toggleBright(View view) {
        toggleBright(mCamera);


    }

    /**
     * 打开或者关闭亮度调整器
     *
     * @param view
     */

    public void toggleBrightAjust(View view) {
        int visibility = mBrightJustContainer.getVisibility();
        mBrightJustContainer.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);

    }

    /**
     * 水印打开或关闭
     *
     * @param view
     */
    public void toggleWater(View view) {
        int visibility = mWaterListView.getVisibility();
        mWaterListView.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);

    }

    private void toggleBright(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mToggleFlash.setSelected(true);

            }
        } else if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mToggleFlash.setSelected(false);
            } else {
                Log.e(TAG, "FLASH_MODE_OFF not supported");
            }
        }
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera App");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("linc", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}