package unimelb.edu.instamelb.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import butterknife.ButterKnife;
import butterknife.InjectView;
import unimelb.edu.instamelb.fragments.FragmentCamera;
import unimelb.edu.instamelb.fragments.FragmentLibrary;
import unimelb.edu.instamelb.fragments.FragmentPhoto;
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
    ImageView _alteredPhoto;

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

    Bitmap originalPhoto = ((BitmapDrawable)_alteredPhoto.getDrawable()).getBitmap();
    Bitmap editedPhoto = ((BitmapDrawable)_alteredPhoto.getDrawable()).getBitmap();
    Bitmap newImage;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_photo);
        ButterKnife.inject(this);

//        Button _photoButton = (Button) findViewById(R.id.button_photo);
//        Button _libraryButton = (Button) findViewById(R.id.button_library);
//        final TextView _currentSelection = (TextView) findViewById(R.id.current_selection);

        final ActivityEditPhoto photo = new ActivityEditPhoto(this);

        _brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                newImage = photo.adjustBrightness(editedPhoto, progress-255);
                _alteredPhoto.setImageBitmap(newImage);
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
                _alteredPhoto.setImageBitmap(newImage);
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

                _alteredPhoto.setImageBitmap(originalPhoto);
                Log.d("FP", "SELECTED ORIGINAL PHOTO");
            }
        });

        _filter1Thumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                newImage = photo.grayscaleFilter(originalPhoto);
                _alteredPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED GRAYSCALE PHOTO");
            }
        });

        _filter2Thumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                newImage = photo.sunsetFilter(originalPhoto);
                _alteredPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED SUNSET PHOTO");
            }
        });

        _filter3Thumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                newImage = photo.desaturatedFilter(originalPhoto);
                _alteredPhoto.setImageBitmap(newImage);
                Log.d("FP", "SELECTED DESATURATED PHOTO");
            }
        });

        _uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                newImage = ((BitmapDrawable)_alteredPhoto.getDrawable()).getBitmap();

            }
        });



        // Crop photo
        _alteredPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = event.getX();
                    startY = event.getY();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP){

                    // Crop Photo Here
                    endX = event.getX();
                    endY = event.getY();
                }

                newImage = photo.cropImage(originalPhoto, startX, startY, endX, endY);
                _alteredPhoto.setImageBitmap(newImage);
                Log.d("FP", "IMAGE CROPPED");


                return true;
            }
        });




    }
}

