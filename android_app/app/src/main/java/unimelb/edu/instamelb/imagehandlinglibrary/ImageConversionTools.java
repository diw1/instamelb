package unimelb.edu.instamelb.imagehandlinglibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bboyce on 12/09/15.
 */
public class ImageConversionTools {

    public static byte[] convertImageToByteArray(Bitmap image) {
        // Convert image to byte[]
        int bytes = image.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        image.copyPixelsToBuffer(buffer);
        byte[] b = buffer.array();
        Log.d("BYTE COUNT", "Byte count is: " + bytes*8);
        return b;
    }

    public static String convertToBase64(Bitmap image) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String b64image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return b64image;
    }

    public static Bitmap convertByteToBitmap(byte[] bytes) {
        Bitmap image = null;

        int byteCount = bytes.length;
        image = BitmapFactory.decodeByteArray(bytes, 0, byteCount);

        return image;
    }

    public static Bitmap compressImage(Bitmap image) {

        int byteCountX = image.getByteCount();
        Log.d("BYTE COUNT", "Byte count of uncompressed: " + byteCountX);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(byteCountX);
        image.compress(Bitmap.CompressFormat.JPEG, 10, bos);
        byte[] byteArray = bos.toByteArray();
        Bitmap compressedImage = convertByteToBitmap(byteArray);
        int byteCountY = compressedImage.getByteCount();
        Log.d("BYTE COUNT", "Byte count of compressed: " + byteCountY);
        return compressedImage;
    }

    public static Uri saveImageToLibrary(Bitmap image) {

        byte[] byteArray = convertImageToByteArray(image);
        File newImageFile = getOutputMediaFile();
        Uri newImageUri = Uri.fromFile(getOutputMediaFile());
        String imagePath = newImageFile.getAbsolutePath();
        String uriString = newImageUri.toString();

        Log.d("FP", "GOT OUTPUT FILE: " + uriString);

        if (newImageFile == null) {
            Log.e("FP", "ERROR CREATING FILE");
            return newImageUri;
        }

        try {
            FileOutputStream fos = new FileOutputStream(newImageFile);
//            image.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.write(byteArray);
            fos.close();
            Log.d("FP", "FILE OUTPUT STREAM CREATED");
        }
        catch (FileNotFoundException e) {
            Log.e("FP", "FILE NOT FOUND");
        }
        catch (IOException e) {
            Log.e("FP", "ERROR ACCESSING FILE");
        }
        Log.d("FP", "SAVED IMAGE TO FILE");
        return newImageUri;

    };


    public static File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Instamelb");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("FP", "FAILED TO CREATE DIRECTORY");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

//    public static Bitmap getImageFromLibrary(ContentResolver cr, Uri uri) {
//        Bitmap newBitmap = null;
//        try {
//            newBitmap = MediaStore.Images.Media.getBitmap(cr , uri);
//            Log.d("FP", "BITMAP RETURNED FROM URI");
//        } catch (IOException e) {
//            Log.e("ERROR", "COULD NOT RETRIEVE BITMAP");
//        }
//        return newBitmap;
//    }






}
