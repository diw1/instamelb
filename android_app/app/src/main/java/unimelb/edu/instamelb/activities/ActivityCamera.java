package unimelb.edu.instamelb.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.media.MediaRecorder.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unimelb.edu.instamelb.fragments.FragmentLibrary;
import unimelb.edu.instamelb.materialtest.R;
//import unimelb.edu.instamelb.views.CameraGridLines;

/**
 * Created by bboyce on 12/09/15.
 */
public class ActivityCamera extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String EXTRA_MESSAGE = "unimelb.edu.instamelb.activities.MESSAGE";

    private final static String TAKEN_PHOTO_PATH = "mCurrentPhotoPath";
    private final static String TAKEN_PHOTO_URI = "mCapturedImageURI";

    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;
    private Camera mCamera = null;
    //    private CameraPreview mPreview;
    private ImageView mCameraPreview = null;
    private ImageView selectedImage;
    private Camera.Parameters mParams;
    private FrameLayout preview;
    int flashStatus = 0;
    boolean flashAvailable;
    boolean gridlinesStatus = false;
//    private CameraGridLines mGrid;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    boolean previewCamera = false;
    private int cameraID = 1;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap mcapturedImage;
    private Bitmap mcapturedImageThumbnail;

    public static Uri imageURI;
    public File imageFile;


    private Camera.ShutterCallback mShutterCallback;
    private Camera.PictureCallback mRawImageCallback;
    private Camera.PictureCallback mJpegCallback;
    private Camera.PictureCallback mPostviewCallback;
    private boolean mFaceDetectionRunning = false;

    private static final int CAMERA_MSG_SHUTTER          = 0x002;
    private static final int CAMERA_MSG_POSTVIEW_FRAME   = 0x040;
    private static final int CAMERA_MSG_RAW_IMAGE        = 0x080;
    private static final int CAMERA_MSG_COMPRESSED_IMAGE = 0x100;


    @InjectView(R.id.button_flash)
    Button _flashButton;
    @InjectView(R.id.button_gridlines)
    Button _gridlinesButton;
    @InjectView(R.id.button_takePhoto)
    Button _takePhotoButton;
    @InjectView(R.id.grid)
    ImageView _gridView;
    @InjectView(R.id.button_library)
    Button _libraryButton;
    @InjectView(R.id.imageViewFullSized)
    ImageView _imageFullSize;
    @InjectView(R.id.imageViewThumbnail)
    ImageView _imageThumbnail;

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

        _flashButton.setHeight(borderHeight);
        _gridlinesButton.setHeight(borderHeight);
        _takePhotoButton.setHeight(borderHeight);
        _gridView.setMaxHeight(previewHeight);
        _gridView.setMinimumHeight(previewHeight);

        _gridView.setBackgroundColor(Color.TRANSPARENT);

        // Safely open camera
        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
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
            Log.d("FP", "TRYING TO GET PARAMETERS");
            mParams = mCamera.getParameters();
            Log.d("FP", "GOT PARAMETERS");
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            Log.d("FP", "SET FLASH MODE");
            mCamera.setParameters(mParams);
            Log.d("FP", "SET CAMERA PARAMETERS");
            flashAvailable = true;
            _flashButton.setText("FLASH AUTO");
        } catch (Exception e) {
            flashAvailable = false;
            flashStatus = 2;
            _flashButton.setText("FLASH OFF");
            Log.e("ERROR", "CAMERA PARAMETERS COULD NOT BE LOADED");
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
//                    Toast.makeText(this, "Flash mode not supported", Toast.LENGTH_LONG);
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

//                _imageFullSize.

//

//                if (imageURI!=null) {
//                    imageURI = getCapturedImageURI();
//                    String string = imageURI.toString();
//                    Log.d("FP", "GOT URI STRING: " + string);
//                }
//                else {
//                    Log.d("FP", "imageURI IS NULL");
//                }
//                Intent intent = new Intent(getApplicationContext(), ActivityPhoto.class);
//                String uriMessage = imageURI.toString();
//                intent.putExtra(EXTRA_MESSAGE, uriMessage);
//                startActivity(intent);

//                Intent intent = new Intent(getApplicationContext(), ActivityLibrary.class);
//                startActivity(intent);

                Log.d("FP", "PHOTO TAKING FINISHED");
            }
        });

        // Choose a photo from library
        _libraryButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                PICK_IMAGE_REQUEST = 2;
                Intent selectImage = new Intent();
                selectImage.setType("image/*");
                selectImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(selectImage, "Select Picture"), PICK_IMAGE_REQUEST);

                Log.d("FP", "SELECTED LIBRARY");
            }
        });



//        View.OnClickListener libraryViewListener = new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {

//            FragmentManager fm = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fm.beginTransaction();
////                FragmentLibrary libraryFragment = new FragmentLibrary();
//            FragmentLibrary libraryFragment = FragmentLibrary.newInstance("", "");
//            fragmentTransaction.add(R.id.fragment_photo_option, libraryFragment, "");
//            fragmentTransaction.commit();
//            _currentSelection.setText("LIBRARY");
//            Intent intent = new Intent(getApplicationContext(), ActivityLibrary.class);
//            startActivity(intent);
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 2);
//                PICK_IMAGE_REQUEST = 2;
//                Intent selectImage = new Intent();
//                selectImage.setType("image/*");
//                selectImage.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(selectImage, "Select Picture"), PICK_IMAGE_REQUEST);
//
//
//
//
//                Log.d("FP", "SELECTED LIBRARY");
//            }
//        };
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

//            _imageFullSize.setImageURI(imageURI);
//            _imageThumbnail.setImageURI(imageURI);


            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                fos.write(data);
                fos.close();
                Log.d("FP", "FILE OUTPUT STREAM CREATED");
//                mCamera.stopPreview();
//                mCamera.release();

//                BitmapFactory.Options scalingOptions = new BitmapFactory.Options();
//                scalingOptions.inSampleSize = camera.getParameters().getPictureSize().width/ _imageFullSize.getMeasuredWidth();
//                scalingOptions.inSampleSize = camera.getParameters().getPictureSize().height/ _imageFullSize.getMeasuredHeight();
//                mcapturedImage = BitmapFactory.decodeByteArray(data, 0, data.length, null);

                Intent intent = new Intent(getApplicationContext(), ActivityPhoto.class);
                String uriMessage = imageURI.toString();
                intent.putExtra(EXTRA_MESSAGE, uriMessage);
                startActivity(intent);
            }
            catch (FileNotFoundException e) {
                Log.e("FP", "File not found");
            }
            catch (IOException e) {
                Log.e("FP", "Error accessing file");
            }
        }
    };

    private static File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Instamelb");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("FP", "failed to create directory");
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

 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
//                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//                            bitmapOptions);
//
//                    selectedImage.setImageBitmap(bitmap);
//
//                    String path = android.os.Environment
//                            .getExternalStorageDirectory()
//                            + File.separator
//                            + "Phoenix" + File.separator + "default";
//                    f.delete();
//                    OutputStream outFile = null;
//                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                    try {
//                        outFile = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
//                        outFile.flush();
//                        outFile.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            } else if (requestCode == 2) {

//                Uri selectedImage = data.getData();
//                String[] filePath = {MediaStore.Images.Media.DATA };
//                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));

                Uri uri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Log.d("FP", String.valueOf(bitmap));

                    ImageView imageView = (ImageView) findViewById(R.id.imageViewFullSized);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }



//                Log.d("Picture Path: ", picturePath+"");
//                selectedImage.setImageBitmap(thumbnail);



            }
        }
    }*/

/*

    // Create a file Uri for saving an image
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
*/













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
                b.setPixel(i, j, Color.RED);
            }
        }

        for (int i = width2; i < width2 + 3; i++) {
            for (int j = 0; j < previewHeight; j++) {
                b.setPixel(i, j, Color.RED);
            }
        }

        for (int i = 0; i < previewWidth; i++) {
            for (int j = height1; j < height1 + 3; j++) {
                b.setPixel(i, j, Color.RED);
            }
        }

        for (int i = 0; i < previewWidth; i++) {
            for (int j = height2; j < height2 + 3; j++) {
                b.setPixel(i, j, Color.RED);
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


//    public static Camera getCameraInstance(){
//        Camera c = null;
//        try {
//            c = Camera.open();
//        }
//        catch(Exception e) {
//            Log.e("ERROR", "CAMERA COULD NOT BE OPENED");
//        }
//        return c;
//    }

//    private boolean safeCamera(CameraPreview mPreview) {
//        boolean safeToOpen = false;
//
//        try {
//            releaseCameraAndPreview(mPreview);
//
//        }
//        catch (Exception e){
//            return safeToOpen
//        }
//    }

//    public static Camera getCamera() {
//        Camera c = null;
//        try {
//            c = Camera.open();
//        }
//        catch(Exception e) {
//
//        }
//        return c;
//
//    }

    private void releaseCameraAndPreview() {
//        mCameraPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }



    // Check for cameras
    private boolean checkForCamera(Context context, Camera c) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        }
        else {
            return false;
        }

    }

    // Check for front facing cameras
    private boolean checkForFrontCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        }
        else {
            return false;
        }
    }

    // Check for flash capability
    private boolean checkForFlash(Context context) {

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        }
        else {
            return false;
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        if (mCurrentPhotoPath != null) {
//            savedInstanceState.putString(TAKEN_PHOTO_PATH, mCurrentPhotoPath);
//        }
//        if (mCapturedImageURI != null) {
//            savedInstanceState.putString(TAKEN_PHOTO_URI, mCapturedImageURI.toString());
//        }
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        if (savedInstanceState.containsKey(TAKEN_PHOTO_PATH)) {
//            mCurrentPhotoPath = savedInstanceState.getString(TAKEN_PHOTO_PATH);
//        }
//        if (savedInstanceState.containsKey(TAKEN_PHOTO_URI)) {
//            mCapturedImageURI = Uri.parse(savedInstanceState.getString(TAKEN_PHOTO_URI));
//        }
//        super.onRestoreInstanceState(savedInstanceState);
//    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public Uri getCapturedImageURI() {
        return mCapturedImageURI;
    }

    public void setCapturedImageURI(Uri mCapturedImageURI) {
        this.mCapturedImageURI = mCapturedImageURI;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
//
        try {
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
                mCamera.setPreviewDisplay(mSurfaceHolder);
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
