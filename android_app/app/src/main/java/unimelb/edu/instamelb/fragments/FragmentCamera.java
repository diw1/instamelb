package unimelb.edu.instamelb.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import unimelb.edu.instamelb.activities.ActivityCamera;
import unimelb.edu.instamelb.extras.SortListener;
import unimelb.edu.instamelb.materialtest.R;

//import android.support.Fragment;
/**
 * Created by bboyce on 12/09/15.
 */
public class FragmentCamera extends Fragment implements SortListener, View.OnClickListener {

    static final int TAKE_PHOTO = 1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView mImageView;
    private ImageView mThumbnailImageView;


    private Context mContext;
    private View mCameraView;
    private Button mButton;
    private ProgressBar mLoadingPb;
    private GridView mGridView;

    public FragmentCamera() {
        super();
    }

    public static FragmentCamera newInstance(String param1, String param2) {
        FragmentCamera fragment = new FragmentCamera();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mCameraView=inflater.inflate(R.layout.fragment_camera, container, false);
        mContext=container.getContext();
        Intent intent = new Intent(getActivity(), ActivityCamera.class);
        startActivity(intent);
        Log.d("FP", "CREATED CAMERA VIEW");
        return mCameraView;



    }
//
//    private class CameraTask extends AsyncTask<String, Integer, List<String>> {
//
//        @Override
//        protected List doInBackground(String... strings) {
//            List<String> result =new ArrayList<>();
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(List<String> result){
//
//        }
//
//
//    }

//    protected void dispatchTakePictureIntent() {
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        ActivityCamera activity = (ActivityCamera) getActivity();
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                Toast toast = Toast.makeText(activity, "Photo could not be saved...", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//            if (photoFile != null) {
//                Uri fileUri = Uri.fromFile(photoFile);
//                activity.setCapturedImageURI(fileUri);
//                activity.setCurrentPhotoPath(fileUri.getPath());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        activity.getCapturedImageURI());
//                startActivityForResult(takePictureIntent, TAKE_PHOTO);
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
//            addPhotoToGallery();
//            ActivityCamera activity = (ActivityCamera) getActivity();
//
//            setFullImageFromFilePath(activity.getCurrentPhotoPath(), mImageView);
//            setFullImageFromFilePath(activity.getCurrentPhotoPath(), mThumbnailImageView);
//        } else {
//            Toast.makeText(getActivity(), "Could not take photo", Toast.LENGTH_SHORT)
//                    .show();
//        }
//    }
//
//
//    protected File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//
//        // Save a file: path for use with ACTION_VIEW intents
//        ActivityCamera activity = (ActivityCamera) getActivity();
//        activity.setCurrentPhotoPath("file:" + image.getAbsolutePath());
//        return image;
//    }
//
//
//    protected void addPhotoToGallery() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        ActivityCamera activity = (ActivityCamera) getActivity();
//        File f = new File(activity.getCurrentPhotoPath());
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.getActivity().sendBroadcast(mediaScanIntent);
//    }
//
////    // @Override
////    public void OnClickListener(View v) {
////        dispatchTakePictureIntent();
////    }
//
//
//
//    /**
//     * Scale the photo down and fit it to image views.
//     * <p/>
//     * "Drastically increases performance" to set images using this technique.
//     * Read more:http://developer.android.com/training/camera/photobasics.html
//     */
//    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(imagePath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
//        imageView.setImageBitmap(bitmap);
//    }
//
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSortByName() {

    }

    @Override
    public void onSortByDate() {

    }

    @Override
    public void onSortByRating() {

    }
}
