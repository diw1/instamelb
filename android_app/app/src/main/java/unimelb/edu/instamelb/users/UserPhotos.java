package unimelb.edu.instamelb.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kevin on 2015/9/10.
 */
public class UserPhotos {
    public ArrayList<Long> mPhotoId=new ArrayList<>();
    public ArrayList<String> mPhotoUrl= new ArrayList<>();
    public ArrayList<String> mPhotoCaption= new ArrayList<>();
    public ArrayList<Integer> mCommentsCount= new ArrayList<>();
    public ArrayList<Integer> mLikeCount= new ArrayList<>();
    public ArrayList<Double> mLongitude= new ArrayList<>();
    public ArrayList<Double> mLatitude= new ArrayList<>();
    public ArrayList<String> mPhotoList= new ArrayList<>();
    public UserPhotos(String jsonAsString) {
        try {
            JSONObject object = new JSONObject(jsonAsString);
            if (object.has("photos")){
                JSONArray photoList= object.getJSONArray("photos");
                int length=photoList.length();
                for (int i=0;i<length;i++){
                    object= photoList.getJSONObject(i);
                    mPhotoId.add( object.getLong("photo_id"));
                    mPhotoUrl.add(object.getString("photo_image"));
                    mPhotoCaption.add(object.getString("photo_caption"));
                    mPhotoList.add(object.getString("photo_image"));
                    if (object.has("comments")) {
                        JSONObject counts = object.getJSONObject("comments");
                        mCommentsCount.add(counts.getInt("count"));
                    }

                    if (object.has("likes")) {
                        JSONObject counts = object.getJSONObject("likes");
                        mLikeCount.add(counts.getInt("count"));
                    }

                    if (object.has("location")) {
                        JSONObject counts = object.getJSONObject("location");
                        mLatitude.add( counts.getDouble("latitude"));
                        mLongitude.add(  counts.getDouble("longitude"));

                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Long> getmPhotoId() {
        return mPhotoId;
    }

    public void setmPhotoId(ArrayList<Long> mPhotoId) {
        this.mPhotoId = mPhotoId;
    }

    public ArrayList<String> getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(ArrayList<String> mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public ArrayList<String> getmPhotoCaption() {
        return mPhotoCaption;
    }

    public void setmPhotoCaption(ArrayList<String> mPhotoCaption) {
        this.mPhotoCaption = mPhotoCaption;
    }

    public ArrayList<Integer> getmCommentsCount() {
        return mCommentsCount;
    }

    public void setmCommentsCount(ArrayList<Integer> mCommentsCount) {
        this.mCommentsCount = mCommentsCount;
    }

    public ArrayList<Integer> getmLikeCount() {
        return mLikeCount;
    }

    public void setmLikeCount(ArrayList<Integer> mLikeCount) {
        this.mLikeCount = mLikeCount;
    }

    public ArrayList<Double> getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(ArrayList<Double> mLongitude) {
        this.mLongitude = mLongitude;
    }

    public ArrayList<Double> getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(ArrayList<Double> mLatitude) {
        this.mLatitude = mLatitude;
    }

    public ArrayList<String> getmPhotoList() {
        return mPhotoList;
    }

    public void setmPhotoList(ArrayList<String> mPhotoList) {
        this.mPhotoList = mPhotoList;
    }
}
