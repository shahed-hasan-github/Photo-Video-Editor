package infiapp.com.videomaker.theme.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MarkerView extends ImageView {
    private MarkerListener mListener = null;
    private int mVelocity = 0;

    public interface MarkerListener {
        void markerDraw();

        void markerEnter(MarkerView pvmwsMarkerView);

        void markerFocus(MarkerView pvmwsMarkerView);

        void markerKeyUp();

        void markerLeft(MarkerView pvmwsMarkerView, int i);

        void markerRight(MarkerView pvmwsMarkerView, int i);

        void markerTouchEnd(MarkerView pvmwsMarkerView);

        void markerTouchMove(MarkerView pvmwsMarkerView, float f);

        void markerTouchStart(MarkerView pvmwsMarkerView, float f);
    }

    public MarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
    }

    public void setListener(MarkerListener listener) {
        this.mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                requestFocus();
                this.mListener.markerTouchStart(this, event.getRawX());
                break;
            case 1:
                this.mListener.markerTouchEnd(this);
                break;
            case 2:
                this.mListener.markerTouchMove(this, event.getRawX());
                break;
        }
        return true;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus && this.mListener != null) {
            this.mListener.markerFocus(this);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mListener != null) {
            this.mListener.markerDraw();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        this.mVelocity++;
        int v = (int) Math.sqrt((double) ((this.mVelocity / 2) + 1));
        if (this.mListener != null) {
            if (keyCode == 21) {
                this.mListener.markerLeft(this, v);
                return true;
            } else if (keyCode == 22) {
                this.mListener.markerRight(this, v);
                return true;
            } else if (keyCode == 23) {
                this.mListener.markerEnter(this);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.mVelocity = 0;
        if (this.mListener != null) {
            this.mListener.markerKeyUp();
        }
        return super.onKeyDown(keyCode, event);
    }
}
