package unimelb.edu.instamelb.activities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;


import java.io.File;

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

    @InjectView(R.id.brightnessSeekBar)
    SeekBar _brightnessSeekBar;
    @InjectView(R.id.contrastSeekBar)
    SeekBar _contrastSeekBar;
    @InjectView(R.id.photoPreview)
    ImageView _editPhoto;

    @InjectView(R.id.upload_button)
    Button _uploadButton;

    @InjectView(R.id.photoThumbnail)
    ImageView _originalThumbnail;
    @InjectView(R.id.thumbnailFilter1)
    ImageView _filter1Thumbnail;
    @InjectView(R.id.thumbnailFilter2)
    ImageView _filter2Thumbnail;
    @InjectView(R.id.thumbnailFilter3)
    ImageView _filter3Thumbnail;

    Bitmap originalPhoto;
    Bitmap editedPhoto;
    Bitmap newImage;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_photo);
        ButterKnife.inject(this);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        final int previewWidth = metrics.widthPixels;
        int photoPreviewHeight = previewWidth;


//        Button _photoButton = (Button) findViewById(R.id.button_photo);
//        Button _libraryButton = (Button) findViewById(R.id.button_library);
//        final TextView _currentSelection = (TextView) findViewById(R.id.current_selection);

        final ActivityEditPhoto photo = new ActivityEditPhoto(this);

        _editPhoto.setMinimumHeight(photoPreviewHeight);
        _editPhoto.setMaxHeight(photoPreviewHeight);
        Drawable d = getResources().getDrawable(R.drawable.sonic);
        _editPhoto.setImageDrawable(d);

        originalPhoto = ((BitmapDrawable) _editPhoto.getDrawable()).getBitmap();
        editedPhoto = ((BitmapDrawable) _editPhoto.getDrawable()).getBitmap();


        _brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                newImage = photo.adjustBrightness(editedPhoto, progress-255);
                _editPhoto.setImageBitmap(newImage);
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
                newImage = photo.adjustContrast(editedPhoto, progress - 255);
                _editPhoto.setImageBitmap(newImage);
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

                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);
                _editPhoto.setImageBitmap(originalPhoto);
                Log.d("FP", "SELECTED ORIGINAL PHOTO");
            }
        });

        _filter1Thumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.grayscaleFilter(originalPhoto);
                _editPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED GRAYSCALE PHOTO");
            }
        });

        _filter2Thumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.sunsetFilter(originalPhoto);
                _editPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED SUNSET PHOTO");
            }
        });

        _filter3Thumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                _brightnessSeekBar.setProgress(255);
                _contrastSeekBar.setProgress(255);

                newImage = photo.desaturatedFilter(originalPhoto);
                _editPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED DESATURATED PHOTO");
            }
        });

        _uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                newImage = ((BitmapDrawable) _editPhoto.getDrawable()).getBitmap();

            }
        });



        // Crop photo
        _editPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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


                return true;
            }
        });




    }
}

