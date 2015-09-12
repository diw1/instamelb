package unimelb.edu.instamelb.users;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kevin on 2015/9/9.
 */
public class User {
    public long mId;
    public String mUserName;
    public String mEmail;
    public int mFollowersCount;
    public int mFollowingCount;
    public int mPhotoCount;
    public String mProfileImageUrl;

    public User(String jsonAsString) {
        try {
            JSONObject object = new JSONObject(jsonAsString);
            mId = object.getLong("user_id");
            mUserName = object.getString("username");
            mEmail = object.getString("email");

            if (object.has("counts")) {
                JSONObject counts = object.getJSONObject("counts");
                mFollowersCount = counts.getInt("followed_by");
                mFollowingCount = counts.getInt("follows");
                mPhotoCount = counts.getInt("photos");
            }

            if (object.has("profile_image")) {
                mProfileImageUrl = object.getString("profile_image");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public int getmFollowersCount() {
        return mFollowersCount;
    }

    public void setmFollowersCount(int mFollowersCount) {
        this.mFollowersCount = mFollowersCount;
    }

    public int getmFollowingCount() {
        return mFollowingCount;
    }

    public void setmFollowingCount(int mFollowingCount) {
        this.mFollowingCount = mFollowingCount;
    }

    public int getmPhotoCount() {
        return mPhotoCount;
    }

    public void setmPhotoCount(int mPhotoCount) {
        this.mPhotoCount = mPhotoCount;
    }

    public String getmProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setmProfileImageUrl(String mProfileImageUrl) {
        this.mProfileImageUrl = mProfileImageUrl;
    }
}
