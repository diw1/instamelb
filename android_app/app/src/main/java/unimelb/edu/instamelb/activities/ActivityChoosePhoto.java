package unimelb.edu.instamelb.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bboyce on 12/09/15.
 */
public class ActivityChoosePhoto extends AppCompatActivity{


    private final int PICK_IMAGE_REQUEST = 1;
    public final static String EXTRA_MESSAGE = "unimelb.edu.instamelb.activities.MESSAGE";

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

            Uri imageUri = data.getData();

            Intent intent = new Intent(getApplicationContext(), ActivityPhoto.class);
            String uriMessage = imageUri.toString();
            intent.putExtra(EXTRA_MESSAGE, uriMessage);
            startActivity(intent);
        }
    }
}
