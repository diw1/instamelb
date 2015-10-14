package unimelb.edu.instamelb.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import unimelb.edu.instamelb.activities.ActivityProfile;
import unimelb.edu.instamelb.fragments.FragmentDiscover;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.widget.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by ANDRES on 13/10/2015.
 */
public class CustomAdapterActivity extends RecyclerView.Adapter<CustomAdapterActivity.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private String[] mDataSet;
    private Context mContext;
    private ViewHolder holder;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView,textComment;
        private final ImageView imageView;
        private Intent mIntent;
        private HashMap loginUser;
        private String mUsername;
        private String mPassword;
        private String mEmail;
        private String mUserid;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                ActivityOptionsCompat options;
                @Override
                public void onClick(View v) {
                }
            });
            textView = (TextView) v.findViewById(R.id.user_name);
            textComment = (TextView) v.findViewById(R.id.post_timestamp);
            imageView = (ImageView) v.findViewById(R.id.user_image);
        }

        public TextView getTextView() {
            return textView;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapterActivity(String[] dataSet,Context context) {
        mDataSet = dataSet;
        this.mContext=context;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.discover_item, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.d(TAG, "Element " + position + " set.");
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        String[] parts = mDataSet[position].split("\\|");
        //Log.d("USER>>>>>>>>>>>>", parts[1]);
        switch (parts[0]) {
            case "comment":
                Drawable drawable1 = ContextCompat.getDrawable(mContext, R.drawable.ic_action_dialog);
                viewHolder.imageView.setImageDrawable(drawable1);
                break;
            case "like":
                Drawable drawable2 = ContextCompat.getDrawable(mContext, R.drawable.ic_action_like);
                viewHolder.imageView.setImageDrawable(drawable2);
                break;
            case "follow":
                Drawable drawable3 = ContextCompat.getDrawable(mContext, R.drawable.ic_action_star);
                viewHolder.imageView.setImageDrawable(drawable3);
                break;
            case "swipe":
                Drawable drawable4 = ContextCompat.getDrawable(mContext, R.drawable.ic_action_wifi);
                viewHolder.imageView.setImageDrawable(drawable4);
                break;
        }
        viewHolder.getTextView().setText(parts[1]);
        viewHolder.textComment.setText("");
        //UrlImageViewHelper.setUrlDrawable(viewHolder.imageView, parts[1], R.drawable.ic_contact_picture);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
