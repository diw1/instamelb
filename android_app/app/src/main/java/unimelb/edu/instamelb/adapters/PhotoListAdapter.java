package unimelb.edu.instamelb.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import unimelb.edu.instamelb.activities.ActivityDetail;
import unimelb.edu.instamelb.fragments.FragmentHome;
import unimelb.edu.instamelb.fragments.FragmentProfile;
import unimelb.edu.instamelb.materialtest.R;
import unimelb.edu.instamelb.users.Photo;

public class PhotoListAdapter extends BaseAdapter{
	private Context mContext;
	
	private ImageLoader mImageLoader;
	private FragmentProfile.AnimateFirstDisplayListener mAnimator;

	private int mWidth;
	private int mHeight;
	private Intent mIntent;
	private ArrayList<Photo> mPhotoList;

	public PhotoListAdapter(Context context) {
		this.mContext = context;
		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher_web)
				.showImageForEmptyUri(R.drawable.ic_launcher_web)
				.showImageOnFail(R.drawable.ic_launcher_web)
				.cacheInMemory(true)
				.cacheOnDisc(false)
				.considerExifParams(true)
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)                		                       
		        .writeDebugLogs()
		        .defaultDisplayImageOptions(displayOptions)		        
		        .build();

		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(config);

		mAnimator  = new FragmentProfile.AnimateFirstDisplayListener();
	}

	public void setData(ArrayList<Photo> data) {
		mPhotoList = data;
	}

	public void setLayoutParam(int width, int height) {
		mWidth 	= width;
		mHeight = height;
	}
	
	@Override
	public int getCount() {
		return (mPhotoList == null) ? 0 : mPhotoList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ImageView imageIv;
		
		if (convertView == null) {
			imageIv = new ImageView(mContext);
			
			imageIv.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            imageIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageIv.setPadding(0, 0, 0, 0);
			imageIv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mIntent =new Intent(mContext, ActivityDetail.class);
					mIntent.putExtra("username", FragmentHome.mUsername);
					mIntent.putExtra("password",FragmentHome.mPassword);
					mIntent.putExtra("photo",mPhotoList.get(position));
					mContext.startActivity(mIntent);
				}
			});
		} else {
			imageIv = (ImageView) convertView;
		}
		
		mImageLoader.displayImage(mPhotoList.get(position).getPhoto_image(), imageIv, mAnimator);
		
		return imageIv;
	}
}