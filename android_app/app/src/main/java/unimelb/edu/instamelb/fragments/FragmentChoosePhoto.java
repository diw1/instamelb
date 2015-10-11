package unimelb.edu.instamelb.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.materialtest.R;

/**
 * Created by bboyce on 12/09/15.
 */
public class FragmentChoosePhoto extends Fragment implements View.OnClickListener {

    static final int TAKE_PHOTO = 1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView mImageView;
    private ImageView mThumbnailImageView;


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

//                Intent intent = new Intent(getActivity(), ActivityLibrary.class);
//                startActivity(intent);

            }
        });



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


        return mChooserView;



    }

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


