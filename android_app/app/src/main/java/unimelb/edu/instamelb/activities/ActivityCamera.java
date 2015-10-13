package unimelb.edu.instamelb.activities;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unimelb.edu.instamelb.materialtest.R;

/**
 * Created by bboyce on 12/09/15.
 */
public class ActivityCamera extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    public final static String EXTRA_MESSAGE = "unimelb.edu.instamelb.activities.MESSAGE";

    private Camera mCamera = null;
    private Camera.Parameters mParams;
    int flashStatus = 0;
    boolean flashAvailable;
    boolean gridlinesStatus = false;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    boolean previewCamera = false;


    public static Uri imageURI;
    public File imageFile;

    @InjectView(R.id.button_flash)
    Button _flashButton;
    @InjectView(R.id.button_gridlines)
    Button _gridlinesButton;
    @InjectView(R.id.button_takePhoto)
    Button _takePhotoButton;
    @InjectView(R.id.grid)
    ImageView _gridView;
    @InjectView(R.id.imageViewFullSized)
    ImageView _imageFullSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("FP", "CREATED ACTIVITYCAMERA INSTANCE");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.inject(this);

        // Get display dimensions
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        final int previewWidth = metrics.widthPixels;
        int previewHeight = previewWidth;
        int borderHeight = (metrics.heightPixels - previewWidth) / 2;

        _gridView.setMaxHeight(previewHeight);
        _gridView.setMinimumHeight(previewHeight);

        _gridView.setBackgroundColor(Color.TRANSPARENT);

        // Safely open camera
        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//            mCamera.setDisplayOrientation(90);
            Log.d("FP", "CAMERA OPENED");
        }
        catch (Exception e) {
            if (mCamera != null) {
                mCamera.release();
            }
            mCamera= null;
            Log.e("ERROR", "CAMERA FAILED TO OPEN");
        }



        getWindow().setFormat(PixelFormat.UNKNOWN);
        mSurfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        mSurfaceView.setMinimumHeight(previewHeight);
        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceHolder.setFixedSize(previewWidth, previewHeight);
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Set initial camera parameters
        try {
            Log.d("FP", "TRYING TO GET CAMERA PARAMETERS");
            mParams = mCamera.getParameters();
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            mCamera.setParameters(mParams);
            Log.d("FP", "SET CAMERA PARAMETERS");
            flashAvailable = true;
            _flashButton.setText("FLASH AUTO");
        } catch (Exception e) {
            flashAvailable = false;
            flashStatus = 2;
            _flashButton.setText("FLASH OFF");
            Log.e("ERROR", "CAMERA PARAMETERS COULD NOT BE LOADED");
            Toast.makeText(this, "Flash mode not supported", Toast.LENGTH_LONG);
        }

        // Toggle Flash settings (AUTO/ON/OFF)
        _flashButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashAvailable == true) {
                    try {
                        if (flashStatus == 0) {
                            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                            mCamera.setParameters(mParams);
                            _flashButton.setText("FLASH ON");
                            flashStatus = 1;
                            Log.d("FP", "FLASH ON");
                        } else if (flashStatus == 1) {
                            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(mParams);
                            _flashButton.setText("FLASH OFF");
                            flashStatus = 2;
                            Log.d("FP", "FLASH OFF");
                        } else if (flashStatus == 2) {
                            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                            mCamera.setParameters(mParams);
                            _flashButton.setText("FLASH AUTO");
                            flashStatus = 0;
                            Log.d("FP", "FLASH AUTO");
                        } else {
                            flashStatus = 0;
                            Log.d("FP", "FLASH AUTO");
                        }
                    } catch (Exception e) {
                        Log.e("ERROR", "FLASH NOT FOUND");
                        _flashButton.setText("FLASH OFF");

                    }
                }
            }
        });

        // Toggle gridlines on/off
        _gridlinesButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previewWidth = _gridView.getWidth();
                int previewHeight = _gridView.getHeight();

                if (!gridlinesStatus) {
                    Log.d("FP", "GRIDLINES ACTIVATED");
                    _gridlinesButton.setText("GRID ON");
                    gridlinesStatus = true;

                    _gridView.setBackgroundColor(Color.TRANSPARENT);

                    Bitmap b = setGridlines(previewWidth, previewHeight);
                    _gridView.setImageBitmap(b);
                    _gridView.setEnabled(true);
                    _gridView.bringToFront();

                } else {
                    Log.d("FP", "GRIDLINES DEACTIVATED");
                    _gridlinesButton.setText("GRID OFF");
                    gridlinesStatus = false;
                    _gridView.setEnabled(false);

                    Bitmap b = removeGridlines(previewWidth, previewHeight);
                    _gridView.setImageBitmap(b);
                    _gridView.setEnabled(true);
                    _gridView.bringToFront();
                }
            }
        });

        // Take the photo
        _takePhotoButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FP", "ABOUT TO TAKE PHOTO - LAUNCH ActivityPhoto");

                mCamera.setPreviewCallback(null);
                mCamera.takePicture(null, null, mPicture);
                Log.d("FP", "PHOTO TAKEN");
            }
        });

    }


    public static Uri getImageURI() {
        return imageURI;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.d("FP", "LAUNCHED onPictureTaken()");
            imageFile = getOutputMediaFile();
            imageURI = Uri.fromFile(getOutputMediaFile());

            String uriString = imageURI.toString();

            Log.d("FP", "GOT OUTPUT FILE: " + uriString);

            if (imageFile == null) {
                Log.e("FP", "ERROR CREATING FILE");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                fos.write(data);
                fos.close();
                Log.d("FP", "FILE OUTPUT STREAM CREATED");

                Intent intent = new Intent(getApplicationContext(), ActivityPhoto.class);
                String uriMessage = imageURI.toString();
                intent.putExtra(EXTRA_MESSAGE, uriMessage);
                startActivity(intent);
            }
            catch (FileNotFoundException e) {
                Log.e("FP", "FILE NOT FOUND");
            }
            catch (IOException e) {
                Log.e("FP", "ERROR ACCESSING FILE");
            }
        }
    };

    private static File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Instamelb");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("FP", "FAILED TO CREATE DIRECTORY");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }


    //  Helper method to set Gridlines over camera preview
    private Bitmap setGridlines(int previewWidth, int previewHeight) {

        int width1 = previewWidth / 3;
        int width2 = 2 * previewWidth / 3;
        int height1 = previewHeight / 3;
        int height2 = 2 * previewHeight / 3;

        Bitmap b = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        for (int i = 0 ; i < previewWidth ; i++) {
            for (int j = 0 ; j < previewHeight ; j++) {
                b.setPixel(i, j, Color.TRANSPARENT);
            }
        }

        for (int i = width1; i < width1 + 3; i++) {
            for (int j = 0; j < previewHeight; j++) {
                b.setPixel(i, j, Color.LTGRAY);
            }
        }

        for (int i = width2; i < width2 + 3; i++) {
            for (int j = 0; j < previewHeight; j++) {
                b.setPixel(i, j, Color.LTGRAY);
            }
        }

        for (int i = 0; i < previewWidth; i++) {
            for (int j = height1; j < height1 + 3; j++) {
                b.setPixel(i, j, Color.LTGRAY);
            }
        }

        for (int i = 0; i < previewWidth; i++) {
            for (int j = height2; j < height2 + 3; j++) {
                b.setPixel(i, j, Color.LTGRAY);
            }
        }
        return b;
    }

    //  Helper method to remove Gridlines over camera preview
    private Bitmap removeGridlines(int previewWidth, int previewHeight) {
        Bitmap b = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        for (int i = 0 ; i < previewWidth ; i++) {
            for (int j = 0 ; j < previewHeight ; j++) {
                b.setPixel(i, j, Color.TRANSPARENT);
            }
        }
        return b;
    }

    private void releaseCameraAndPreview() {
//        mCameraPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
//
        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
        }
        catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("FP", "SURFACE CHANGED");

        if(previewCamera){
            mCamera.stopPreview();
            previewCamera = true;
        }

        if (mCamera != null){
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                previewCamera = true;
            }
            catch (IOException E) {
                Log.e("FP", "COULDN'T SET PREVIEW DISPLAY");
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
        }
        mCamera = null;
//        previewCamera = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        ActivityCompat.finishAfterTransition(this);

        Intent myIntent = new Intent(getApplicationContext(), ActivityMain.class);

        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        return;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageURI != null) {
            outState.putString("cameraImageUri", imageURI.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("cameraImageUri")) {
            imageURI = Uri.parse(savedInstanceState.getString("cameraImageUri"));
        }
    }
}
