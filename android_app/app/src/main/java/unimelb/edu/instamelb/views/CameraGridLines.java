package unimelb.edu.instamelb.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.View;

import unimelb.edu.instamelb.materialtest.R;

/**
 * Created by bboyce on 12/09/15.
 */
public class CameraGridLines extends View{



    public CameraGridLines(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int previewWidth = metrics.widthPixels;

        Paint mPaintbrush = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaintbrush.setStyle(Paint.Style.STROKE);
        mPaintbrush.setColor(Color.BLACK);
        mPaintbrush.setStrokeWidth(10);

        this.setBackgroundColor(Color.TRANSPARENT);

//        Bitmap transparentCanvas = Bitmap.createBitmap(previewWidth, previewWidth, Bitmap.Config.ARGB_8888);
//        transparentCanvas.eraseColor(Color.TRANSPARENT);

//        canvas.setBitmap(transparentCanvas);

        this.bringToFront();
        canvas.drawLine(previewWidth / 3, 0, previewWidth / 3, previewWidth, mPaintbrush);

//        Bitmap transparentCanvas = Bitmap.createBitmap(previewWidth, previewWidth, Bitmap.Config.ARGB_4444);
//
//        Canvas mCanvas = new Canvas(transparentCanvas);
//        mCanvas.drawLine(previewWidth / 3, 0, previewWidth / 3, previewWidth, mPaintbrush);
    }


}
