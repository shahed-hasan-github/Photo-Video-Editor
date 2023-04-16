package infiapp.com.videomaker.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.LayManager;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.theme.util.FileUtils;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.KSUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import infiapp.com.videomaker.model.ImageData;

public class SelectFrameActivity extends AppCompatActivity {


    Bitmap firstBit;
    Bitmap endBit;
    MyCustomLayoutManager hrlManagaer2;
    MyCustomLayoutManager hrlManagaer;
    StartFrameAdapter sFrmAdapter;
    EndFrameAdapter eFrmAdapter;
    RecyclerView startFrmRecycle;
    RecyclerView endFrmRecycle;
    Bitmap startFrameBitmap;
    Bitmap endFrameBitmap;
    ImageView done;
    ImageView back;
    ImageView startIV;
    ImageView endIV;
    ImageView startframebtn;
    ImageView endframebtn;
    protected static ArrayList<ImageData> arrayListmain;
    MyApplication application;
    int posstart;
    ImageView imgBtnYes;
    ImageView imgBtnNo;
    LinearLayout maindailog;
    RelativeLayout header;
    RelativeLayout footer;
    LinearLayout tabLay;
    ArrayList<String> allPath;

    LinearLayout banner_container;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decorefrm);

        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        init();

        arrayListmain = application.getSelectedImages();

        posstart = 0;


        sFrmAdapter = new StartFrameAdapter();
        eFrmAdapter = new EndFrameAdapter();

        hrlManagaer = new MyCustomLayoutManager(getApplicationContext());
        startFrmRecycle.setLayoutManager(hrlManagaer);

        hrlManagaer2 = new MyCustomLayoutManager(getApplicationContext());
        endFrmRecycle.setLayoutManager(hrlManagaer2);

        startFrmRecycle.setAdapter(sFrmAdapter);
        endFrmRecycle.setAdapter(eFrmAdapter);

        back.setOnClickListener(v -> onBackPressed());

        startIV.setVisibility(View.VISIBLE);
        startFrmRecycle.setVisibility(View.VISIBLE);

        firstBit = application.loadBitmapFromAssets(getApplicationContext(), "startframe/" +
                MyApplication.startframelist[0]);
        startIV.setImageBitmap(firstBit);


        endBit = application.loadBitmapFromAssets(getApplicationContext(), "endframe/"
                + MyApplication.endframelist[0]);
        endIV.setImageBitmap(endBit);


        done.setOnClickListener(v -> open());

        startframebtn.setOnClickListener(v -> {
            endFrmRecycle.setVisibility(View.GONE);
            startIV.setVisibility(View.VISIBLE);
            firstBit = application.loadBitmapFromAssets(getApplicationContext(), "startframe/" +
                    MyApplication.startframelist[posstart]);
            startIV.setImageBitmap(firstBit);
            endIV.setVisibility(View.GONE);
            if (startFrmRecycle.getVisibility() == View.VISIBLE) {
                startFrmRecycle.setVisibility(View.GONE);
                startframebtn.setImageResource(R.drawable.start_unpress);

            } else {
                startFrmRecycle.setVisibility(View.VISIBLE);
                startframebtn.setImageResource(R.drawable.start_press);
                endframebtn.setImageResource(R.drawable.end_unpress);
            }
        });

        endframebtn.setOnClickListener(v -> {
            startFrmRecycle.setVisibility(View.GONE);
            startIV.setVisibility(View.GONE);
            endBit = application.loadBitmapFromAssets(getApplicationContext(), "endframe/"
                    + MyApplication.endframelist[posstart]);
            endIV.setImageBitmap(endBit);
            endIV.setVisibility(View.VISIBLE);
            if (endFrmRecycle.getVisibility() == View.VISIBLE) {
                endFrmRecycle.setVisibility(View.GONE);
                endframebtn.setImageResource(R.drawable.end_unpress);

            } else {
                endFrmRecycle.setVisibility(View.VISIBLE);
                endframebtn.setImageResource(R.drawable.end_press);
                startframebtn.setImageResource(R.drawable.start_unpress);

            }
        });


        setLTV();
    }

    private void init() {

        this.application = MyApplication.getInstance();
        application.isEditEnable = true;
        allPath = new ArrayList<>();
        allPath.addAll(KSUtil.videoPathList);
        header = findViewById(R.id.topbar);
        tabLay = findViewById(R.id.tabLay);
        footer = findViewById(R.id.footer);
        startFrmRecycle = findViewById(R.id.rvstartframerecycle);
        endFrmRecycle = findViewById(R.id.rvendframerecycle);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        startIV = findViewById(R.id.startiv);
        endIV = findViewById(R.id.endiv);
        startframebtn = findViewById(R.id.startframebtn);
        endframebtn = findViewById(R.id.endframebtn);
    }



    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    public ImageData getItem(int pos) {
        ArrayList<ImageData> list = application.getSelectedImagesstart();
        if (list.size() <= pos) {
            return new ImageData();
        }
        return list.get(pos);
    }


    class StartFrameAdapter extends RecyclerView.Adapter<StartFrameAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            RelativeLayout relend;

            public MyViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.framerow_img);
                this.relend = (RelativeLayout) view.findViewById(R.id.framerow_bg);
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
                relend.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
            }
        }

        StartFrameAdapter() {
        }

        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.framerow_item,
                    viewGroup, false));
        }

        public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

            InputStream open;
            try {
                AssetManager assets = getAssets();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("startframe/");
                stringBuilder.append(MyApplication.startframelist[i]);
                open = assets.open(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
                open = null;
            }

            myViewHolder.imageView.setImageBitmap(BitmapFactory.decodeStream(open));
            myViewHolder.relend.setBackgroundColor(ContextCompat.getColor(SelectFrameActivity.this,
                    R.color.colorwhite));

            myViewHolder.imageView.setOnClickListener(view -> {

                try {

                    firstBit = application.loadBitmapFromAssets(getApplicationContext(), "startframe/" +
                            MyApplication.startframelist[i]);
                    posstart = i;

                    startIV.setImageBitmap(firstBit);


                } catch (Exception e) {
//dosomething
                }
            });
        }

        public int getItemCount() {
            return MyApplication.startframelist.length;
        }

    }

    class EndFrameAdapter extends RecyclerView.Adapter<EndFrameAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            RelativeLayout relend;

            public MyViewHolder(View view) {
                super(view);
                this.imageView = (ImageView) view.findViewById(R.id.framerow_img);
                this.relend = (RelativeLayout) view.findViewById(R.id.framerow_bg);
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
                relend.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 266 / 1080,
                        getResources().getDisplayMetrics().widthPixels * 266 / 1080));
            }
        }

        EndFrameAdapter() {


        }

        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.framerow_item, viewGroup, false));
        }

        public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
            InputStream open;
            try {
                AssetManager assets = getAssets();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("endframe/");
                stringBuilder.append(MyApplication.endframelist[i]);
                open = assets.open(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
                open = null;
            }

            myViewHolder.imageView.setImageBitmap(BitmapFactory.decodeStream(open));
            myViewHolder.relend.setBackgroundColor(ContextCompat.getColor(SelectFrameActivity.this,
                    R.color.colorwhite));

            myViewHolder.imageView.setOnClickListener(view -> {
                try {
                    endBit = application.loadBitmapFromAssets(getApplicationContext(), "endframe/"
                            + MyApplication.endframelist[i]);


                    posstart = i;
                    endIV.setImageBitmap(endBit);


                } catch (Exception e) {
//dosomething
                }
            });
        }

        public int getItemCount() {
            return MyApplication.endframelist.length;
        }

    }


    public class MyCustomLayoutManager extends LinearLayoutManager {

        private Context mContext;

        public MyCustomLayoutManager(Context context) {
            super(context);
            mContext = context;
        }

        public void setOrientation(int i) {
            super.setOrientation(RecyclerView.HORIZONTAL);
        }

        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {

            LinearSmoothScroller recyclerView1 = new LinearSmoothScroller(mContext) {
                public PointF computeScrollVectorForPosition(int i) {
                    return computeScrollVectorForPosition(i);
                }

                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return (350.0f / ((float) displayMetrics.densityDpi));
                }
            };
            recyclerView1.setTargetPosition(i);
        }
    }

    public void open() {

        final Dialog dDelete = new Dialog(SelectFrameActivity.this);
        dDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dDelete.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        dDelete.setContentView(R.layout.dialog_delete);

        TextView maintext = (TextView) dDelete.findViewById(R.id.maintext);
        imgBtnYes = (ImageView) dDelete.findViewById(R.id.imgBtnYes);
        imgBtnNo = (ImageView) dDelete.findViewById(R.id.imgBtnNo);
        maindailog = (LinearLayout) dDelete.findViewById(R.id.maindailog);


        popuplayoutDialog();

        maintext.setText(getResources().getString(R.string.back_msg));
        imgBtnYes.setOnClickListener(arg0 -> {

            startFrameBitmap = application.loadBitmapFromAssets(getApplicationContext(),
                    "startframe/" + application.startframelist[posstart]);


            endFrameBitmap = application.loadBitmapFromAssets(getApplicationContext(),
                    "endframe/" + application.endframelist[posstart]);

            new SaveAsync().execute();
            dDelete.dismiss();
        });
        imgBtnNo.setOnClickListener(arg0 -> {
            application.isEditEnable = false;

            Intent i2 = new Intent(SelectFrameActivity.this, VideoThemeActivity.class);
            startActivityForResult(i2,0);

            dDelete.dismiss();
        });
        dDelete.show();

    }

    File resultingfile;

    String startFramePath;
    String endFramePath;

    class SaveAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SelectFrameActivity.this);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            startFramePath = saver(startFrameBitmap);
            Log.e("startFramePath", startFramePath);
            endFramePath = saver(endFrameBitmap);
            Log.e("endFramePath", endFramePath);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            KSUtil.videoPathList.clear();
            KSUtil.videoPathList.add(startFramePath);


            for (String str : allPath) {
                KSUtil.videoPathList.add(str);
                Log.e("allPath", str);
            }

            KSUtil.videoPathList.add(endFramePath);

            application.isEditEnable = false;
            application.selectedImages.clear();

            for (int i = 0; i < KSUtil.videoPathList.size(); i++) {
                ImageData idata = new ImageData();
                idata.setImagePath(KSUtil.videoPathList.get(i));
                application.selectedImages.add(i, idata);
            }
            Intent i2 = new Intent(SelectFrameActivity.this, VideoThemeActivity.class);
            startActivityForResult(i2,0);

        }
    }


    public String saver(Bitmap bitmap) {
        String folder = getResources().getString(R.string.app_name);
        try {
            File rootFile = new File(FileUtils.filepath1 + "/" + folder + "/editor");
            rootFile.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";

            resultingfile = new File(rootFile, fname);

            if (resultingfile.exists())
                resultingfile.delete();

            FileOutputStream fOut = new FileOutputStream(resultingfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();


        } catch (Exception e) {
            //dosomething
        }
        return resultingfile.getAbsolutePath();
    }


    void setLTV() {

        RelativeLayout.LayoutParams pBtn = LayManager.relParams(SelectFrameActivity.this, 90, 90);
        pBtn.addRule(RelativeLayout.CENTER_IN_PARENT);
        back.setLayoutParams(pBtn);
        done.setLayoutParams(pBtn);

        RelativeLayout.LayoutParams pHeader = LayManager.relParams(SelectFrameActivity.this, 1080, 168);
        header.setLayoutParams(pHeader);


        LinearLayout.LayoutParams paramsTab = LayManager.linParams(SelectFrameActivity.this, 1080, 82);
        tabLay.setLayoutParams(paramsTab);


        RelativeLayout.LayoutParams pFooter = LayManager.relParams(SelectFrameActivity.this, 1080, 340);
        footer.setLayoutParams(pFooter);

    }

    void popuplayoutDialog() {

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 270 / 1080,
                getResources().getDisplayMetrics().heightPixels * 106 / 1920);
        params4.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgBtnYes.setLayoutParams(params4);
        imgBtnNo.setLayoutParams(params4);

        RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                getResources().getDisplayMetrics().widthPixels * 840 / 1080,
                getResources().getDisplayMetrics().heightPixels * 415 / 1920);
        params5.addRule(RelativeLayout.CENTER_IN_PARENT);
        maindailog.setLayoutParams(params5);
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
