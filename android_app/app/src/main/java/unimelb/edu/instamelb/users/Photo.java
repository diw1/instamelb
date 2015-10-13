package unimelb.edu.instamelb.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kevin on 2015/9/14.
 */
public class Photo implements Serializable {
    private long photo_id;
    private String photo_image;
    private String photo_caption;
    private long timestamp;
    private int comment_count;
    private int like_count;
    private double longitude;
    private double latitude;
    private long user_id;
    private String user_avatar;
    private String username;
    private boolean user_has_liked=false;
    private ArrayList<String> liker_list;
    private ArrayList<Comment> comment_list;

    public Photo(){

    }

    public Photo(long photo_id, String photo_image, String photo_caption, long timestamp,
                 int comment_count, int like_count, double longitude, double latitude,
                 boolean user_has_liked, ArrayList<String> liker_list, ArrayList<Comment> comment_list) {
        this.photo_id = photo_id;
        this.photo_image = photo_image;
        this.photo_caption = photo_caption;
        this.timestamp = timestamp;
        this.comment_count = comment_count;
        this.like_count = like_count;
        this.longitude = longitude;
        this.latitude = latitude;
        this.user_has_liked = user_has_liked;
        this.liker_list = liker_list;
        this.comment_list = comment_list;
    }

    public Photo(long photo_id, String photo_image, String photo_caption, long timestamp,
                 int comment_count, int like_count, double longitude, double latitude,
                 long user_id, String user_avatar, String username, boolean user_has_liked,
                 ArrayList<String> liker_list, ArrayList<Comment> comment_list) {
        super();
        this.photo_id = photo_id;
        this.photo_image = photo_image;
        this.photo_caption = photo_caption;
        this.timestamp = timestamp;
        this.comment_count = comment_count;
        this.like_count = like_count;
        this.longitude = longitude;
        this.latitude = latitude;
        this.user_id = user_id;
        this.user_avatar = user_avatar;
        this.username = username;
        this.user_has_liked = user_has_liked;
        this.liker_list = liker_list;
        this.comment_list = comment_list;
    }

    public Photo(JSONObject object){
        try {
            photo_id = object.getLong("photo_id");
            photo_caption = object.getString("photo_caption");
            photo_image = object.getString("photo_image");
            if (object.has("timestamp")){
                timestamp=object.getLong("timestamp");
            }

            if (object.has("comments")) {
                JSONObject counts = object.getJSONObject("comments");
                comment_count=counts.getInt("count");
            }

            if (object.has("likes")) {
                JSONObject counts = object.getJSONObject("likes");
                like_count=counts.getInt("count");
            }

            if (object.has("location")) {
                JSONObject counts = object.getJSONObject("location");
                latitude= counts.getDouble("latitude");
                longitude= counts.getDouble("longitude");
            }

            if (object.has("user")) {
                JSONObject counts = object.getJSONObject("user");
                user_id= counts.getLong("user_id");
                user_avatar= counts.getString("profile_image");
                username=counts.getString("username");
            }
            liker_list=new ArrayList<String>();
            comment_list=new ArrayList<Comment>();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(long photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhoto_image() {
        return photo_image;
    }

    public void setPhoto_image(String photo_image) {
        this.photo_image = photo_image;
    }

    public String getPhoto_caption() {
        return photo_caption;
    }

    public void setPhoto_caption(String photo_caption) {
        this.photo_caption = photo_caption;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
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

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isUser_has_liked() {
        return user_has_liked;
    }

    public void setUser_has_liked(boolean user_has_liked) {
        this.user_has_liked = user_has_liked;
    }

    public ArrayList<String> getLiker_list() {
        return liker_list;
    }

    public void setLiker_list(ArrayList<String> liker_list) {
        this.liker_list = liker_list;
    }

    public ArrayList<Comment> getComment_list() {
        return comment_list;
    }

    public void setComment_list(ArrayList<Comment> comment_list) {
        this.comment_list = comment_list;
    }
}
