package unimelb.edu.instamelb.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import unimelb.edu.instamelb.adapters.PhotoListAdapter;
import unimelb.edu.instamelb.extras.Util;
import unimelb.edu.instamelb.fragments.FragmentProfile;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;
import unimelb.edu.instamelb.users.Follows;
import unimelb.edu.instamelb.users.Photo;
import unimelb.edu.instamelb.users.User;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

public class ActivityProfile extends AppCompatActivity {

    private Bundle mBundle;
    private User mUser;
    private Photo mPhoto;
    private Follows mFollows;
    private Boolean mLoggedInUserFollows;
    private Button mChangeImageButton;
    private ProgressBar mLoadingPb;
    private GridView mGridView;
    private String mUsername;
    private String mPassword;
    private String mUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBundle=getIntent().getExtras();
        mUsername=mBundle.getString("username");
        mPassword=mBundle.getString("password");
        mUserid=mBundle.getString("userid");

        String[] args={mUsername, mPassword,"users", mUserid};
        new DownloadTask().execute(args);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                ActivityCompat.finishAfterTransition(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureFollowButton(Boolean loggedInUserFollows) {
        mChangeImageButton.setText(loggedInUserFollows ? R.string.action_unfollow
                : R.string.action_follow);
        mChangeImageButton.setOnClickListener(mFollowButtonListener);
    }

    private final View.OnClickListener mChangeImageButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
//TODO
        }

    };

    private final View.OnClickListener mFollowButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mUser != null && mLoggedInUserFollows != null) {
                String action = mLoggedInUserFollows?"unfollow":"follow";
                String[] args={mUsername, mPassword,"action", action};
                new FollowAction().execute(args);
            }
        };
    };

    private class DownloadTask extends AsyncTask<String, Integer, List<String>> {
        private ArrayList<Photo> userPhotoList=new ArrayList<>();
        @Override
        protected List doInBackground(String... strings) {
            List<String> result =new ArrayList();
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
                        new FragmentProfile.getLikeAndComments(mPhoto,mUsername,mPassword).execute();
                        userPhotoList.add(mPhoto);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFollows=new Follows(result.get(2));
            TextView usernameTextView = (TextView) findViewById(R.id.username);

            ImageView avatar = (ImageView) findViewById(R.id.profileImage);
            String imageUrl = mUser.getmProfileImageUrl();
            UrlImageViewHelper.setUrlDrawable(avatar, imageUrl,
                    R.drawable.ic_contact_picture);
            usernameTextView.setText(mUser.getmUserName());
            TextView emailTextView = (TextView) findViewById(R.id.Email);
            emailTextView.setText(mUser.getmEmail());

            TextView photoCount = (TextView) findViewById(R.id.photoCountLabel);
            TextView followingCount = (TextView) findViewById(R.id.followingCountLabel);
            TextView followersCount = (TextView) findViewById(R.id.followersCountLabel);
            LinearLayout detailsLayout = (LinearLayout) findViewById(R.id.detailsLayout);
            mLoadingPb = (ProgressBar) findViewById(R.id.pb_loading);
            mGridView = (GridView) findViewById(R.id.gridView);
            mChangeImageButton = (Button) findViewById(R.id.change_button);

            if (mUser != null) {

                detailsLayout.setVisibility(View.VISIBLE);

                photoCount.setText(Util.getPrettyCount(mUser.getmPhotoCount()));
                followingCount
                        .setText(Util.getPrettyCount(mUser.getmFollowingCount()));
                followersCount.setText(Util.getPrettyCount(mUser
                        .getmFollowersCount()));
                if (mUserid=="self"||mUserid==String.valueOf(mUser.getmId())){
                    mLoggedInUserFollows=null;
                    mChangeImageButton.setText("Change Avatar");
                    mChangeImageButton.setOnClickListener(mChangeImageButtonListener);
                }else {
                    mLoggedInUserFollows= mFollows.getmId().contains(Long.valueOf(mUserid).longValue()) ? true : false;
                    configureFollowButton(mLoggedInUserFollows);
                }

            } else {
                detailsLayout.setVisibility(View.GONE);
                mChangeImageButton.setVisibility(View.GONE);
            }
            mLoadingPb.setVisibility(View.GONE);
            DisplayMetrics dm = new DisplayMetrics();

           getWindowManager().getDefaultDisplay().getMetrics(dm);

            int width = (int) Math.ceil((double) dm.widthPixels / 2);
            width = width - 50;
            int height = width;

            PhotoListAdapter adapter = new PhotoListAdapter(ActivityProfile.this);

            adapter.setData(userPhotoList);
            adapter.setLayoutParam(width, height);
            mGridView.setAdapter(adapter);
        }
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
                    TextView followingCount = (TextView) findViewById(R.id.followingCountLabel);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);


    }
    public void onClickPhoto(View v){
        //TODO
    }
}
