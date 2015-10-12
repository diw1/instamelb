package unimelb.edu.instamelb.activities;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import unimelb.edu.instamelb.adapters.CommentsAdapter;
import unimelb.edu.instamelb.extras.Util;
import unimelb.edu.instamelb.fragments.FragmentHome;
import unimelb.edu.instamelb.fragments.FragmentProfile;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;
import unimelb.edu.instamelb.users.Comment;
import unimelb.edu.instamelb.users.Photo;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

public class ActivityDetail extends AppCompatActivity implements View.OnClickListener {

    private Bundle mBundle;
    private Photo mPhoto;
    private String mUsername;
    private String mPassword;
    private String mUserid;
    private ArrayList<Comment> commentList;

    public static CommentsAdapter commentsAdapter;
    public static ScrollView scrollView;
    public static LinearLayout userContainer, detailComments, likes,icons;
    public static RelativeLayout userProfile;
    public static Button sendComment;
    public static EditText message;
    public static ImageView postImage,detailUserPic, likeIcon,commentIcon,wifiIcon;
    public static TextView detailUsername, detailDate, detailCaption,likeUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBundle=getIntent().getExtras();
        mUsername=mBundle.getString("username");
        mPassword=mBundle.getString("password");
        mPhoto=(Photo)mBundle.get("photo");
        mUserid=String.valueOf(mPhoto.getUser_id());

        postImage=(ImageView)findViewById(R.id.post_image);
        userProfile = (RelativeLayout) findViewById(R.id.user_profile);
        detailComments = (LinearLayout) findViewById(R.id.detail_comments);
        likes = (LinearLayout) findViewById(R.id.likes);
        userContainer = (LinearLayout) findViewById(R.id.user_container);
        icons = (LinearLayout) findViewById(R.id.icons);

        sendComment = (Button) findViewById(R.id.send_comment);
        message = (EditText) findViewById(R.id.message);

        likeUsers = (TextView) findViewById(R.id.like_user);
        detailUsername = (TextView) findViewById(R.id.detail_username);
        detailDate = (TextView) findViewById(R.id.detail_date);
        detailCaption = (TextView) findViewById(R.id.detail_caption);

        likeIcon = (ImageView) findViewById(R.id.like_icon);
        commentIcon = (ImageView) findViewById(R.id.comment_icon);
        wifiIcon=(ImageView) findViewById(R.id.wifi_icon);
        detailUserPic = (ImageView) findViewById(R.id.detail_user_pic);

        UrlImageViewHelper.setUrlDrawable(postImage, mPhoto.getPhoto_image(), R.drawable.ic_contact_picture);
        UrlImageViewHelper.setUrlDrawable(detailUserPic, mPhoto.getUser_avatar(), R.drawable.ic_contact_picture);
        detailUsername.setText(mPhoto.getUsername() + ", ");
        detailDate.setText(Util.getDateTime(mPhoto.getTimestamp()));
        detailCaption.setBackgroundColor(Color.argb(20, 0, 0, 0));
        if (mPhoto.isUser_has_liked()){
            likeIcon.setImageDrawable(likeIcon.getContext().getResources().getDrawable(R.drawable.like));
        }else{
            likeIcon.setImageDrawable(likeIcon.getContext().getResources().getDrawable(R.drawable.unlike));
        }

        if(mPhoto.getPhoto_caption() != null){
            detailCaption.setText(mPhoto.getPhoto_caption());
        }
        else {
            detailCaption.setVisibility(View.GONE);
        }

        likeUsers.setText(getLikeList());
        commentsAdapter = new CommentsAdapter(this);
        commentList=mPhoto.getComment_list();
        commentsAdapter.setData(commentList);
        for (int i = 0; i < commentsAdapter.getCount(); i++) {

            View item = commentsAdapter.getView(i, null, detailComments);

            detailComments.addView(item);
        }
        detailUserPic.setOnClickListener(this);
        detailUsername.setOnClickListener(this);
        likeIcon.setOnClickListener(this);
        commentIcon.setOnClickListener(this);
        sendComment.setOnClickListener(this);
        if (checkNetworkInfo()){
            wifiIcon.setOnClickListener(this);
        }else{
            wifiIcon.setVisibility(View.GONE);
        }


    }

    public String getLikeList(){
        String likeList="❤";
        ArrayList<String> currentList;
        if (mPhoto.getLiker_list().isEmpty()){
            currentList=new ArrayList<>();
        }else{
            currentList=mPhoto.getLiker_list();
        }

        if (currentList.contains(mUsername)){
            currentList.remove(mUsername);
            currentList.add(0,(mUsername));
        }
        int length=mPhoto.getLiker_list().size();
        if (length>10){
            likeList+=length+" people like this";
        }else {
            for (int i = 0; i < length; i++) {
                if (length-i==1){
                    likeList += currentList.get(i)+" like this";
                }else{
                    likeList += currentList.get(i) + ", ";
                }
            }
        }
        return likeList;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.like_icon:
                likeIcon.setEnabled(false);
                String[] args={mUsername,mPassword};
                new LikeAction(mPhoto,mPhoto.isUser_has_liked()).execute(args);
                break;
            case R.id.comment_icon:
                findViewById(R.id.message).requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(message, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.wifi_icon:
                // TODO: 2015/10/12 Add wifi swipe function here.
                break;
            case R.id.send_comment:
                sendComment.setEnabled(false);
                String[] commentargs={mUsername,mPassword,"text",message.getText().toString()};
                new PostComment(mPhoto).execute(commentargs);
                break;

        }
    }
    @Override
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
    public class LikeAction extends AsyncTask<String, Void, Photo> {
        private Photo post;
        private boolean like;
        public LikeAction(Photo p, boolean like){
            this.like = like;
            this.post=p;
        }
        @Override
        protected Photo doInBackground(String... strings) {
            String endpoint="/photo/"+post.getPhoto_id()+"/likes";
            if(!like){
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                    APIRequest request = new APIRequest(strings[0], strings[1]);

                    JSONObject object = new JSONObject(request.createRequest("POST", endpoint, params));
                    if (object.getBoolean("liked")) {
                        if( post.getLiker_list() == null ) post.setLiker_list(new ArrayList<String>());
                        post.getLiker_list().add(FragmentHome.mUsername);
                        post.setLike_count(post.getLike_count() + 1);
                        post.setUser_has_liked(true);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                return post;
            }
            else {
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                    APIRequest request = new APIRequest(strings[0], strings[1]);
                    JSONObject object = new JSONObject(request.createRequest("DELETE", endpoint, params));
                    if (object.getBoolean("deleted")) {
                        post.getLiker_list().remove(FragmentHome.mUsername);
                        post.setLike_count(post.getLike_count() - 1);
                        post.setUser_has_liked(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return post;
            }
        }
        @Override
        protected void onPostExecute(Photo post) {
            likeUsers.setText(getLikeList());
            if (mPhoto.isUser_has_liked()){
                likeIcon.setImageDrawable(likeIcon.getContext().getResources().getDrawable(R.drawable.like));
            }else{
                likeIcon.setImageDrawable(likeIcon.getContext().getResources().getDrawable(R.drawable.unlike));
            }
            likeIcon.setEnabled(true);
        }
    }
    public class PostComment extends AsyncTask<String, Void, JSONObject> {
        private Photo post;
        public PostComment(Photo p){
            this.post=p;
        }
        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject object=new JSONObject();
            String endpoint="/photo/"+post.getPhoto_id()+"/comments";
                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(1);
                    APIRequest request = new APIRequest(strings[0], strings[1]);
                    params.add(new BasicNameValuePair(strings[2], strings[3]));
                    object = new JSONObject(request.createRequest("POST", endpoint, params));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                return object;
            }

        @Override
        protected void onPostExecute(JSONObject object) {
            try {
                if (object.getBoolean("posted")) {
                    new FragmentProfile.getLikeAndComments(mPhoto,mUsername,mPassword).execute();
                    Toast.makeText(getBaseContext(), "Post comment success!", Toast.LENGTH_LONG).show();
                    sendComment.setEnabled(true);
                    finish();
                    startActivity(getIntent());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean checkNetworkInfo()
    {
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //wifi
        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(wifi==State.CONNECTED||wifi==State.CONNECTING) return true;
        else return false;
    }


}
