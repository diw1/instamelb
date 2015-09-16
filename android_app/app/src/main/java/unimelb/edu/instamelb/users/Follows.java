package unimelb.edu.instamelb.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kevin on 2015/9/12.
 */
public class Follows {
    public ArrayList<Long> mId =new ArrayList<>();
    public ArrayList<String> mProfileUrl = new ArrayList<>();
    public ArrayList<String> mUsername = new ArrayList<>();

    public Follows(String jsonAsString) {
        try {
            JSONObject object = new JSONObject(jsonAsString);
            if (object.has("follows")){
                JSONArray photoList= object.getJSONArray("follows");
                int length=photoList.length();
                for (int i=0;i<length;i++){
                    object= photoList.getJSONObject(i);
                    mId.add(object.getLong("user_id"));
                    mProfileUrl.add(object.getString("profile_image"));
                    mUsername.add(object.getString("username"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Long> getmId() {
        return mId;
    }

    public void setmId(ArrayList<Long> mId) {
        this.mId = mId;
    }

    public ArrayList<String> getmProfileUrl() {
        return mProfileUrl;
    }

    public void setmProfileUrl(ArrayList<String> mProfileUrl) {
        this.mProfileUrl = mProfileUrl;
    }

    public ArrayList<String> getmUsername() {
        return mUsername;
    }

    public void setmUsername(ArrayList<String> mUsername) {
        this.mUsername = mUsername;
    }

}
