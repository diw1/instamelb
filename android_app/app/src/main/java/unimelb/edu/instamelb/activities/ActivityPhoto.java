package unimelb.edu.instamelb.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import unimelb.edu.instamelb.materialtest.R;

/**
 * Created by bboyce on 12/09/15.
 */
public class ActivityPhoto extends AppCompatActivity {


    private static final int REQUEST_PHOTO = 0;
    final int PIC_CROP = 2;
    private ViewPager mViewPager;
    int seekBarValue;
    private Uri mCapturedImageURI = null;

    // Variables for cropping
    private float startX;
    private float startY;
    private float endX;
    private float endY;

    final int THUMBSIZE = 64;
    public Bitmap originalPhoto, editedPhoto, newImage, originalOrCroppedImage;
    public Bitmap originalThumbnail, grayThumbnail, warmThumbnail, coolThumbnail, imageThumbnail;
    private String mComment;
    private double longitude = 0;
    private double latitude = 0;

    public Uri mImageUri;
    public File mImageFile;
    public static Uri newImageUri;
    public File newImageFile;
    public String imagePath;
    public Bitmap bitmap = null;

    @InjectView(R.id.brightnessSeekBar)
    SeekBar _brightnessSeekBar;
    @InjectView(R.id.contrastSeekBar)
    SeekBar _contrastSeekBar;
    @InjectView(R.id.photoPreview)
    ImageView _editPhoto;
    @InjectView(R.id.caption_button)
    Button _captionButton;
    @InjectView(R.id.reset_Button)
    Button _resetButton;

    @InjectView(R.id.brightnessLayout)
    LinearLayout _brightnessLayout;
    @InjectView(R.id.contrastLayout)
    LinearLayout _contrastLayout;
    @InjectView(R.id.filterLayout)
    LinearLayout _filterLayout;
    @InjectView(R.id.cropLayout)
    LinearLayout _cropLayout;
    @InjectView(R.id.selectEditTypeLayout)
    LinearLayout _selectLayout;

    @InjectView(R.id.brightnessButton)
    ImageButton _brightnessButton;
    @InjectView(R.id.contrastButton)
    ImageButton _contrastButton;
    @InjectView(R.id.filterButton)
    ImageButton _filterButton;
    @InjectView(R.id.cropButton)
    ImageButton _cropButton;


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

        bitmap = getImageFromLibrary(mImageUri);



//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
//        } catch (IOException e) {
//            Log.e("ERROR", "COULD NOT RETRIEVE BITMAP");
//        }

        if (bitmap == null) {
            Log.e("ERROR", "BITMAP IS NULL");
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
        originalOrCroppedImage = bitmap;

        android.view.ViewGroup.LayoutParams layoutParams = _selectLayout.getLayoutParams();
        layoutParams.height = previewWidth / 4;
        _selectLayout.setLayoutParams(layoutParams);

        setLayoutHeight(_selectLayout, previewWidth);
        setLayoutHeight(_brightnessLayout, previewWidth);
        setLayoutHeight(_contrastLayout, previewWidth);
        setLayoutHeight(_filterLayout, previewWidth);


        _brightnessLayout.setVisibility(View.VISIBLE);
        _contrastLayout.setVisibility(View.GONE);
        _filterLayout.setVisibility(View.GONE);
        _cropLayout.setVisibility(View.GONE);


        _resetButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtons(false);
                _brightnessSeekBar.setProgress(130);
                _contrastSeekBar.setProgress(100);
                _editPhoto.setImageBitmap(originalPhoto);
                editedPhoto = originalPhoto;
                Log.d("FP", "SELECTED ORIGINAL PHOTO");
                setButtons(true);
                _brightnessLayout.setVisibility(View.GONE);
                _contrastLayout.setVisibility(View.GONE);
            }
        });

        _brightnessButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                _brightnessLayout.setVisibility(View.VISIBLE);
                _contrastLayout.setVisibility(View.GONE);
                _filterLayout.setVisibility(View.GONE);
                _cropLayout.setVisibility(View.GONE);
            }
        });

        _contrastButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                _contrastLayout.setVisibility(View.VISIBLE);
                _brightnessLayout.setVisibility(View.GONE);
                _contrastLayout.setVisibility(View.VISIBLE);
                _filterLayout.setVisibility(View.GONE);
                _cropLayout.setVisibility(View.GONE);
            }
        });

        _filterButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                _filterLayout.setVisibility(View.VISIBLE);
                _brightnessLayout.setVisibility(View.GONE);
                _contrastLayout.setVisibility(View.GONE);
                _filterLayout.setVisibility(View.VISIBLE);
                _cropLayout.setVisibility(View.GONE);
            }
        });

        _cropButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                _cropLayout.setVisibility(View.VISIBLE);
                _brightnessLayout.setVisibility(View.GONE);
                _contrastLayout.setVisibility(View.GONE);
                _filterLayout.setVisibility(View.GONE);
            }
        });


        _brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                setButtons(false);
                Log.d("FP", "Brightness value: " + progress);
                newImage = photo.adjustBrightness(editedPhoto, progress - 130);
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
                newImage = photo.adjustContrast(editedPhoto, progress - 100);
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
                _brightnessSeekBar.setProgress(130);
                _contrastSeekBar.setProgress(100);
                _editPhoto.setImageBitmap(originalOrCroppedImage);
                editedPhoto = originalOrCroppedImage;
                Log.d("FP", "SELECTED ORIGINAL PHOTO");
                setButtons(true);
                _brightnessLayout.setVisibility(View.GONE);
                _contrastLayout.setVisibility(View.GONE);
            }
        });

        _grayThumbnail.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);
                _brightnessSeekBar.setProgress(130);
                _contrastSeekBar.setProgress(100);

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
                _brightnessSeekBar.setProgress(130);
                _contrastSeekBar.setProgress(100);

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
                _brightnessSeekBar.setProgress(130);
                _contrastSeekBar.setProgress(100);

                newImage = photo.desaturatedFilter(originalPhoto);
                editedPhoto = newImage;
                _editPhoto.setImageBitmap(newImage);

                Log.d("FP", "SELECTED DESATURATED PHOTO");
                setButtons(true);
            }
        });

        _cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Uri cropImageUri = saveImageToLibrary(newImage);
                performCrop(cropImageUri);
                newImage = getImageFromLibrary(cropImageUri);
                originalOrCroppedImage = newImage;
                editedPhoto = newImage;
            }
        });

        _uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                setButtons(false);

                // Compress image & save to library
                mImageUri = saveImageToLibrary(newImage);

                // Get Uri of saved image, convert to bitmap, convert to byte[]
                newImage = getImageFromLibrary(mImageUri);
                String s = convertToBase64(newImage);

                Intent intent = new Intent(getBaseContext(), ActivityCamera.class);
                startActivity(intent);

                setButtons(true);

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

    private void setLayoutHeight(LinearLayout layout, int previewWidth) {

        android.view.ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        layoutParams.height = previewWidth / 4;
        layout.setLayoutParams(layoutParams);
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

        Bitmap image = bitmap;
        Bitmap rotatedImage = null;
        try {
            if (isThumbnail == false) {
                if (width > height) {
                    height = (height * previewWidth) / width;
                    width = previewWidth;
                } else {
                    width = (width * previewWidth) / height;
                    height = previewWidth;
                }
            }

            image = Bitmap.createScaledBitmap(image, width, height, false);

            if (isThumbnail == false) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                rotatedImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
                image = rotatedImage;
            }
        } catch (Exception e) {
            Log.e("ERROR", "COULD NOT CREATE THUMBNAIL");
        }
        return image;
    }

    private void performCrop(Uri imageUri){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(imageUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException e){
            Log.e("ERROR", "DEVICE DOES NOT SUPPORT CROP");
        }
    }

    private byte[] convertImageToByteArray(Bitmap image) {
        // Convert image to byte[]
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        image.copyPixelsToBuffer(buffer);
        byte[] b = buffer.array();
        Log.d("BYTE COUNT", "Byte count is: " + bytes*8);
        return b;
    }

//    private void compressImage(Bitmap image) {
//        Bitmap compressedImage = null;
//
//        FileOutputStream out = null;
//        try {
//            out = new FileOutputStream(filename);
//            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//
//
////        return compressedImage;
//    }


    private String convertToBase64(Bitmap image) {
        String b64Image = null;

        byte[] b = convertImageToByteArray(image);

        b64Image = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("FP", "CONVERTED IMAGE TO BASE64 STRING");

        return b64Image;
    }

    public Bitmap getImageFromLibrary(Uri uri) {
        Bitmap newBitmap = null;
        try {
            newBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            Log.d("FP", "BITMAP RETURNED FROM URI");
        } catch (IOException e) {
            Log.e("ERROR", "COULD NOT RETRIEVE BITMAP");
        }



        return newBitmap;
    }

    public Bitmap convertByteToBitmap(byte[] bytes) {
        Bitmap image = null;

        int byteCount = bytes.length;
        image = BitmapFactory.decodeByteArray(bytes, 0, byteCount);

        return image;
    }



    private Uri saveImageToLibrary(Bitmap image) {

            Log.d("FP", "LAUNCHED onPictureTaken()");
            newImageFile = getOutputMediaFile();
            newImageUri = Uri.fromFile(getOutputMediaFile());
            imagePath = newImageFile.getAbsolutePath();
            String uriString = newImageUri.toString();

            Log.d("FP", "GOT OUTPUT FILE: " + uriString);

            if (newImageFile == null) {
                Log.e("FP", "ERROR CREATING FILE");
                return newImageUri;
            }

            try {
                FileOutputStream fos = new FileOutputStream(newImageFile);
                image.compress(Bitmap.CompressFormat.PNG, 50, fos);
//                fos.write(byteArray);
                fos.close();
                Log.d("FP", "FILE OUTPUT STREAM CREATED");
            }
            catch (FileNotFoundException e) {
                Log.e("FP", "FILE NOT FOUND");
            }
            catch (IOException e) {
                Log.e("FP", "ERROR ACCESSING FILE");
            }

        return newImageUri;

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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        //        ActivityCompat.finishAfterTransition(this);
//
//        Intent myIntent = new Intent(getApplicationContext(), ActivityCamera.class);
//
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(myIntent);
//        finish();
//        return;
        super.onBackPressed();
//        ActivityCompat.finishAfterTransition(this);

        Intent myIntent = new Intent(getApplicationContext(), ActivityMain.class);

        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
        return;


    }


}

