package unimelb.edu.instamelb.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import unimelb.edu.instamelb.adapters.PhotoListAdapter;
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.extras.Util;
import unimelb.edu.instamelb.logging.L;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;
import unimelb.edu.instamelb.users.Comment;
import unimelb.edu.instamelb.users.Follows;
import unimelb.edu.instamelb.users.Photo;
import unimelb.edu.instamelb.users.User;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProfile extends Fragment implements SortListener{
    private static final String TAG = "ProfileFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String USERID = "userid";

    private String mUsername;
    private String mPassword;
    private String mUserid;
    private Intent mIntent;


    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param username Parameter 1.
     * @param password Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    public static FragmentProfile newInstance(String username, String password, String userid) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        args.putString(USERID, userid);
        fragment.setArguments(args);
        return fragment;
    }
    private Context mContext;
    private User mUser;
    private Photo mPhoto;
    private Follows mFollows;
    private View mProfileView;
    private Boolean mLoggedInUserFollows;
    private Button mChangeImageButton;
    private ProgressBar mLoadingPb;
    private GridView mGridView;



    private void configureFollowButton(Boolean loggedInUserFollows) {
        mChangeImageButton.setText(loggedInUserFollows ? R.string.action_unfollow
                : R.string.action_follow);
        mChangeImageButton.setOnClickListener(mFollowButtonListener);
    }

    private final View.OnClickListener mChangeImageButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }

    };

    private final OnClickListener mFollowButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mUser != null && mLoggedInUserFollows != null) {
                String action = mLoggedInUserFollows?"unfollow":"follow";
                String[] args={mUsername, mPassword,"action", action};
                new FollowAction().execute(args);
                    }
                };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUsername = getArguments().getString(USERNAME);
            mPassword = getArguments().getString(PASSWORD);
            mUserid =getArguments().getString(USERID);
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
    private class DownloadTask extends AsyncTask<String, Integer, List<String>> {
        ArrayList<Photo> userPhotoList=new ArrayList<>();
        @Override
        protected List doInBackground(String... strings) {
            List<String> result =new ArrayList<>();
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                params.add(new BasicNameValuePair(strings[2], strings[3]));
                APIRequest request = new APIRequest(strings[0],strings[1]);
                result.add(request.createRequest("GET", "/", params));
                params.add(new BasicNameValuePair("photos",""));
                result.add( request.createRequest("GET", "/", params));
                List<NameValuePair> follows = new ArrayList<NameValuePair>(1);
                follows.add(new BasicNameValuePair(strings[2],"self"));
                follows.add(new BasicNameValuePair("follows",""));
                result.add(request.createRequest("GET","/",follows));

            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(List<String>  result) {
            mUser = new User(result.get(0));
            try {
                JSONObject object = new JSONObject(result.get(1));
                if (object.has("photos")) {
                    JSONArray photoList = object.getJSONArray("photos");
                    int length = photoList.length();
                    for (int i = 0; i < length; i++) {
                        object = photoList.getJSONObject(i);
                        mPhoto=new Photo(object);
                        mPhoto.setUser_id(mUser.getmId());
                        mPhoto.setUsername(mUser.getmUserName());
                        mPhoto.setUser_avatar(mUser.getmProfileImageUrl());
                        new getLikeAndComments(mPhoto,mUsername,mPassword).execute();
                        userPhotoList.add(mPhoto);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFollows = new Follows(result.get(2));
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
            mLoadingPb = (ProgressBar) mProfileView
                    .findViewById(R.id.pb_loading);
            mGridView = (GridView) mProfileView
                    .findViewById(R.id.gridView);
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

                photoCount.setText(Util.getPrettyCount(mUser.getmPhotoCount()));
                followingCount
                        .setText(Util.getPrettyCount(mUser.getmFollowingCount()));
                followersCount.setText(Util.getPrettyCount(mUser
                        .getmFollowersCount()));
                if (mUserid == "self" || mUserid == String.valueOf(mUser.getmId())) {
                    mLoggedInUserFollows = null;
                    mChangeImageButton.setText("Change Avatar");
                    mChangeImageButton.setOnClickListener(mChangeImageButtonListener);
                } else {
                    mLoggedInUserFollows = mFollows.getmId().contains(Long.valueOf(mUserid).longValue()) ? true : false;
                    configureFollowButton(mLoggedInUserFollows);
                }

            } else {
                usernameTextView.setText(null);
                detailsLayout.setVisibility(View.GONE);
                mChangeImageButton.setVisibility(View.GONE);
            }
            mLoadingPb.setVisibility(View.GONE);
            DisplayMetrics dm = new DisplayMetrics();

            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            int width = (int) Math.ceil((double) dm.widthPixels / 2);
            width = width - 50;
            int height = width;

            PhotoListAdapter adapter = new PhotoListAdapter(mContext);

            adapter.setData(userPhotoList);
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

    public class FollowAction extends AsyncTask<String, Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                List<NameValuePair> params = new ArrayList<>(1);
                params.add(new BasicNameValuePair(strings[2], strings[3]));
                APIRequest request = new APIRequest(strings[0], strings[1]);
                String endpoint="/users/"+mUser.getmId()+"/relationship";
                result = request.createRequest("POST", endpoint, params);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject object = new JSONObject(result);
                if (object.getBoolean("modified")) {
                    mLoggedInUserFollows = mLoggedInUserFollows != null && !mLoggedInUserFollows;
                    configureFollowButton(mLoggedInUserFollows);
                    TextView followingCount = (TextView) mProfileView
                            .findViewById(R.id.followingCountLabel);
                    if (mLoggedInUserFollows){
                        followingCount.setText(Util.getPrettyCount(mUser.getmFollowingCount() + 1));
                        mUser.setmFollowingCount(mUser.getmFollowingCount()+1);
                    }else{
                        followingCount.setText(Util.getPrettyCount(mUser.getmFollowingCount() - 1));
                        mUser.setmFollowingCount(mUser.getmFollowingCount()-1);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class getLikeAndComments extends AsyncTask<String, Integer, List<String>> {
        private ArrayList<Comment> oneCommentList=new ArrayList<>();
        private ArrayList<String> likeList=new ArrayList<>();
        private Photo photo;
        private String username;
        private String password;
        public getLikeAndComments(Photo photo,String username,String password){
            this.photo=photo;
            this.username=username;
            this.password=password;
        }

        @Override
        protected List doInBackground(String... strings) {
            List<String> result = new ArrayList();
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                APIRequest request = new APIRequest(username,password);
                params.add(new BasicNameValuePair("photo",String.valueOf(photo.getPhoto_id())));
                params.add(new BasicNameValuePair("comments", ""));
                JSONObject object = new JSONObject(request.createRequest("GET", "/", params));

                if (object.has("comments")) {
                    JSONArray commentList = object.getJSONArray("comments");
                    int commlength = commentList.length();
                    for (int j = 0; j < commlength; j++) {
                        oneCommentList.add(new Comment((JSONObject) commentList.get(j)));
                    }
                }
                photo.setComment_list(oneCommentList);
                params=new ArrayList<>();
                params.add(new BasicNameValuePair("photo",String.valueOf(photo.getPhoto_id() )));
                params.add(new BasicNameValuePair("likes", ""));
                object = new JSONObject(request.createRequest("GET", "/", params));
                if (object.has("likes")) {
                    JSONArray likesList = object.getJSONArray("likes");
                    int likelength = likesList.length();
                    for (int j = 0; j < likelength; j++) {
                        JSONObject oneLike= (JSONObject)likesList.get(j);
                        likeList.add(oneLike.getString("username"));
                        if (username==oneLike.get("username")) photo.setUser_has_liked(true);
                    }
                }
                photo.setLiker_list(likeList);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<String> result) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext=container.getContext();
        mProfileView=inflater.inflate(R.layout.fragment_profile, container, false);
        String[] args={mUsername, mPassword,"users", mUserid};
        new DownloadTask().execute(args);
        Log.d("FP","FINISHDOWNLOAD");
        return mProfileView;
    }
}
