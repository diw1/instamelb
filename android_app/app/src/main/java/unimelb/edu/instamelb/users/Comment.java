package unimelb.edu.instamelb.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Kevin on 2015/9/15.
 */
public class Comment implements Serializable{
    private long comment_id;
    private long timestamp;
    private double longitude=0.0;
    private double latitude=0.0;
    private String text;
    private long user_id;
    private String username;
    private String user_avatar;

    public Comment() {
    }

    public Comment(JSONObject object){
        try {
            comment_id = object.getLong("comment_id");
            text = object.getString("text");
            timestamp=object.getLong("timestamp");

            if (object.has("location")) {
                JSONObject counts = object.getJSONObject("location");
                if (!counts.isNull("longitude")&&!counts.isNull("latitude")){
                    latitude= counts.getDouble("latitude");
                    longitude= counts.getDouble("longitude");
                }
            }

            if (object.has("from")) {
                JSONObject counts = object.getJSONObject("from");
                user_id= counts.getLong("user_id");
                user_avatar= counts.getString("profile_image");
                username=counts.getString("username");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getComment_id() {
        return comment_id;
    }

    public void setComment_id(long comment_id) {
        this.comment_id = comment_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }
}
