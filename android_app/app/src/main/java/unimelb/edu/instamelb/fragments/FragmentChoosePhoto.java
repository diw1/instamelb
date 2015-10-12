package unimelb.edu.instamelb.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.InjectView;
import unimelb.edu.instamelb.activities.ActivityCamera;
import unimelb.edu.instamelb.activities.ActivityLibrary;
import unimelb.edu.instamelb.activities.ActivityMain;
import unimelb.edu.instamelb.activities.ActivityPhoto;
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.materialtest.R;

/**
 * Created by bboyce on 12/09/15.
 */
public class FragmentChoosePhoto extends Fragment implements View.OnClickListener {

    static final int TAKE_PHOTO = 1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final int PICK_IMAGE_REQUEST = 1;

    private ImageView mImageView;
    private ImageView mThumbnailImageView;
    private ImageView mSelectedImage;


    private Context mContext;
    private View mChooserView;
    private Button mButton;
    private ProgressBar mLoadingPb;
    private GridView mGridView;

    @InjectView(R.id.button_take_new_photo)
    Button _openCamera;
    @InjectView(R.id.button_choose_from_library)
    Button _openLibrary;




    public FragmentChoosePhoto() {
        super();
    }

    public static FragmentChoosePhoto newInstance(String param1, String param2) {
        FragmentChoosePhoto fragment = new FragmentChoosePhoto();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mChooserView=inflater.inflate(R.layout.fragment_choose_photo, container, false);
        mContext=container.getContext();
        Log.d("FP", "CREATED PHOTO CHOOSER VIEW");

        mChooserView.findViewById(R.id.button_take_new_photo).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), ActivityCamera.class);
                startActivity(intent);

            }
        });

        mChooserView.findViewById(R.id.button_choose_from_library).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent selectImage = new Intent();
                selectImage.setType("image/*");
                selectImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(selectImage, "Select Picture"), PICK_IMAGE_REQUEST);
                Log.d("FP", "SELECTED LIBRARY");



            }
        });
        return mChooserView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST  && resultCode == Activity.RESULT_OK) {
            ActivityMain activity = (ActivityMain)getActivity();
            Bitmap bitmap = getBitmapFromCameraData(data, activity);
            mSelectedImage.setImageBitmap(bitmap);
        }
    }

    private void setFullImageFromFilePath(String imagePath) {
        // Get the dimensions of the View
        int targetW = mSelectedImage.getWidth();
        int targetH = mSelectedImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        mSelectedImage.setImageBitmap(bitmap);
    }


    public static Bitmap getBitmapFromCameraData(Intent data, Context context){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }


//        _openCamera.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), ActivityCamera.class);
//                startActivity(intent);
//
//            }
//        });

//        _openLibrary.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getActivity(), ActivityLibrary.class);
//                startActivity(intent);
//
//            }
//        });




    @Override
    public void onClick(View v) {

    }




}

//    @Override
//    public void onClick(View v) {
//
//    }
//
//    @Override
//    public void onSortByName() {
//
//    }
//
//    @Override
//    public void onSortByDate() {
//
//    }
//
//    @Override
//    public void onSortByRating() {
//
//    }


