package infiapp.com.videomaker.theme.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import infiapp.com.videomaker.R;
import infiapp.com.videomaker.song.SoundFile;


@SuppressLint({"ClickableViewAccessibility"})
public class WaveformView extends View {
    private Paint mBorderLinePaint;
    private float mDensity;
    private GestureDetector mGestureDetector;
    private Paint mGridPaint = new Paint();
    private int[] mHeightsAtThisZoomLevel;
    private boolean mInitialized;
    private int[] mLenByZoomLevel;
    private WaveformListener mListener;
    private int mNumZoomLevels;
    private int mOffset;
    private Paint mPlaybackLinePaint;
    private int mPlaybackPos;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private Paint mSelectedLinePaint;
    private int mSelectionEnd;
    private int mSelectionStart;
    private SoundFile mSoundFile;
    private Paint mTimecodePaint;
    private Paint mUnselectedBkgndLinePaint;
    private Paint mUnselectedLinePaint;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int mZoomLevel;

    public interface WaveformListener {
        void waveformDraw();

        void waveformFling(float f);

        void waveformTouchEnd();

        void waveformTouchMove(float f);

        void waveformTouchStart(float f);
    }

    class C13831 extends SimpleOnGestureListener {
        C13831() {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
            WaveformView.this.mListener.waveformFling(vx);
            return true;
        }
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
        this.mGridPaint.setAntiAlias(false);
        this.mGridPaint.setColor(ContextCompat.getColor(getContext(),R.color.grid_line));
        this.mSelectedLinePaint = new Paint();
        this.mSelectedLinePaint.setAntiAlias(false);
        this.mSelectedLinePaint.setColor(ContextCompat.getColor(getContext(),R.color.waveform_selected));
        this.mUnselectedLinePaint = new Paint();
        this.mUnselectedLinePaint.setAntiAlias(false);
        this.mUnselectedLinePaint.setColor(ContextCompat.getColor(getContext(),R.color.waveform_unselected));
        this.mUnselectedBkgndLinePaint = new Paint();
        this.mUnselectedBkgndLinePaint.setAntiAlias(false);
        this.mUnselectedBkgndLinePaint.setColor(ContextCompat.getColor(getContext(),R.color.waveform_unselected_bkgnd_overlay));
        this.mBorderLinePaint = new Paint();
        this.mBorderLinePaint.setAntiAlias(true);
        this.mBorderLinePaint.setStrokeWidth(1.5f);
        this.mBorderLinePaint.setPathEffect(new DashPathEffect(new float[]{3.0f, 2.0f}, 0.0f));
        this.mBorderLinePaint.setColor(ContextCompat.getColor(getContext(),R.color.selection_border));
        this.mPlaybackLinePaint = new Paint();
        this.mPlaybackLinePaint.setAntiAlias(false);
        this.mPlaybackLinePaint.setColor(ContextCompat.getColor(getContext(),R.color.playback_indicator));
        this.mTimecodePaint = new Paint();
        this.mTimecodePaint.setTextSize(12.0f);
        this.mTimecodePaint.setAntiAlias(true);
        this.mTimecodePaint.setColor(ContextCompat.getColor(getContext(),R.color.timecode));
        this.mTimecodePaint.setShadowLayer(2.0f, 1.0f, 1.0f, ContextCompat.getColor(getContext(),R.color.timecode_shadow));
        this.mGestureDetector = new GestureDetector(context, new C13831());
        this.mSoundFile = null;
        this.mLenByZoomLevel = null;
        this.mValuesByZoomLevel =  null;
        this.mHeightsAtThisZoomLevel = null;
        this.mOffset = 0;
        this.mPlaybackPos = -1;
        this.mSelectionStart = 0;
        this.mSelectionEnd = 0;
        this.mDensity = 1.0f;
        this.mInitialized = false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mGestureDetector.onTouchEvent(event)) {
            switch (event.getAction()) {
                case 0:
                    this.mListener.waveformTouchStart(event.getX());
                    break;
                case 1:
                    this.mListener.waveformTouchEnd();
                    break;
                case 2:
                    this.mListener.waveformTouchMove(event.getX());
                    break;
            }
        }
        return true;
    }

    public void setSoundFile(SoundFile soundFile) {
        this.mSoundFile = soundFile;
        this.mSampleRate = this.mSoundFile.getSampleRate();
        this.mSamplesPerFrame = this.mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
        this.mHeightsAtThisZoomLevel = null;
    }

    public boolean isInitialized() {
        return this.mInitialized;
    }

    public int getZoomLevel() {
        return this.mZoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        while (this.mZoomLevel > zoomLevel) {
            zoomIn();
        }
        while (this.mZoomLevel < zoomLevel) {
            zoomOut();
        }
    }

    public boolean canZoomIn() {
        return this.mZoomLevel > 0;
    }

    public void zoomIn() {
        if (canZoomIn()) {
            this.mZoomLevel--;
            this.mSelectionStart *= 2;
            this.mSelectionEnd *= 2;
            this.mHeightsAtThisZoomLevel = null;
            this.mOffset = ((this.mOffset + (getMeasuredWidth() / 2)) * 2) - (getMeasuredWidth() / 2);
            if (this.mOffset < 0) {
                this.mOffset = 0;
            }
            invalidate();
        }
    }

    public boolean canZoomOut() {
        return this.mZoomLevel < this.mNumZoomLevels + -1;
    }

    public void zoomOut() {
        if (canZoomOut()) {
            this.mZoomLevel++;
            this.mSelectionStart /= 2;
            this.mSelectionEnd /= 2;
            this.mOffset = ((this.mOffset + (getMeasuredWidth() / 2)) / 2) - (getMeasuredWidth() / 2);
            if (this.mOffset < 0) {
                this.mOffset = 0;
            }
            this.mHeightsAtThisZoomLevel = null;
            invalidate();
        }
    }

    public int maxPos() {
        return this.mLenByZoomLevel[this.mZoomLevel];
    }

    public int secondsToFrames(double seconds) {
        return (int) ((((1.0d * seconds) * ((double) this.mSampleRate)) / ((double) this.mSamplesPerFrame)) + 0.5d);
    }

    public int secondsToPixels(double seconds) {
        return (int) ((((this.mZoomFactorByZoomLevel[this.mZoomLevel] * seconds) * ((double) this.mSampleRate)) / ((double) this.mSamplesPerFrame)) + 0.5d);
    }

    public double pixelsToSeconds(int pixels) {
        return (((double) pixels) * ((double) this.mSamplesPerFrame)) / (((double) this.mSampleRate) * this.mZoomFactorByZoomLevel[this.mZoomLevel]);
    }

    public int millisecsToPixels(int msecs) {
        return (int) (((((((double) msecs) * 1.0d) * ((double) this.mSampleRate)) * this.mZoomFactorByZoomLevel[this.mZoomLevel]) / (1000.0d * ((double) this.mSamplesPerFrame))) + 0.5d);
    }

    public int pixelsToMillisecs(int pixels) {
        return (int) (((((double) pixels) * (1000.0d * ((double) this.mSamplesPerFrame))) / (((double) this.mSampleRate) * this.mZoomFactorByZoomLevel[this.mZoomLevel])) + 0.5d);
    }

    public void setParameters(int start, int end, int offset) {
        this.mSelectionStart = start;
        this.mSelectionEnd = end;
        this.mOffset = offset;
    }

    public int getStart() {
        return this.mSelectionStart;
    }

    public int getEnd() {
        return this.mSelectionEnd;
    }

    public int getOffset() {
        return this.mOffset;
    }

    public void setPlayback(int pos) {
        this.mPlaybackPos = pos;
    }

    public void setListener(WaveformListener listener) {
        this.mListener = listener;
    }

    public void recomputeHeights(float density) {
        this.mHeightsAtThisZoomLevel = null;
        this.mDensity = density;
        this.mTimecodePaint.setTextSize((float) ((int) (12.0f * density)));
        invalidate();
    }

    protected void drawWaveformLine(Canvas canvas, int x, int y0, int y1, Paint paint) {
        canvas.drawLine((float) x, (float) y0, (float) x, (float) y1, paint);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mSoundFile != null) {
            if (this.mHeightsAtThisZoomLevel == null) {
                computeIntsForThisZoomLevel();
            }
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            int start = this.mOffset;
            int width = this.mHeightsAtThisZoomLevel.length - start;
            int ctr = measuredHeight / 2;
            if (width > measuredWidth) {
                width = measuredWidth;
            }
            double onePixelInSecs = pixelsToSeconds(1);
            boolean onlyEveryFiveSecs = onePixelInSecs > 0.02d;
            double fractionalSecs = ((double) this.mOffset) * onePixelInSecs;
            int integerSecs = (int) fractionalSecs;
            int i = 0;
            while (i < width) {
                int i2 = i + 1;
                fractionalSecs += onePixelInSecs;
                int integerSecsNew = (int) fractionalSecs;
                if (integerSecsNew != integerSecs) {
                    integerSecs = integerSecsNew;
                    if (!onlyEveryFiveSecs || integerSecs % 5 == 0) {
                        canvas.drawLine((float) i2, 0.0f, (float) i2, (float) measuredHeight, this.mGridPaint);
                    }
                }
                i = i2;
            }
            i = 0;
            while (i < width) {
                Paint paint;
                if (i + start < this.mSelectionStart || i + start >= this.mSelectionEnd) {
                    drawWaveformLine(canvas, i, 0, measuredHeight, this.mUnselectedBkgndLinePaint);
                    paint = this.mUnselectedLinePaint;
                } else {
                    paint = this.mSelectedLinePaint;
                }
                drawWaveformLine(canvas, i, ctr - this.mHeightsAtThisZoomLevel[start + i], (ctr + 1) + this.mHeightsAtThisZoomLevel[start + i], paint);
                if (i + start == this.mPlaybackPos) {
                    canvas.drawLine((float) i, 0.0f, (float) i, (float) measuredHeight, this.mPlaybackLinePaint);
                }
                i++;
            }
            for (i = width; i < measuredWidth; i++) {
                drawWaveformLine(canvas, i, 0, measuredHeight, this.mUnselectedBkgndLinePaint);
            }
            canvas.drawLine(((float) (this.mSelectionStart - this.mOffset)) + 0.5f, 30.0f, ((float) (this.mSelectionStart - this.mOffset)) + 0.5f, (float) measuredHeight, this.mBorderLinePaint);
            canvas.drawLine(((float) (this.mSelectionEnd - this.mOffset)) + 0.5f, 0.0f, ((float) (this.mSelectionEnd - this.mOffset)) + 0.5f, (float) (measuredHeight - 30), this.mBorderLinePaint);
            double timecodeIntervalSecs = 1.0d;
            if (1.0d / onePixelInSecs < 50.0d) {
                timecodeIntervalSecs = 5.0d;
            }
            if (timecodeIntervalSecs / onePixelInSecs < 50.0d) {
                timecodeIntervalSecs = 15.0d;
            }
            fractionalSecs = ((double) this.mOffset) * onePixelInSecs;
            int integerTimecode = (int) (fractionalSecs / timecodeIntervalSecs);
            i = 0;
            while (i < width) {
                i++;
                fractionalSecs += onePixelInSecs;
                integerSecs = (int) fractionalSecs;
                int integerTimecodeNew = (int) (fractionalSecs / timecodeIntervalSecs);
                if (integerTimecodeNew != integerTimecode) {
                    integerTimecode = integerTimecodeNew;
                    String timecodeMinutes = String.valueOf(integerSecs / 60);
                    String timecodeSeconds = String.valueOf(integerSecs % 60);
                    if (integerSecs % 60 < 10) {
                        timecodeSeconds = "0" + timecodeSeconds;
                    }
                    String timecodeStr = new StringBuilder(String.valueOf(timecodeMinutes)).append(":").append(timecodeSeconds).toString();
                    canvas.drawText(timecodeStr, ((float) i) - ((float) (0.5d * ((double) this.mTimecodePaint.measureText(timecodeStr)))), (float) ((int) (12.0f * this.mDensity)), this.mTimecodePaint);
                }
            }
            if (this.mListener != null) {
                this.mListener.waveformDraw();
            }
        }
    }

    private void computeDoublesForAllZoomLevels() {
        int i;
        int numFrames = this.mSoundFile.getNumFrames();
        int[] frameGains = this.mSoundFile.getFrameGains();
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = (double) frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = (double) frameGains[0];
            smoothedGains[1] = (double) frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (((double) frameGains[0]) / 2.0d) + (((double) frameGains[1]) / 2.0d);
            for (i = 1; i < numFrames - 1; i++) {
                smoothedGains[i] = ((((double) frameGains[i - 1]) / 3.0d) + (((double) frameGains[i]) / 3.0d)) + (((double) frameGains[i + 1]) / 3.0d);
            }
            smoothedGains[numFrames - 1] = (((double) frameGains[numFrames - 2]) / 2.0d) + (((double) frameGains[numFrames - 1]) / 2.0d);
        }
        double maxGain = 1.0d;
        for (i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0d;
        if (maxGain > 255.0d) {
            scaleFactor = 255.0d / maxGain;
        }
        maxGain = 0.0d;
        int[] gainHist = new int[256];
        for (i = 0; i < numFrames; i++) {
            int smoothedGain = (int) (smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0) {
                smoothedGain = 0;
            }
            if (smoothedGain > 255) {
                smoothedGain = 255;
            }
            if (((double) smoothedGain) > maxGain) {
                maxGain = (double) smoothedGain;
            }
            gainHist[smoothedGain] = gainHist[smoothedGain] + 1;
        }
        double minGain = 0.0d;
        int sum = 0;
        while (minGain < 255.0d && sum < numFrames / 20) {
            sum += gainHist[(int) minGain];
            minGain += 1.0d;
        }
        sum = 0;
        while (maxGain > 2.0d && sum < numFrames / 100) {
            sum += gainHist[(int) maxGain];
            maxGain -= 1.0d;
        }
        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (i = 0; i < numFrames; i++) {
            double value = ((smoothedGains[i] * scaleFactor) - minGain) / range;
            if (value < 0.0d) {
                value = 0.0d;
            }
            if (value > 1.0d) {
                value = 1.0d;
            }
            heights[i] = value * value;
        }
        this.mNumZoomLevels = 5;
        this.mLenByZoomLevel = new int[5];
        this.mZoomFactorByZoomLevel = new double[5];
        this.mValuesByZoomLevel = new double[5][];
        this.mLenByZoomLevel[0] = numFrames * 2;
        this.mZoomFactorByZoomLevel[0] = 2.0d;
        this.mValuesByZoomLevel[0] = new double[this.mLenByZoomLevel[0]];
        if (numFrames > 0) {
            this.mValuesByZoomLevel[0][0] = 0.5d * heights[0];
            this.mValuesByZoomLevel[0][1] = heights[0];
        }
        for (i = 1; i < numFrames; i++) {
            this.mValuesByZoomLevel[0][i * 2] = 0.5d * (heights[i - 1] + heights[i]);
            this.mValuesByZoomLevel[0][(i * 2) + 1] = heights[i];
        }
        this.mLenByZoomLevel[1] = numFrames;
        this.mValuesByZoomLevel[1] = new double[this.mLenByZoomLevel[1]];
        this.mZoomFactorByZoomLevel[1] = 1.0d;
        for (i = 0; i < this.mLenByZoomLevel[1]; i++) {
            this.mValuesByZoomLevel[1][i] = heights[i];
        }
        for (int j = 2; j < 5; j++) {
            this.mLenByZoomLevel[j] = this.mLenByZoomLevel[j - 1] / 2;
            this.mValuesByZoomLevel[j] = new double[this.mLenByZoomLevel[j]];
            this.mZoomFactorByZoomLevel[j] = this.mZoomFactorByZoomLevel[j - 1] / 2.0d;
            for (i = 0; i < this.mLenByZoomLevel[j]; i++) {
                this.mValuesByZoomLevel[j][i] = 0.5d * (this.mValuesByZoomLevel[j - 1][i * 2] + this.mValuesByZoomLevel[j - 1][(i * 2) + 1]);
            }
        }
        if (numFrames > 5000) {
            this.mZoomLevel = 3;
        } else if (numFrames > 1000) {
            this.mZoomLevel = 2;
        } else if (numFrames > 300) {
            this.mZoomLevel = 1;
        } else {
            this.mZoomLevel = 0;
        }
        this.mInitialized = true;
    }

    private void computeIntsForThisZoomLevel() {
        int halfHeight = (getMeasuredHeight() / 2) - 1;
        this.mHeightsAtThisZoomLevel = new int[this.mLenByZoomLevel[this.mZoomLevel]];
        for (int i = 0; i < this.mLenByZoomLevel[this.mZoomLevel]; i++) {
            this.mHeightsAtThisZoomLevel[i] = (int) (this.mValuesByZoomLevel[this.mZoomLevel][i] * ((double) halfHeight));
        }
    }
}
