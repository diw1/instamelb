package unimelb.edu.instamelb.activities;

import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;


import java.io.File;
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
    private String mComment;
    private double longitude = 0;
    private double latitude = 0;

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
    @InjectView(R.id.edit_button)
    Button _editButton;
    @InjectView(R.id.caption_button)
    Button _captionButton;

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
//        mImageUri = ActivityCamera.getImageURI();
//        mImageFile = new File(mImageUri.getPath());

        // Retrieve Uri
        Intent intent = getIntent();
        String uriString = intent.getStringExtra(ActivityCamera.EXTRA_MESSAGE);
        mImageUri = Uri.parse(uriString);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mImageUri);
        } catch (IOException e) {
            Log.e("ERROR", "COULD NOT RETRIEVE BITMAP");
        }

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();

        bitmap = scaledImage(metrics, bitmap, imageWidth, imageHeight, false, previewWidth);
        imageThumbnail = scaledImage(metrics, bitmap, thumbnailWidth, thumbnailWidth, true, previewWidth);



        final ActivityEditPhoto photo = new ActivityEditPhoto(this);
        final ActivityEditPhoto photoThumbnail = new ActivityEditPhoto(this);

        // Create Thumbnails
        originalThumbnail = imageThumbnail;
        _originalThumbnail.setImageBitmap(originalThumbnail);
//        _originalThumbnail = setThumbnailSize(_originalThumbnail, thumbnailWidth);
//
//        grayThumbnail = imageThumbnail;
//        warmThumbnail = createThumbnail(originalThumbnail, thumbnailWidth);
//        coolThumbnail = createThumbnail(originalThumbnail, thumbnailWidth);
        Log.d("FP", "NEW THUMBNAILS CREATED");

        // Apply filters to thumbnails
        grayThumbnail = photoThumbnail.grayscaleFilter(imageThumbnail);
        warmThumbnail = photoThumbnail.sunsetFilter(originalThumbnail);
        coolThumbnail = photoThumbnail.desaturatedFilter(originalThumbnail);

        // Set thumbnail images to view
        _grayThumbnail.setImageBitmap(grayThumbnail);
        _warmThumbnail.setImageBitmap(warmThumbnail);
        _coolThumbnail.setImageBitmap(coolThumbnail);

        _editPhoto.setMinimumHeight(photoPreviewHeight);
        _editPhoto.setMaxHeight(photoPreviewHeight);
//        Drawable d = getResources().getDrawable(R.drawable.sonic);
//        _editPhoto.setImageDrawable(bitmap);

        _editPhoto.setImageBitmap(bitmap);
        originalPhoto = bitmap;
        editedPhoto = bitmap;
        newImage = bitmap;


        _brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                setButtons(false);
                newImage = photo.adjustBrightness(editedPhoto, progress-255);
                editedPhoto = newImage;
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
                editedPhoto = newImage;
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
                editedPhoto = originalPhoto;
                Log.d("FP", "SELECTED ORIGINAL PHOTO");
                setButtons(true);
            }
        });

        _grayThumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.grayscaleFilter(originalPhoto);
                editedPhoto = newImage;
                _editPhoto.setImageBitmap(newImage);

                Log.d("FP", "SELECTED GRAYSCALE PHOTO");
                setButtons(true);
            }
        });

        _warmThumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.sunsetFilter(originalPhoto);
                editedPhoto = newImage;
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
                _contrastSeekBar.setProgress(275);

                newImage = photo.desaturatedFilter(originalPhoto);
                editedPhoto = newImage;
                _editPhoto.setImageBitmap(newImage);

                Log.d("FP", "SELECTED DESATURATED PHOTO");
                setButtons(true);
            }
        });

        _uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                String s = convertToBase64(newImage);
                Log.d("FP", "IMAGE UPLOADED");
                Intent intent = new Intent(getBaseContext(), ActivityCamera.class);
                startActivity(intent);


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

    public Bitmap scaledImage(DisplayMetrics metrics, Bitmap bitmap, int width, int height, boolean isThumbnail, int previewWidth) {
//        byte[] imageData = null;
        Bitmap image = bitmap;
        Bitmap rotatedImage = null;
        try
        {
//            final int IMAGE_SIZE = size;
//            int imageWidth = width;
//            int imageHeight = height;

            if (isThumbnail == false) {
                if (width > height) {
                    height = (height * previewWidth) / width;
                    width = previewWidth;
                }
                else {
                    width = (width * previewWidth) / height;
                    height = previewWidth;
                }
            }

//            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            image = Bitmap.createScaledBitmap(image, width, height, false);

//            Bitmap rotatedImage = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888);

            if (isThumbnail == false) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                rotatedImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
                image = rotatedImage;
            }

//            if (isThumbnail == true) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                image.compress(Bitmap.CompressFormat.PNG, 100, baos);
////            imageData = baos.toByteArray();
//            }
        }
        catch(Exception e) {
            Log.e("ERROR", "COULD NOT CREATE THUMBNAIL");
        }
        return image;
    }

    private String convertToBase64(Bitmap image) {
        String b64Image = null;

        // Convert image to byte[]
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        image.copyPixelsToBuffer(buffer);
        byte[] b = buffer.array();

        b64Image = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("FP", "CONVERTED IMAGE TO BASE64 STRING");

        return b64Image;
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

