package infiapp.com.videomaker.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;

public class ScaleCardLayout extends CardView {
    public int mAspectRatioHeight = 360;
    public int mAspectRatioWidth = 640;



    public ScaleCardLayout(Context context) {
        super(context);
    }

    public ScaleCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public ScaleCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context, attrs);
    }

    private void Init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScaleCardLayout);
        this.mAspectRatioWidth = a.getInt(R.styleable.ScaleCardLayout_aspectRatioWidth, MyApplication.VIDEO_WIDTH);
        this.mAspectRatioHeight = a.getInt(R.styleable.ScaleCardLayout_aspectRatioHeight, MyApplication.VIDEO_HEIGHT);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalWidth;
        int finalHeight;
        if (this.mAspectRatioHeight == this.mAspectRatioWidth) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);
        int calculatedHeight = (int) (((float) (this.mAspectRatioHeight * originalWidth)) / ((float) this.mAspectRatioWidth));
        if (calculatedHeight > originalHeight) {
            finalWidth = (int) (((float) (this.mAspectRatioWidth * originalHeight)) / ((float) this.mAspectRatioHeight));
            finalHeight = originalHeight;
        } else {
            finalWidth = originalWidth;
            finalHeight = calculatedHeight;
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
