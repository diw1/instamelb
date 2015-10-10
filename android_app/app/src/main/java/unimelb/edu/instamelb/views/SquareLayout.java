package unimelb.edu.instamelb.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by bboyce on 12/09/15.
 */
public class SquareLayout extends View{

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int value;
        if (width > height) value = height;
        else value = width;
        setMeasuredDimension(value, value);
    }



    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int previewWidth = metrics.widthPixels;
        int previewHeight = metrics.heightPixels;
        int borderHeight = (previewHeight-previewWidth)/2;

        Paint mPaintbrush = new Paint(Paint.ANTI_ALIAS_FLAG);

        int xStart = 0;
        int xFinish = previewWidth;

        int yStart1 = 0;
        int yEnd1 = borderHeight;

        int yStart2 = previewHeight-borderHeight;
        int yEnd2 = previewHeight;

//        mPaintbrush.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintbrush.setColor(Color.BLACK);
        mPaintbrush.setStrokeWidth(10);

        this.setBackgroundColor(Color.TRANSPARENT);
        this.bringToFront();
        canvas.drawRect(previewWidth / 3, 0, previewWidth / 3, previewWidth, mPaintbrush);
    }


}
