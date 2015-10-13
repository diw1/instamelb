package unimelb.edu.instamelb.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> implements Filterable{
    private static final String TAG = "CustomAdapter";

    private String[] mDataSet;
    private Context mContext;
    private ViewHolder holder;

    @Override
    public Filter getFilter() {
        return null;
    }

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
            mUsername=FragmentDiscover.mUsername;
            Log.d("ARGS>>>>>>>>>", mUsername);
            mPassword=FragmentDiscover.mPassword;
            Log.d("ARGS>>>>>>>>>", mPassword);
            mUserid=FragmentDiscover.mId;
            ///Log.d("ARGS>>>>>>>>>", mUserid);
            v.setOnClickListener(new View.OnClickListener() {
                ActivityOptionsCompat options;
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                    String[] parts = mDataSet[getPosition()].split("\\|");
                    mIntent =new Intent(mContext, ActivityProfile.class);
                    mIntent.putExtra("username", mUsername);
                    mIntent.putExtra("password",mPassword);
                    mIntent.putExtra("userid", String.valueOf(parts[2]));
                    mIntent.putExtra("photo", "");
                    //options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, holder.imageView, "avatar");
                    mContext.startActivity(mIntent);
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
    public CustomAdapter(String[] dataSet,Context context) {
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
        viewHolder.getTextView().setText(parts[0]);
        viewHolder.textComment.setText("");
        UrlImageViewHelper.setUrlDrawable(viewHolder.imageView, parts[1], R.drawable.ic_contact_picture);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
