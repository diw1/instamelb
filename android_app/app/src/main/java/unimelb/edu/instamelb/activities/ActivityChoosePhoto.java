package unimelb.edu.instamelb.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import unimelb.edu.instamelb.fragments.FragmentChoosePhoto;

/**
 * Created by bboyce on 12/09/15.
 */
public class ActivityChoosePhoto extends AppCompatActivity{


    private final int PICK_IMAGE_REQUEST = 1;

    public ActivityChoosePhoto() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent selectImage = new Intent();
        selectImage.setType("image/*");
        selectImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(selectImage, "Select Picture"), PICK_IMAGE_REQUEST);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                 Log.d("FP", String.valueOf(bitmap));

//                ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
