package unimelb.edu.instamelb.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import unimelb.edu.instamelb.adapters.PhotoListAdapter;
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.logging.L;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;
import unimelb.edu.instamelb.users.User;
import unimelb.edu.instamelb.users.UserPhotos;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment implements SortListener{
    private static final String TAG = "ProfileFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentProfile() {
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
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private Context mContext;
    private User mUser;
    private UserPhotos mUserPhotos;
    private View mProfileView;
    private Boolean mFollowsLoggedInUser;
    private Boolean mLoggedInUserFollows;
    private Button mFollowButton;
    private Button mChangeImageButton;
    private View mFollowDivider;
    private ProgressBar mLoadingPb;
    private GridView mGridView;

    /*
    * Given 1234567890, return "1,234,567,890"
    */
    public static String getPrettyCount(int count) {
        String regex = "(\\d)(?=(\\d{3})+$)";
        return Integer.toString(count).replaceAll(regex, "$1,");
    }

    private void configureFollowButtonVisibility(Boolean loggedInUserFollows) {

        if (loggedInUserFollows == null) {
            mFollowButton.setVisibility(View.GONE);
        } else {
            mFollowButton.setVisibility(View.VISIBLE);
            mFollowButton
                    .setText(loggedInUserFollows ? R.string.action_unfollow
                            : R.string.action_follow);
           // mFollowButton.setOnClickListener(mFollowButtonListener);
        }
    }

    private void configureChangeButtonVisibility(User mUser) {
        if (mUser.getmId()==1){
            mChangeImageButton.setVisibility(View.VISIBLE);
            mChangeImageButton.setOnClickListener(mChangeImageButtonListener);
        } else {
            mChangeImageButton.setVisibility(View.GONE);
        }
    }

    private final View.OnClickListener mChangeImageButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        }

    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
    public class DownloadTask extends AsyncTask<String, Integer, String[]> {
        ArrayList<String> photoList;
        @Override
        protected String[] doInBackground(String... strings) {
            String result[] = {"",""};
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                params.add(new BasicNameValuePair(strings[2], strings[3]));
                APIRequest request = new APIRequest(strings[0],strings[1]);
                result[0] = request.createRequest("GET", "/", params);
                params.add(new BasicNameValuePair("photos",""));
                result[1] = request.createRequest("GET", "/", params);
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String... result) {
            mUser = new User(result[0]);
            mUserPhotos = new UserPhotos(result[1]);
            photoList = mUserPhotos.getmPhotoList();

            Log.d(TAG, mUser.getmEmail());
            TextView usernameTextView = (TextView) mProfileView
                    .findViewById(R.id.username);
            TextView photoCount = (TextView) mProfileView
                    .findViewById(R.id.photoCountLabel);
            TextView emailTextView = (TextView) mProfileView
                    .findViewById(R.id.Email);
            TextView followingCount = (TextView) mProfileView
                    .findViewById(R.id.followingCountLabel);
            TextView followersCount = (TextView) mProfileView
                    .findViewById(R.id.followersCountLabel);
            LinearLayout detailsLayout = (LinearLayout) mProfileView
                    .findViewById(R.id.detailsLayout);
            ProgressBar mLoadingPb = (ProgressBar) mProfileView
                    .findViewById(R.id.pb_loading);
            GridView mGridView = (GridView) mProfileView
                    .findViewById(R.id.gridView);
            mFollowButton = (Button) mProfileView
                    .findViewById(R.id.friendship_button);
            mChangeImageButton = (Button) mProfileView.
                    findViewById(R.id.change_button);

            if (mUser != null) {
                ImageView avatar = (ImageView) mProfileView
                        .findViewById(R.id.profileImage);
                //TODO
                String imageUrl = mUser.getmProfileImageUrl();
                UrlImageViewHelper.setUrlDrawable(avatar, imageUrl,
                        R.drawable.ic_contact_picture);
                // avatar.setImageURL(imageUrl);
                //TODO
                usernameTextView.setText(mUser.getmUserName());
                emailTextView.setText(mUser.getmEmail());
                detailsLayout.setVisibility(View.VISIBLE);

                photoCount.setText(getPrettyCount(mUser.getmPhotoCount()));
                followingCount
                        .setText(getPrettyCount(mUser.getmFollowingCount()));
                followersCount.setText(getPrettyCount(mUser
                        .getmFollowersCount()));

                configureFollowButtonVisibility(mLoggedInUserFollows);
                configureChangeButtonVisibility(mUser);

            } else {
                usernameTextView.setText(null);
                detailsLayout.setVisibility(View.GONE);
                mFollowButton.setVisibility(View.GONE);
                mFollowDivider.setVisibility(View.GONE);
            }
            mLoadingPb.setVisibility(View.GONE);


            DisplayMetrics dm = new DisplayMetrics();

            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            int width = (int) Math.ceil((double) dm.widthPixels / 2);
            width = width - 50;
            int height = width;

            PhotoListAdapter adapter = new PhotoListAdapter(mContext);

            adapter.setData(photoList);
            adapter.setLayoutParam(width, height);

            mGridView.setAdapter(adapter);
        }
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

    public void onClickPhoto(View v){
    //TODO
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=container.getContext();
        mProfileView=inflater.inflate(R.layout.fragment_profile, container, false);
        String[] args={"di","123456","users","1"};
        new DownloadTask().execute(args);
        return mProfileView;
    }
}
