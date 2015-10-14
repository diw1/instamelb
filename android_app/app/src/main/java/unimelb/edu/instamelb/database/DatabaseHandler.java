package unimelb.edu.instamelb.database;

/**
 * Created by Kevin on 2015/9/11.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "instamelb";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "uname";
    private static final String KEY_UID = "uid";
    private static final String KEY_PASSWORD = "upassword";

    // Feed table name
    private static final String TABLE_FEED = "table";

    // Feed Table Columns names
    private static final String FEED_ID = "id";
    private static final String FEED_TYPE = "type";
    private static final String FEED_MESSAGE = "message";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_UID + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_FEED_TABLE = "CREATE TABLE" + TABLE_FEED + "("
                + FEED_ID + " INTEGER PRIMARY KEY,"
                + FEED_TYPE + " STRING"
                + FEED_MESSAGE + " STRING";
        db.execSQL(CREATE_FEED_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String email, String uname,String upassword, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_USERNAME, uname); // UserName
        values.put(KEY_PASSWORD, upassword); // Password
        values.put(KEY_UID, uid); // Uid

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Getting user data from database
     * */
    public HashMap getUserDetails(){
        HashMap user = new HashMap();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("email", cursor.getString(1));
            user.put("uname", cursor.getString(2));
            user.put("upassword", cursor.getString(3));
            user.put("uid", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    // Write feed to database
    public Boolean writeFeed(String type, String message) {
        return Boolean.FALSE;
    }

    // Grab feeds
    public String[] getFeeds() {
        String getQuery = "SELECT * FROM " + TABLE_FEED;
        SQLiteDatabase db = this.getReadableDatabase();
        return new String[] {"ok"};
    }

    /**
     * Re create database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }

}