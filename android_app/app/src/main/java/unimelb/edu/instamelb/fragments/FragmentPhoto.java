package unimelb.edu.instamelb.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.InjectView;
import unimelb.edu.instamelb.activities.ActivityCamera;
import unimelb.edu.instamelb.activities.ActivityPhoto;
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.logging.L;
import unimelb.edu.instamelb.materialtest.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPhoto extends Fragment implements SortListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private View mPhotoView;


//    @InjectView(R.id.current_selection)
//    TextView _currentSelection;
//    @InjectView(R.id.button_library)
//    Button _libraryButton;
//    @InjectView(R.id.button_photo)
    Button _photoButton;
//    Button _photoButton = (Button) findViewById(R.id.button_photo);
//    @InjectView(R.id.button_video)

    public FragmentPhoto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPhoto newInstance(String param1, String param2) {
        FragmentPhoto fragment = new FragmentPhoto();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Button _photoButton = (Button) findViewById(R.id.button_photo);
//        Button _libraryButton = (Button) findViewById(R.id.button_library);
//        final TextView _currentSelection = (TextView) findViewById(R.id.current_selection);


    }


    public void onSortByName(){
        L.t(getActivity(), "sort name search");
    }

    @Override
    public void onSortByDate() {

    }

    @Override
    public void onSortByRating() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext=container.getContext();
        mPhotoView=inflater.inflate(R.layout.fragment_photo, container, false);
        Intent intent = new Intent(getActivity(), ActivityPhoto.class);
        startActivity(intent);
        Log.d("FP", "CREATED PHOTO VIEW");
        return mPhotoView;


    }
}
