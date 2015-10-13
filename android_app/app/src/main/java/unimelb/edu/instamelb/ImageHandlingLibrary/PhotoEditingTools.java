package unimelb.edu.instamelb.imagehandlinglibrary;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by bboyce on 12/09/15.
 */
public class PhotoEditingTools extends ImageView  {

    public PhotoEditingTools(Context context) {
        super(context);
    }


    // Crop image
    public void cropImage(Bitmap photo, float startX, float startY, float endX, float endY) {
    }


    // Grayscale filter - colour values optimally weighted
    public Bitmap grayscaleFilter(Bitmap photo){
        final double RED_FACTOR = 0.21;
        final double GREEN_FACTOR = 0.72;
        final double BLUE_FACTOR = 0.07;

        int R, G, B, pixel;
        int width = photo.getWidth();
        int height = photo.getHeight();

        Bitmap adjustedPhoto = Bitmap.createBitmap(width, height, photo.getConfig());

        // loop through pixels in photo
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixel = photo.getPixel(i, j);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // Calculate weighted average to convert pixel to gray
                int weightedAverage = colourCheck((int)((R * RED_FACTOR) + (G * GREEN_FACTOR) + (B * BLUE_FACTOR)));
                adjustedPhoto.setPixel(i, j, Color.rgb(weightedAverage, weightedAverage, weightedAverage));
            }
        }
        return adjustedPhoto;
    }

    // Warm filter - reds enhances, blues decreased
    public Bitmap sunsetFilter(Bitmap photo){
        final double RED_FACTOR = 1.2;
        final double GREEN_FACTOR = 1.0;
        final double BLUE_FACTOR = 0.9;

        int R, G, B, pixel;
        int width = photo.getWidth();
        int height = photo.getHeight();

        Bitmap adjustedPhoto = Bitmap.createBitmap(width, height, photo.getConfig());

        // loop through pixels in photo
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixel = photo.getPixel(i, j);
                R = colourCheck((int) (Color.red(pixel) * RED_FACTOR));
                G = colourCheck((int) (Color.green(pixel) * GREEN_FACTOR));
                B = colourCheck((int) (Color.blue(pixel) * BLUE_FACTOR));

                // Calculate weighted average to convert pixel to gray
                adjustedPhoto.setPixel(i, j, Color.rgb(R, G, B));
            }
        }
        return adjustedPhoto;
    }

    // Color faded to halfway between grayscale and original
    public Bitmap desaturatedFilter(Bitmap photo){

        int R, G, B, pixel;
        int average;
        int width = photo.getWidth();
        int height = photo.getHeight();

        Bitmap adjustedPhoto = Bitmap.createBitmap(width, height, photo.getConfig());

        // loop through pixels in photo
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixel = photo.getPixel(i, j);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                average = (int)((R + G + B) / 3);
                R = colourCheck((int) ((R + average) / 2));
                G = colourCheck((int) ((G + average) / 2));
                B = colourCheck((int) ((B + average) / 2));

                // Calculate weighted average to convert pixel to gray
                adjustedPhoto.setPixel(i, j, Color.rgb(R, G, B));
            }
        }
        return adjustedPhoto;
    }



    // To adjust contrast - set value between -100 to 100
    public Bitmap adjustContrast(Bitmap photo, double contrastValue){

        int R, G, B, pixel;
        int width = photo.getWidth();
        int height = photo.getHeight();
        Bitmap adjustedPhoto = Bitmap.createBitmap(width, height, photo.getConfig());

        double contrastFactor = Math.pow((100 + contrastValue) / 100, 2);

        // loop through pixels in photo
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                pixel = photo.getPixel(i, j);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // adjust values based on contrast required
                R = colourCheck((int)(((((R / 255.0) - 0.5) * contrastFactor) + 0.5) * 255.0));
                G = colourCheck((int)(((((G / 255.0) - 0.5) * contrastFactor) + 0.5) * 255.0));
                B = colourCheck((int)(((((B / 255.0) - 0.5) * contrastFactor) + 0.5) * 255.0));

                adjustedPhoto.setPixel(i, j, Color.rgb(R, G, B));
            }
        }
        return adjustedPhoto;
    }

    // To adjust brightness set value between -130 to 130
    public Bitmap adjustBrightness(Bitmap photo, int brightnessValue) {


        int R, G, B, pixel;
        int width = photo.getWidth();
        int height = photo.getHeight();
        Bitmap adjustedPhoto = Bitmap.createBitmap(width, height, photo.getConfig());

        // loop through pixels in photo
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++) {
                pixel = photo.getPixel(i, j);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // adjust values based on contrast required
                R = colourCheck(R + brightnessValue);
                G = colourCheck(G + brightnessValue);
                B = colourCheck(B + brightnessValue);

                adjustedPhoto.setPixel(i, j, Color.rgb(R, G, B));
            }
        }
        return adjustedPhoto;
    }

    // Helper method to check colour value is between 0-255
    public int colourCheck(int value) {
        if (value < 0){
            value = 0;
        }
        if (value > 255){
            value = 255;
        }
        return value;
    }
}
