/*
package unimelb.edu.instamelb.activities;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import unimelb.edu.instamelb.materialtest.R;

*/
/**
 * Created by bboyce on 12/09/15.
 *//*

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;

    CameraPreview(Context context, Camera camera) {
        super(context);

        mCamera = camera;
        mSurfaceView = new SurfaceView(context);
//        addView(mSurfaceView);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

//    @Override
    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (IOException e) {
            Log.d("ERROR", "Error setting camera preview: " + e.getMessage());
        }
    }


    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }

//        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            List<Camera.Size> screenSize = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPreviewSizes = screenSize;
            requestLayout();

            try {

                mCamera.setPreviewDisplay(mSurfaceHolder);
//                mCamera.release();
            }
            catch (IOException e) {
                Log.d("ERROR", "Error setting camera preview: " + e.getMessage());
            }
            mCamera.startPreview();
        }
    }

    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    // Not required at this time
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    // Not required at this time
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }




    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


}
*/
