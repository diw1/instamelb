package unimelb.edu.instamelb.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import unimelb.edu.instamelb.activities.ActivityDetail;
import unimelb.edu.instamelb.activities.ActivityProfile;
import unimelb.edu.instamelb.extras.Util;
import unimelb.edu.instamelb.fragments.FragmentHome;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.APIRequest;
import unimelb.edu.instamelb.users.Comment;
import unimelb.edu.instamelb.users.Photo;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by Kevin on 2015/9/14.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.userFeedViewHolder>{

    private List<Photo> photosList;
    private Context mContext;
    private userFeedViewHolder holder;
    private int lastAnimatedPosition = -1;
    private Photo photo;
    private Intent mIntent;

    public RecyclerViewAdapter(List<Photo> photos,Context context) {
        this.photosList = photos;
        this.mContext=context;

    }

    class userFeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView post_profile;
        TextView post_username;
        TextView post_timestamp;
        ImageView like_icon;
        TextView like_count;
        ImageView feed_photo;
        TextView photo_desc;
        Button load_more;
        ImageView comment_photo;
        TextView comment_username;
        TextView comment_text;

        public userFeedViewHolder(final View itemView) {
            super(itemView);
            post_profile = (ImageView) itemView.findViewById(R.id.post_profile);
            post_timestamp= (TextView) itemView.findViewById(R.id.post_timestamp);
            post_username=(TextView)itemView.findViewById(R.id.post_username);
            like_icon = (ImageView) itemView.findViewById(R.id.like_icon);
            like_count= (TextView) itemView.findViewById(R.id.like_count);
            feed_photo = (ImageView) itemView.findViewById(R.id.feed_photo);
            comment_text= (TextView) itemView.findViewById(R.id.comment_text);
            comment_username=(TextView)itemView.findViewById(R.id.comment_username);
            comment_photo=(ImageView) itemView.findViewById(R.id.comment_photo);
            photo_desc = (TextView) itemView.findViewById(R.id.photo_desc);
            load_more = (Button) itemView.findViewById(R.id.load_more);
            post_profile.setOnClickListener(this);
            post_username.setOnClickListener(this);
            like_icon.setOnClickListener(this);
            like_count.setOnClickListener(this);
            feed_photo.setOnClickListener(this);
            load_more.setOnClickListener(this);
            comment_username.setOnClickListener(this);
            comment_photo.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            Photo currentPhoto= photosList.get(getPosition());
            List<Comment> commentData=currentPhoto.getComment_list();
            ActivityOptionsCompat options;
            switch (v.getId()){
                case R.id.post_profile:
                    mIntent =new Intent(mContext, ActivityProfile.class);
                    mIntent.putExtra("username", FragmentHome.mUsername);
                    mIntent.putExtra("password",FragmentHome.mPassword);
                    mIntent.putExtra("userid", String.valueOf(currentPhoto.getUser_id()));
                    mIntent.putExtra("photo",currentPhoto);
                    options = ActivityOptionsCompat .makeSceneTransitionAnimation((Activity) mContext, holder.post_profile, "avatar");
                    mContext.startActivity(mIntent, options.toBundle());
                    break;
                case R.id.post_username:
                    mIntent =new Intent(mContext, ActivityProfile.class);
                    mIntent.putExtra("username", FragmentHome.mUsername);
                    mIntent.putExtra("password",FragmentHome.mPassword);
                    mIntent.putExtra("userid", String.valueOf(currentPhoto.getUser_id()));
                    mIntent.putExtra("photo", currentPhoto);
                    options = ActivityOptionsCompat .makeSceneTransitionAnimation((Activity) mContext, holder.post_profile, "avatar");
                    mContext.startActivity(mIntent, options.toBundle());
                    break;
                case R.id.like_icon:
                    like_icon.setEnabled(false);
                    String[] args={FragmentHome.mUsername,FragmentHome.mPassword};
                    new LikeAction(currentPhoto,currentPhoto.isUser_has_liked(),getPosition()).execute(args);
                    break;
                case R.id.comment_photo:
                    mIntent =new Intent(mContext, ActivityProfile.class);
                    mIntent.putExtra("username", FragmentHome.mUsername);
                    mIntent.putExtra("password",FragmentHome.mPassword);
                    mIntent.putExtra("photo",currentPhoto);
                    mIntent.putExtra("userid", String.valueOf(currentPhoto.getComment_list().get(0).getUser_id()));
                    options = ActivityOptionsCompat .makeSceneTransitionAnimation((Activity) mContext, holder.comment_photo, "avatar");
                    mContext.startActivity(mIntent, options.toBundle());
                    break;
                case R.id.comment_username:
                    mIntent =new Intent(mContext, ActivityProfile.class);
                    mIntent.putExtra("username", FragmentHome.mUsername);
                    mIntent.putExtra("password",FragmentHome.mPassword);
                    mIntent.putExtra("photo",currentPhoto);
                    mIntent.putExtra("userid", String.valueOf(currentPhoto.getComment_list().get(0).getUser_id()));
                    options = ActivityOptionsCompat .makeSceneTransitionAnimation((Activity) mContext, holder.comment_photo, "avatar");
                    mContext.startActivity(mIntent, options.toBundle());
                    break;
                case R.id.feed_photo:
                    mIntent =new Intent(mContext, ActivityDetail.class);
                    mIntent.putExtra("username", FragmentHome.mUsername);
                    mIntent.putExtra("password",FragmentHome.mPassword);
                    mIntent.putExtra("photo",currentPhoto);
                    mContext.startActivity(mIntent);

            }
        }
    }
    @Override
    public userFeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.userfeed_item,viewGroup,false);
        userFeedViewHolder nvh=new userFeedViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(userFeedViewHolder personViewHolder, int i) {
        final int j=i;
        holder=personViewHolder;
        setAnimation(holder.itemView, i);
        photo = photosList.get(i);
        holder.post_username.setText(photo.getUsername());
        holder.post_timestamp.setText(Util.getDateTime(photo.getTimestamp()));
        UrlImageViewHelper.setUrlDrawable(holder.post_profile, photo.getUser_avatar(),
                R.drawable.ic_action_photo);
        if (!photo.isUser_has_liked()){
            holder.like_icon.setImageDrawable(holder.like_icon.getContext().getResources().getDrawable(R.drawable.unlike));
        }
        holder.like_count.setText(Util.getPrettyCount(photo.getLike_count()));
        UrlImageViewHelper.setUrlDrawable(holder.feed_photo, photo.getPhoto_image(),
                R.drawable.ic_action_photo);
        if( !photo.getComment_list().isEmpty()) {
            holder.comment_username.setText(photo.getComment_list().get(0).getUsername());
            holder.comment_text.setText(photo.getComment_list().get(0).getText());
            UrlImageViewHelper.setUrlDrawable(holder.comment_photo, photo.getComment_list().get(0).getUser_avatar(),
                    R.drawable.ic_action_photo);
        }

    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastAnimatedPosition)
        {
            viewToAnimate.setTranslationY(getScreenHeight(viewToAnimate.getContext()));
            viewToAnimate.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3.f)).setDuration(1000).start();
            //Animation animation = AnimationUtils.loadAnimation(activity, android.support.v7.appcompat.R.anim.abc_slide_in_bottom);
            //viewToAnimate.startAnimation(animation);
            lastAnimatedPosition = position;
        }
    }
    private static int getScreenHeight(Context c) {

        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        return screenHeight;
    }

    public class LikeAction extends AsyncTask<String, Void, Photo> {
        private Photo post;
        private boolean like;
        private int index;
        public LikeAction(Photo p, boolean like,int index){
            this.like = like;
            this.post=p;
            this.index=index;
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

            photosList.set(index, post);
            holder.like_count.setText(Util.getPrettyCount(post.getLike_count()));
            if (!photo.isUser_has_liked()){
                holder.like_icon.setImageDrawable(holder.like_icon.getContext().getResources().getDrawable(R.drawable.unlike));
            }else {
                holder.like_icon.setImageDrawable(holder.like_icon.getContext().getResources().getDrawable(R.drawable.like));
            }
            holder.like_icon.setEnabled(true);
        }
    }
    @Override
    public int getItemCount() {
        return photosList.size();
    }
}
