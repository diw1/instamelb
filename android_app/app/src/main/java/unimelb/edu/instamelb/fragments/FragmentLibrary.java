package unimelb.edu.instamelb.fragments;

/**
 * Created by bboyce on 12/09/15.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import unimelb.edu.instamelb.extras.SortListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import unimelb.edu.instamelb.activities.ActivityCamera;
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.materialtest.R;

/**
 * Created by bboyce on 12/09/15.
 */
public class FragmentLibrary extends Fragment implements SortListener, View.OnClickListener {

    static final int TAKE_PHOTO = 11111;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView mImageView;
    private ImageView mThumbnailImageView;


    public FragmentLibrary() {
        super();
    }

    public static FragmentLibrary newInstance(String param1, String param2) {
        FragmentLibrary fragment = new FragmentLibrary();
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
        View view = null;
        view = inflater.inflate(R.layout.fragment_camera, container, false);

//        mImageView = (ImageView) view.findViewById(R.id.imageViewFullSized);
//        Button takePhotoButton = (Button) view.findViewById(R.id.button_takePhoto);
//        Button flashButton = (Button) view.findViewById(R.id.button_flash);
//        Button gridlinesButton = (Button) view.findViewById(R.id.button_gridlines);
//
//        takePhotoButton.setOnClickListener(this);
//        flashButton.setOnClickListener(this);
//        gridlinesButton.setOnClickListener(this);
//
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSortByName() {

    }

    @Override
    public void onSortByDate() {

    }

    @Override
    public void onSortByRating() {

    }
}