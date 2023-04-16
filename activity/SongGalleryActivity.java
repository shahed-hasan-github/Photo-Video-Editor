package infiapp.com.videomaker.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.song.SeekTest;
import infiapp.com.videomaker.song.SoundFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


import infiapp.com.videomaker.model.MusicData;
import infiapp.com.videomaker.theme.util.FileUtils;
import infiapp.com.videomaker.theme.util.SongMetadataReader;
import infiapp.com.videomaker.theme.view.MarkerView;
import infiapp.com.videomaker.theme.view.WaveformView;
import infiapp.com.videomaker.util.Ads_Preference;


public class SongGalleryActivity extends AppCompatActivity implements MarkerView.MarkerListener, WaveformView.WaveformListener {
    boolean isFromItemClick = false;
    boolean isPlaying = false;
    MusicAdapter mAdapter;
    String mArtist;
    boolean mCanSeekAccurately;
    float mDensity;
    MarkerView mEndMarker;
    int mEndPos;
    TextView mEndText;
    boolean mEndVisible;
    String mExtension;
    ImageView mFfwdButton;
    File mFile;
    String mFilename = "record";
    int mFlingVelocity;
    Handler mHandler;
    boolean mIsPlaying;
    boolean mKeyDown;
    int mLastDisplayedEndPos;
    int mLastDisplayedStartPos;
    boolean mLoadingKeepGoing;
    long mLoadingLastUpdateTime;
    int mMarkerBottomOffset;
    int mMarkerLeftInset;
    int mMarkerRightInset;
    int mMarkerTopOffset;
    int mMaxPos;
    ArrayList<MusicData> mpvmwsMusicData;
    RecyclerView mMusicList;
    int mOffset;
    int mOffsetGoal;
    ImageView mPlayButton;
    int mPlayEndMsec;
    int mPlayStartMsec;
    int mPlayStartOffset;
    MediaPlayer mPlayer;
    LinearLayout loaderLay;
    String mRecordingFilename;
    Uri mRecordingUri;
    ImageView mRewindButton;
    SoundFile mSoundFile;
    MarkerView mStartMarker;
    int mStartPos;
    TextView mStartText;
    boolean mStartVisible;
    Runnable mTimerRunnable = new Run1();
    String mTitle;
    boolean mTouchDragging;
    int mTouchInitialEndPos;
    int mTouchInitialOffset;
    int mTouchInitialStartPos;
    float mTouchStart;
    long mWaveformTouchStartMsec;
    WaveformView mPVMWSWaveformView;
    int mWidth;
    ImageView backimgMusic;
    ImageView doneMusic;
    ImageButton mZoomInButton;
    MusicData selectedpvmwsMusicData;
    ImageButton mZoomOutButton;
    private static final String PREFS_NAME = "preferenceName";
    boolean flagsong = false;
    LinearLayout toolbarMusic;
    private Uri collection;
    LinearLayout banner_container;
    private AdView adView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_songgallery);

        banner_container = findViewById(R.id.banner_container);

        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        mRecordingFilename = null;
        mRecordingUri = null;
        mPlayer = null;
        mIsPlaying = false;
        mSoundFile = null;
        mKeyDown = false;
        mHandler = new Handler();

        loaderLay = findViewById(R.id.loaderLay);

        bindView();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        loadGui();
        init();


        toolbarMusic = (LinearLayout) findViewById(R.id.toolbar_music);
        setLay();
        mHandler.postDelayed(mTimerRunnable, 100);

    }


    void setLay() {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels,
                getResources().getDisplayMetrics().heightPixels * 160 / 1920);
        toolbarMusic.setLayoutParams(paramsTop);

        RelativeLayout.LayoutParams paramsMenu = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 90 / 1080,
                getResources().getDisplayMetrics().heightPixels * 90 / 1920);
        backimgMusic.setLayoutParams(paramsMenu);
        doneMusic.setLayoutParams(paramsMenu);
    }


    void bindView() {
        mMusicList = (RecyclerView) findViewById(R.id.rvMusicList);

    }

    void init() {
        new LoadMusics().execute();
    }

    void setUpRecyclerView() {
        mAdapter = new MusicAdapter(mpvmwsMusicData);
        mMusicList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        mMusicList.setItemAnimator(new DefaultItemAnimator());
        mMusicList.setAdapter(mAdapter);
    }

    ArrayList<MusicData> getMusicFiles() {
        ArrayList<MusicData> mpvmwsMusicData = new ArrayList();
        Cursor mCursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "_data", "_display_name", "duration"}, "is_music != 0", null, "title ASC");
        int trackId = mCursor.getColumnIndex("_id");
        int trackTitle = mCursor.getColumnIndex("title");
        int trackDisplayName = mCursor.getColumnIndex("_display_name");
        int trackData = mCursor.getColumnIndex("_data");
        int trackDuration = mCursor.getColumnIndex("duration");
        while (mCursor.moveToNext()) {
            String path = mCursor.getString(trackData);
            if (isAudioFile(path)) {
                MusicData musicData = new MusicData();
                musicData.trackId = mCursor.getLong(trackId);
                musicData.trackTitle = mCursor.getString(trackTitle);
                musicData.trackdata = path;
                musicData.trackDuration = mCursor.getLong(trackDuration);
                musicData.trackDisplayName = mCursor.getString(trackDisplayName);
                mpvmwsMusicData.add(musicData);
            }
        }
        return mpvmwsMusicData;
    }

    boolean isAudioFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return path.endsWith(".mp3");
    }


    protected void onDestroy() {

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer = null;
        if (mRecordingFilename != null) {
            try {
                if (!new File(mRecordingFilename).delete()) {
                    showFinalAlert(new Exception(), (int) R.string.delete_tmp_error);
                }
                getContentResolver().delete(mRecordingUri, null, null);
            } catch (Exception e) {
                showFinalAlert(e, (int) R.string.delete_tmp_error);
            }
        }
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 62) {
            return super.onKeyDown(keyCode, event);
        }
        onPlay(mStartPos);
        return true;
    }

    public void waveformDraw() {
        mWidth = mPVMWSWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown) {
            updateDisplay();
        } else if (mIsPlaying) updateDisplay();
        else if (mFlingVelocity != 0) updateDisplay();
    }

    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    public void waveformTouchMove(float x) {
        mOffset = trap((int) (((float) mTouchInitialOffset) + (mTouchStart - x)));
        updateDisplay();
    }

    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        if (System.currentTimeMillis() - mWaveformTouchStartMsec < 300) {
            if (mIsPlaying) {
                int seekMsec = mPVMWSWaveformView.pixelsToMillisecs((int) (mTouchStart + ((float) mOffset)));
                if (seekMsec < mPlayStartMsec || seekMsec >= mPlayEndMsec) {
                    handlePause();
                    return;
                } else {
                    mPlayer.seekTo(seekMsec - mPlayStartOffset);
                    return;
                }
            }
            onPlay((int) (mTouchStart + ((float) mOffset)));
        }
    }

    public void waveformFling(float vx) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-vx);
        updateDisplay();
    }

    public void markerDraw() {
        //dosomething
    }

    public void markerTouchStart(MarkerView marker, float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    public void markerTouchMove(MarkerView marker, float x) {
        float delta = x - mTouchStart;
        if (marker == mStartMarker) {
            mStartPos = trap((int) (((float) mTouchInitialStartPos) + delta));
            mEndPos = trap((int) (((float) mTouchInitialEndPos) + delta));
        } else {
            mEndPos = trap((int) (((float) mTouchInitialEndPos) + delta));
            if (mEndPos < mStartPos) {
                mEndPos = mStartPos;
            }
        }
        updateDisplay();
    }

    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;
        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;
        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }
        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }
            setOffsetGoalEnd();
        }
        updateDisplay();
    }

    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;
        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos) {
                mStartPos = mMaxPos;
            }
            mEndPos += mStartPos - saveStart;
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalStart();
        }
        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalEnd();
        }
        updateDisplay();
    }

    public void markerEnter(MarkerView marker) {
        //dosomething
    }

    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }
        mHandler.postDelayed(() -> updateDisplay(), 100);
    }

    public static void onAbout(Activity activity) {
        new Builder(activity).setTitle(R.string.about_title).setMessage(R.string.about_text)
                .setPositiveButton(R.string.alert_ok_button, null).setCancelable(false).show();
    }

    void loadGui() {


        mMarkerLeftInset = (int) (46.0f * mDensity);
        mMarkerRightInset = (int) (48.0f * mDensity);
        mMarkerTopOffset = (int) (mDensity * 10.0f);
        mMarkerBottomOffset = (int) (mDensity * 10.0f);
        mStartText = findViewById(R.id.starttext);
        mEndText = findViewById(R.id.endtext);
        mPlayButton = findViewById(R.id.play);
        mRewindButton = findViewById(R.id.rew);
        mFfwdButton = findViewById(R.id.ffwd);
        mPVMWSWaveformView = findViewById(R.id.waveform);
        mStartMarker = findViewById(R.id.startmarker);
        mEndMarker = findViewById(R.id.endmarker);
        backimgMusic = findViewById(R.id.backimgMusic);
        doneMusic = findViewById(R.id.doneMusic);

        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 210 / 1080,
                getResources().getDisplayMetrics().heightPixels * 100 / 1920);
        params3.addRule(RelativeLayout.CENTER_IN_PARENT);
        mStartText.setLayoutParams(params3);
        mEndText.setLayoutParams(params3);

        RelativeLayout.LayoutParams params31 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 60 / 1080,
                getResources().getDisplayMetrics().heightPixels * 60 / 1920);
        params31.addRule(RelativeLayout.CENTER_IN_PARENT);
        mRewindButton.setLayoutParams(params31);
        mFfwdButton.setLayoutParams(params31);

        RelativeLayout.LayoutParams params40 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 94 / 1080,
                getResources().getDisplayMetrics().heightPixels * 94 / 1920);
        params40.addRule(RelativeLayout.CENTER_IN_PARENT);
        mPlayButton.setLayoutParams(params40);


        mStartText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//dosomething
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//dosomething
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStartText.hasFocus()) {
                    try {
                        mStartPos = mPVMWSWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText()
                                .toString()));
                        updateDisplay();
                    } catch (NumberFormatException e) {
                        //dosomething
                    }
                }
                if (mEndText.hasFocus()) {
                    try {
                        mEndPos = mPVMWSWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText()
                                .toString()));
                        updateDisplay();
                    } catch (NumberFormatException e2) {
                        //dosomething
                    }
                }
            }
        });


        mEndText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//dosomething
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//dosomething
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStartText.hasFocus()) {
                    try {
                        mStartPos = mPVMWSWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText()
                                .toString()));
                        updateDisplay();
                    } catch (NumberFormatException e) {
                        //dosomething
                    }
                }
                if (mEndText.hasFocus()) {
                    try {
                        mEndPos = mPVMWSWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText()
                                .toString()));
                        updateDisplay();
                    } catch (NumberFormatException e2) {
                        //dosomething
                    }
                }

            }
        });

        mPlayButton.setOnClickListener(v -> onPlay(mStartPos));

        mRewindButton.setOnClickListener(v -> {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() + -5000;
                if (newPos < mPlayStartMsec) {
                    newPos = mPlayStartMsec;
                }
                mPlayer.seekTo(newPos);
                return;
            }
            mStartMarker.requestFocus();
            markerFocus(mStartMarker);
        });

        mFfwdButton.setOnClickListener(v -> {

            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() + 5000;
                if (newPos > mPlayEndMsec) {
                    newPos = mPlayEndMsec;
                }
                mPlayer.seekTo(newPos);
                return;
            }
            mEndMarker.requestFocus();
            markerFocus(mEndMarker);
        });
        enableDisableButtons();

        mPVMWSWaveformView.setListener(this);
        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;
        if (mSoundFile != null) {
            mPVMWSWaveformView.setSoundFile(mSoundFile);
            mPVMWSWaveformView.recomputeHeights(mDensity);
            mMaxPos = mPVMWSWaveformView.maxPos();
        }

        mStartMarker.setListener(this);
        mStartMarker.setAlpha(255);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;

        mEndMarker.setListener(this);
        mEndMarker.setAlpha(255);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;
        updateDisplay();

        backimgMusic.setOnClickListener(v -> onBackPressed());

        doneMusic.setOnClickListener(v -> {
            if (flagsong) {
                onSave();
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("musiccustom", selectedpvmwsMusicData.trackdata);
                editor.commit();
                MyApplication.getInstance().setMusicData(selectedpvmwsMusicData);
            }
        });

    }

    void loadFromFile() {

        mFile = new File(mFilename);
        mExtension = getExtensionFromFilename(mFilename);
        SongMetadataReader metadataReader = new SongMetadataReader(this, mFilename);
        mTitle = metadataReader.mTitle;
        mArtist = metadataReader.mArtist;
        String titleLabel = mTitle;
        if (mArtist != null && mArtist.length() > 0) {
            titleLabel = new StringBuilder(String.valueOf(titleLabel)).append(" - ").append(mArtist).toString();
        }
        setTitle(titleLabel);
        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        loaderLay.setVisibility(View.VISIBLE);
        final SoundFile.ProgressListener listener = fractionComplete -> {
            long now = System.currentTimeMillis();
            if (now - mLoadingLastUpdateTime > 100) {
                mLoadingLastUpdateTime = now;
            }
            return mLoadingKeepGoing;
        };
        mCanSeekAccurately = false;
        new Thread() {
            public void run() {
                mCanSeekAccurately = SeekTest.canSeekAccurately(getPreferences(0));

                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mFile.getAbsolutePath());

                    player.prepare();
                    mPlayer = player;
                } catch (final IOException e) {
                    mHandler.post(() -> handleFatalError("ReadError", getResources().getText(R.string.read_error), e));
                }
            }
        }.start();
        new Thread() {

            class Runn implements Runnable {
                Runn() {
                }

                public void run() {
                    finishOpeningSoundFile();
                }
            }

            public void run() {

                try {
                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(), listener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mSoundFile == null) {
                    String err;
                    loaderLay.setVisibility(View.GONE);
                    flagsong = true;
                    String[] components = mFile.getName().toLowerCase().split("\\.");
                    if (components.length < 2) {
                        err = getResources().getString(R.string.no_extension_error);
                    } else {
                        err = new StringBuilder(String.valueOf(getResources()
                                .getString(R.string.bad_extension_error)))
                                .append(" ").append(components[components.length - 1]).toString();
                    }
                    final String finalErr = err;
                    mHandler.post(() -> handleFatalError("UnsupportedExtension", finalErr, new Exception()));
                    return;
                }
                runOnUiThread(() -> loaderLay.setVisibility(View.GONE));

                flagsong = true;
                if (mLoadingKeepGoing) {
                    mHandler.post(new Runn());
                } else {
                    finish();
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        // Stuff that updates the UI
                        loaderLay.setVisibility(View.GONE);
                    }
                });


                flagsong = true;

//                    mHandler.post(() -> handleFatalError("ReadError", getResources().getText(R.string.read_error), e));


            }
        }.start();
    }

    void finishOpeningSoundFile() {
        mPVMWSWaveformView.setSoundFile(mSoundFile);
        mPVMWSWaveformView.recomputeHeights(mDensity);
        mMaxPos = mPVMWSWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;
        mTouchDragging = false;
        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos) {
            mEndPos = mMaxPos;
        }
        updateDisplay();
        if (isFromItemClick) {
            onPlay(mStartPos);
        }
    }

    synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mPVMWSWaveformView.millisecsToPixels(now);
            mPVMWSWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - (mWidth / 2));
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }
        if (!mTouchDragging) {
            int offsetDelta;
            if (mFlingVelocity != 0) {
                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }
                mOffset += offsetDelta;
                if (mOffset + (mWidth / 2) > mMaxPos) {
                    mOffset = mMaxPos - (mWidth / 2);
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;
                if (offsetDelta > 10) {
                    offsetDelta /= 10;
                } else if (offsetDelta > 0) {
                    offsetDelta = 1;
                } else if (offsetDelta < -10) {
                    offsetDelta /= 10;
                } else if (offsetDelta < 0) {
                    offsetDelta = -1;
                } else {
                    offsetDelta = 0;
                }
                mOffset += offsetDelta;
            }
        }
        mPVMWSWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mPVMWSWaveformView.invalidate();
        mStartMarker.setContentDescription("Start Marker" + " " + formatTime(mStartPos));
        mEndMarker.setContentDescription("End Marker" + " " + formatTime(mEndPos));
        int startX = (mStartPos - mOffset) - mMarkerLeftInset;
        if (mStartMarker.getWidth() + startX < 0) {
            if (mStartVisible) {
                mStartMarker.setAlpha(0);
                mStartVisible = false;
            }
            startX = 0;
        } else if (!mStartVisible) {
            mHandler.postDelayed(() -> {
                mStartVisible = true;
                mStartMarker.setAlpha(255);
            }, 0);
        }
        int endX = ((mEndPos - mOffset) - mEndMarker.getWidth()) + mMarkerRightInset;
        if (mEndMarker.getWidth() + endX < 0) {
            if (mEndVisible) {
                mEndMarker.setAlpha(0);
                mEndVisible = false;
            }
            endX = 0;
        } else if (!mEndVisible) {
            mHandler.postDelayed(() -> {
                mEndVisible = true;
                mEndMarker.setAlpha(255);
            }, 0);
        }
        mStartMarker.setLayoutParams(new LayoutParams(-2, -2, startX, mMarkerTopOffset));
        mEndMarker.setLayoutParams(new LayoutParams(-2, -2, endX, (mPVMWSWaveformView.getMeasuredHeight() - mEndMarker.getHeight()) - mMarkerBottomOffset));
    }

    void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(R.drawable.pause2);
            mPlayButton.setContentDescription("Stop");
            return;
        }
        mPlayButton.setImageResource(R.drawable.play);
        mPlayButton.setContentDescription("Play");
    }

    void resetPositions() {
        mStartPos = mPVMWSWaveformView.secondsToPixels(0.0d);
        mEndPos = mPVMWSWaveformView.secondsToPixels((double) mMaxPos);
    }

    int trap(int pos) {
        if (pos < 0) {
            return 0;
        }
        if (pos > mMaxPos) {
            return mMaxPos;
        }
        return pos;
    }

    void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - (mWidth / 2));
    }

    void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - (mWidth / 2));
    }

    void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - (mWidth / 2));
    }

    void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - (mWidth / 2));
    }

    void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    void setOffsetGoalNoUpdate(int offset) {
        if (!mTouchDragging) {
            mOffsetGoal = offset;
            if (mOffsetGoal + (mWidth / 2) > mMaxPos) {
                mOffsetGoal = mMaxPos - (mWidth / 2);
            }
            if (mOffsetGoal < 0) {
                mOffsetGoal = 0;
            }
        }
    }

    String formatTime(int pixels) {
        if (mPVMWSWaveformView == null || !mPVMWSWaveformView.isInitialized()) {
            return "";
        }
        return formatDecimal(mPVMWSWaveformView.pixelsToSeconds(pixels));
    }

    String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) ((100.0d * (x - ((double) xWhole))) + 0.5d);
        if (xFrac >= 100) {
            xWhole++;
            xFrac -= 100;
            if (xFrac < 10) {
                xFrac *= 10;
            }
        }
        if (xFrac < 10) {
            return new StringBuilder(String.valueOf(xWhole)).append(".0").append(xFrac).toString();
        }
        return new StringBuilder(String.valueOf(xWhole)).append(".").append(xFrac).toString();
    }

    synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mPVMWSWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
        } else if (!(mPlayer == null || startPosition == -1)) {
            try {
                mPlayStartMsec = mPVMWSWaveformView.pixelsToMillisecs(startPosition);
                if (startPosition < mStartPos) {
                    mPlayEndMsec = mPVMWSWaveformView.pixelsToMillisecs(mStartPos);
                } else if (startPosition > mEndPos) {
                    mPlayEndMsec = mPVMWSWaveformView.pixelsToMillisecs(mMaxPos);
                } else {
                    mPlayEndMsec = mPVMWSWaveformView.pixelsToMillisecs(mEndPos);
                }
                mPlayStartOffset = 0;
                int startFrame = mPVMWSWaveformView.secondsToFrames(((double) mPlayStartMsec) * 0.001d);
                int endFrame = mPVMWSWaveformView.secondsToFrames(((double) mPlayEndMsec) * 0.001d);
                int startByte = mSoundFile.getSeekableFrameOffset();
                int endByte = mSoundFile.getSeekableFrameOffset();
                if (mCanSeekAccurately && startByte >= 0 && endByte >= 0) {

                    mPlayer.reset();
                    mPlayer.setDataSource(new FileInputStream(mFile.getAbsolutePath()).getFD(), (long) startByte, (long) (endByte - startByte));
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;

                    mPlayer.reset();
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;

                }
                mPlayer.setOnCompletionListener(new OnCompletionListener() {
                    public synchronized void onCompletion(MediaPlayer arg0) {
                        handlePause();
                    }
                });
                mIsPlaying = true;
                if (mPlayStartOffset == 0) {
                    mPlayer.seekTo(mPlayStartMsec);
                }
                mPlayer.start();
                updateDisplay();
                enableDisableButtons();
            } catch (Exception e2) {
                showFinalAlert(e2, (int) R.string.play_error);
            }
        }
    }

    void showFinalAlert(Exception e, CharSequence message) {
        CharSequence title;
        if (e != null) {
            Log.e("", "Error: " + message);
            Log.e("", getStackTrace(e));
            title = getResources().getText(R.string.alert_title_failure);
            setResult(0, new Intent());
        } else {
            Log.i("Ringdroid", "Success: " + message);
            title = "Success";
        }
        new Builder(this, 0)
                .setTitle(title).setMessage(message).setPositiveButton(R.string.alert_ok_button, (dialog, whichButton) -> finish()).setCancelable(false).show();
    }

    void showFinalAlert(Exception e, int messageResourceId) {
        showFinalAlert(e, getResources().getText(messageResourceId));
    }

    String makeRingtoneFilename(CharSequence title, String extension) {
        FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
        File tempFile = new File(FileUtils.TEMP_DIRECTORY_AUDIO, title + extension);
        if (tempFile.exists()) {
            FileUtils.deleteFile(tempFile);
        }
        return tempFile.getAbsolutePath();
    }

    void saveRingtone(CharSequence title) {
        final String outPath = makeRingtoneFilename(title, mExtension);
        if (outPath == null) {
            showFinalAlert(new Exception(), (int) R.string.no_unique_filename);
            return;
        }
        double startTime = mPVMWSWaveformView.pixelsToSeconds(mStartPos);
        double endTime = mPVMWSWaveformView.pixelsToSeconds(mEndPos);
        final int startFrame = mPVMWSWaveformView.secondsToFrames(startTime);
        final int endFrame = mPVMWSWaveformView.secondsToFrames(endTime);
        final int duration = (int) ((endTime - startTime) + 0.5d);
        loaderLay.setVisibility(View.VISIBLE);
        final CharSequence charSequence = title;
        new Thread() {

            class ProgressListener11 implements SoundFile.ProgressListener {
                ProgressListener11() {
                }

                public boolean reportProgress(double frac) {
                    return true;
                }
            }

            public void run() {
                final File outFile = new File(outPath);
                Log.e("oooooo", "run: " + outPath);
                try {
                    mSoundFile.writeFile(outFile, startFrame, endFrame - startFrame);
                    SoundFile.create(outPath, new ProgressListener11());
                    final String str = outPath;
                    final int i = duration;
                    mHandler.post(() -> afterSavingRingtone(charSequence, str, outFile, i));
                } catch (Exception e) {
                    CharSequence errorMessage;
                    Exception e2 = e;
//                    loaderLay.setVisibility(View.GONE);
                    if (e2.getMessage().equals("No space left on device")) {
                        errorMessage = getResources().getText(R.string.no_space_error);
                        e2 = null;
                    } else {
                        errorMessage = getResources().getText(R.string.write_error);
                    }
                    final CharSequence finalErrorMessage = errorMessage;
                    final Exception finalException = e2;
                    mHandler.post(() -> handleFatalError("WriteError", finalErrorMessage, finalException));
                }
            }
        }.start();
    }

    void afterSavingRingtone(CharSequence title, String outPath, File outFile, int duration) {
        if (outFile.length() <= 512) {
            outFile.delete();
            new Builder(this).setTitle(R.string.alert_title_failure)
                    .setMessage(R.string.too_small_error).setPositiveButton(R.string.alert_ok_button, null).setCancelable(false).show();
            return;
        }
        long fileSize = outFile.length();
        String artist = (String) getResources().getText(R.string.artist_name);
        ContentValues values = new ContentValues();
        values.put("_data", outPath);
        values.put("title", title.toString());
        values.put("_size", Long.valueOf(fileSize));
        values.put("mime_type", "audio/mpeg");
        values.put("artist", artist);
        values.put("duration", Integer.valueOf(duration));
        values.put("is_music", Boolean.valueOf(true));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            collection = MediaStore.Audio.Media.getContentUri(outPath); // added in @api<=29 to get the primary external storage
//            Log.e("111", "afterSavingRingtone: " + "12");
//            // To specify a location instead of the default music directory in external
//            values.put(MediaStore.Audio.Media.RELATIVE_PATH, (Environment.DIRECTORY_MUSIC));
//        } else {
//            // Your Old code
//
//            Log.e("111", "afterSavingRingtone: " + "11");
//            collection = Media.getContentUriForPath(outPath);
//        }
//        Log.e("audio", "duaration is " + collection+"--"+values);
//        Uri itemUri = getContentResolver().insert(collection, values);
//        setResult(-1, new Intent().setData(getContentResolver().insert(collection, values)));
//        setResult(-1, new Intent().setData(getContentResolver().insert(Media.getContentUriForPath(outPath), values)));
        selectedpvmwsMusicData.trackdata = outPath;

        selectedpvmwsMusicData.trackDuration = (long) (duration * 1000);
        MyApplication.getInstance().setMusicData(selectedpvmwsMusicData);
        loaderLay.setVisibility(View.GONE);
        finish();


    }

    void handleFatalError(CharSequence errorInternalName, CharSequence errorString, Exception exception) {
        Log.i("Ringdroid", "handleFatalError");
    }

    void onSave() {
        if (mIsPlaying) {
            handlePause();
        }
        saveRingtone("temp");
    }

    void enableZoomButtons() {
        mZoomInButton.setEnabled(mPVMWSWaveformView.canZoomIn());
        mZoomOutButton.setEnabled(mPVMWSWaveformView.canZoomOut());
    }

    String getStackTrace(Exception e) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(stream, true));
        return stream.toString();
    }

    String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf(46), filename.length());
    }

    public void onBackPressed() {

        if (flagsong) {
            setResult(0);

            if (isPlaying) {
                mPlayer.release();
            }
            flagsong = false;
        }

        super.onBackPressed();

    }


    class Run1 implements Runnable {
        Run1() {
        }

        public void run() {
            if (!(mStartPos == mLastDisplayedStartPos || mStartText.hasFocus())) {
                mStartText.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }
            if (!(mEndPos == mLastDisplayedEndPos || mEndText.hasFocus())) {
                mEndText.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }
            mHandler.postDelayed(mTimerRunnable, 100);
        }
    }


    public class LoadMusics extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();

            loaderLay.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Void... paramVarArgs) {
            mpvmwsMusicData = getMusicFiles();
            if (mpvmwsMusicData.size() > 0) {
                selectedpvmwsMusicData = mpvmwsMusicData.get(0);
                mFilename = selectedpvmwsMusicData.getTrackdata();
            } else {
                mFilename = "record";
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loaderLay.setVisibility(View.GONE);
            if (!mFilename.equals("record")) {
                setUpRecyclerView();

                loadFromFile();
            } else if (mpvmwsMusicData.size() > 0) {
                Toast.makeText(getApplicationContext(),
                        "No Music found in device\nPlease add music in sdCard", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
        SparseBooleanArray booleanArray = new SparseBooleanArray();
        RadioButton mButton;
        int mSelectedChoice = 0;
        List<MusicData> pvmwsMusicData;

        public class Holder extends RecyclerView.ViewHolder {
            TextView musicName;

            public Holder(View v) {
                super(v);
                musicName = (TextView) v.findViewById(R.id.musicName);
            }
        }

        public MusicAdapter(List<MusicData> mpvmwsMusicData) {
            pvmwsMusicData = mpvmwsMusicData;
            booleanArray.put(0, true);
        }

        public Holder onCreateViewHolder(ViewGroup parent, int paramInt) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pvmws_music_list_items, parent, false));
        }

        public void onBindViewHolder(Holder holder, final int pos) {


            holder.musicName.setText(((MusicData) pvmwsMusicData.get(pos)).trackDisplayName);
            holder.musicName.setOnClickListener(arg0 -> {
                booleanArray.clear();
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.reset();
                }
                Log.e("lllll", "onBindViewHolder: "+booleanArray.size() );
                booleanArray.put(pos, true);
                onPlay(-1);
                playMusic(pos);
                isFromItemClick = true;
                notifyDataSetChanged();
            });
        }

        public int getItemCount() {
            return pvmwsMusicData.size();
        }

        public void playMusic(int pos) {
            if (mSelectedChoice != pos) {
                selectedpvmwsMusicData = (MusicData) mpvmwsMusicData.get(pos);
                mFilename = selectedpvmwsMusicData.getTrackdata();
                loadFromFile();
            }
            mSelectedChoice = pos;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void loadBanner() {

        adView = new AdView(this);
        adView.setAdUnitId(new Ads_Preference(this).get_admob_banner_id());
        banner_container.removeAllViews();
        banner_container.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = banner_container.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

}
