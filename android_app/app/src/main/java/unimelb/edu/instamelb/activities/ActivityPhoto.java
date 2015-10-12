package unimelb.edu.instamelb.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unimelb.edu.instamelb.materialtest.R;

/**
 * Created by bboyce on 12/09/15.
 */
public class ActivityPhoto extends AppCompatActivity {



    private static final int REQUEST_PHOTO = 0;
    private ViewPager mViewPager;
    int seekBarValue;
    private Uri mCapturedImageURI = null;

    // Variables for cropping
    private float startX;
    private float startY;
    private float endX;
    private float endY;

    final int THUMBSIZE = 64;
    Bitmap originalPhoto, editedPhoto, newImage;
    Bitmap originalThumbnail, grayThumbnail, warmThumbnail, coolThumbnail;

    public Uri mImageUri;
    public File mImageFile;

    Bitmap bitmap;
    Bitmap imageThumbnail;

    @InjectView(R.id.brightnessSeekBar)
    SeekBar _brightnessSeekBar;
    @InjectView(R.id.contrastSeekBar)
    SeekBar _contrastSeekBar;
    @InjectView(R.id.photoPreview)
    ImageView _editPhoto;
    @InjectView(R.id.back_button)
    Button _backButton;

    @InjectView(R.id.upload_button)
    Button _uploadButton;

    @InjectView(R.id.photoThumbnail)
    ImageView _originalThumbnail;
    @InjectView(R.id.thumbnailFilter1)
    ImageView _grayThumbnail;
    @InjectView(R.id.thumbnailFilter2)
    ImageView _warmThumbnail;
    @InjectView(R.id.thumbnailFilter3)
    ImageView _coolThumbnail;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_photo);
        ButterKnife.inject(this);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        final int previewWidth = metrics.widthPixels;
        int photoPreviewHeight = previewWidth;
        int thumbnailWidth = previewWidth / 4;

        // Get photo URI & create bitmap to edit
        mImageUri = ActivityCamera.getImageURI();
        mImageFile = new File(mImageUri.getPath());

//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
//
////            imageThumbnail= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mImageUri.toString()),THUMBSIZE, THUMBSIZE);
//        }
//        catch (IOException e){
//            Log.e("ERROR", "BITMAP NOT AVAILABLE");
//
//        }


        bitmap = scaledImage(metrics, mImageUri, previewWidth, false);
        originalThumbnail = scaledImage(metrics, mImageUri, thumbnailWidth, true);



        final ActivityEditPhoto photo = new ActivityEditPhoto(this);
        final ActivityEditPhoto photoThumbnail = new ActivityEditPhoto(this);

        // Create Thumbnails
        originalThumbnail = createThumbnail(imageThumbnail, thumbnailWidth);
        originalThumbnail = imageThumbnail;
        _originalThumbnail.setImageBitmap(originalThumbnail);
//        _originalThumbnail = setThumbnailSize(_originalThumbnail, thumbnailWidth);
//
//        grayThumbnail = createThumbnail(imageThumbnail, thumbnailWidth);
//        warmThumbnail = createThumbnail(originalThumbnail, thumbnailWidth);
//        coolThumbnail = createThumbnail(originalThumbnail, thumbnailWidth);
//        Log.d("FP", "NEW THUMBNAILS CREATED");
//
//        // Apply filters to thumbnails
//        grayThumbnail = photoThumbnail.grayscaleFilter(originalThumbnail);
//        warmThumbnail = photoThumbnail.sunsetFilter(originalThumbnail);
//        coolThumbnail = photoThumbnail.desaturatedFilter(originalThumbnail);
//
//        // Set thumbnail images to view
//        _grayThumbnail.setImageBitmap(grayThumbnail);
//        _warmThumbnail.setImageBitmap(warmThumbnail);
//        _coolThumbnail.setImageBitmap(coolThumbnail);

        _originalThumbnail.setEnabled(true);
//        _originalThumbnail.bringToFront();



//        Button _photoButton = (Button) findViewById(R.id.button_photo);
//        Button _libraryButton = (Button) findViewById(R.id.button_library);
//        final TextView _currentSelection = (TextView) findViewById(R.id.current_selection);



        _editPhoto.setMinimumHeight(photoPreviewHeight);
        _editPhoto.setMaxHeight(photoPreviewHeight);
//        Drawable d = getResources().getDrawable(R.drawable.sonic);
//        _editPhoto.setImageDrawable(bitmap);

        _editPhoto.setImageBitmap(bitmap);
        originalPhoto = bitmap;
        editedPhoto = bitmap;

//        originalPhoto = ((BitmapDrawable) _editPhoto.getDrawable()).getBitmap();
//        editedPhoto = ((BitmapDrawable) _editPhoto.getDrawable()).getBitmap();

        // Return to Choose Library/Camera Screen
        _backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FP", "CANCEL EDIT - TAKE NEW PHOTO");
                Intent intent = new Intent(getApplicationContext(), ActivityCamera.class);
                startActivity(intent);

            }
        });
        _brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                setButtons(false);
                newImage = photo.adjustBrightness(editedPhoto, progress-255);
                _editPhoto.setImageBitmap(newImage);
                setButtons(true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        _contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setButtons(false);
                newImage = photo.adjustContrast(editedPhoto, progress - 255);
                _editPhoto.setImageBitmap(newImage);
                setButtons(true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        _originalThumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);
                _editPhoto.setImageBitmap(originalPhoto);
                Log.d("FP", "SELECTED ORIGINAL PHOTO");
                setButtons(true);
            }
        });

        _grayThumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                Log.d("FP", "BUTTONS DISABLED");
                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.grayscaleFilter(originalPhoto);

                Log.d("FP", "IMAGE FILTERED");
                _editPhoto.setImageBitmap(newImage);

                Log.d("FP", "SELECTED GRAYSCALE PHOTO");
                setButtons(true);

                Log.d("FP", "BUTTONS ENABLED");
            }
        });

        _warmThumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.sunsetFilter(originalPhoto);
                _editPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED SUNSET PHOTO");
                setButtons(true);
            }
        });

        _coolThumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.desaturatedFilter(originalPhoto);
                _editPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED DESATURATED PHOTO");
                setButtons(true);
            }
        });

        _uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                newImage = ((BitmapDrawable) _editPhoto.getDrawable()).getBitmap();
                setButtons(true);

            }
        });



        // Crop photo
        _editPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setButtons(false);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = event.getX();
                    startY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    // Crop Photo Here
                    endX = event.getX();
                    endY = event.getY();
                }

                newImage = photo.cropImage(originalPhoto, startX, startY, endX, endY);
                _editPhoto.setImageBitmap(newImage);
                Log.d("FP", "IMAGE CROPPED");
                setButtons(true);
                return true;
            }
        });
    }

    public void setButtons(boolean b) {
        _brightnessSeekBar.setEnabled(b);
        _contrastSeekBar.setEnabled(b);
        _originalThumbnail.setEnabled(b);
        _grayThumbnail.setEnabled(b);
        _warmThumbnail.setEnabled(b);
        _coolThumbnail.setEnabled(b);
        _editPhoto.setEnabled(b);
    }

    private Bitmap createThumbnail(Bitmap originalThumbnail, int thumbnailWidth) {

        Bitmap newThumbnail = Bitmap.createBitmap(thumbnailWidth, thumbnailWidth, Bitmap.Config.ARGB_8888);
        return newThumbnail;
    }

    private ImageView setThumbnailSize(ImageView v, int thumbnailWidth) {
        v.setMinimumWidth(thumbnailWidth);
        v.setMinimumHeight(thumbnailWidth);
        v.setMaxWidth(thumbnailWidth);
        v.setMaxHeight(thumbnailWidth);

        return v;
    }

    public Bitmap scaledImage(DisplayMetrics metrics, Uri mImageUri, int size, boolean isThumbnail) {
//        byte[] imageData = null;
        Bitmap image = null;
        Bitmap rotatedImage = null;
        try
        {
            final int IMAGE_SIZE = size;

            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            image = Bitmap.createScaledBitmap(image, IMAGE_SIZE, IMAGE_SIZE, false);

//            Bitmap rotatedImage = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotatedImage = Bitmap.createBitmap(image, 0, 0, IMAGE_SIZE, IMAGE_SIZE, matrix, true);


//            if (isThumbnail == true) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                image.compress(Bitmap.CompressFormat.PNG, 100, baos);
////            imageData = baos.toByteArray();
//            }
        }
        catch(Exception e) {
            Log.e("ERROR", "COULD NOT CREATE THUMBNAIL");
        }
        return rotatedImage;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //        ActivityCompat.finishAfterTransition(this);

        Intent myIntent = new Intent(getApplicationContext(), ActivityCamera.class);

        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        return;
    }


}

