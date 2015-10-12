package unimelb.edu.instamelb.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import unimelb.edu.instamelb.activities.ActivityProfile;
import unimelb.edu.instamelb.fragments.FragmentHome;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.Comment;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by Kevin on 2015/9/18.
 */
public class CommentsAdapter extends BaseAdapter {
    public CommentsAdapter(){

    }
    private Context mContext;
    private Intent mIntent;
    private ArrayList<Comment> photoComments;

    public CommentsAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(ArrayList<Comment> data) {
        photoComments = data;
    }

    @Override
    public int getCount() {
        return (photoComments == null) ? 0 : photoComments.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder {
        public ImageView commentPhoto;
        public TextView commentUsername, commentText;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater;
        if (view == null) {
            inflater = LayoutInflater.from(mContext);
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.comment_item, parent, false);
            holder.commentPhoto = (ImageView) view.findViewById(R.id.comment_photo);
            holder.commentText = (TextView) view.findViewById(R.id.comment_text);
            holder.commentUsername = (TextView) view.findViewById(R.id.comment_username);
            view.setTag(holder);
        }else holder=(ViewHolder) view.getTag();
        view.setAlpha(0);
        view.animate().alpha(1).setDuration(1000).start();
        final Comment comment = photoComments.get(position);
        if(comment.getUsername() != null){
            holder.commentPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIntent = new Intent(mContext, ActivityProfile.class);
                    mIntent.putExtra("username", FragmentHome.mUsername);
                    mIntent.putExtra("password", FragmentHome.mPassword);
                    mIntent.putExtra("userid", String.valueOf(comment.getUser_id()));
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat .makeSceneTransitionAnimation((Activity)mContext, holder.commentPhoto, "avatar");
                    mContext.startActivity(mIntent, options.toBundle());
                }
            });
        }
        UrlImageViewHelper.setUrlDrawable(holder.commentPhoto,comment.getUser_avatar(),R.drawable.ic_contact_picture);
        holder.commentUsername.setText(comment.getUsername());
        holder.commentText.setText(comment.getText());
        return view;
    }
}
