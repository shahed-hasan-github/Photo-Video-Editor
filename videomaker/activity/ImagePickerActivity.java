package infiapp.com.videomaker.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import infiapp.com.videomaker.adapter.AlbumAdapter;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.adapter.ListAlbumAdapter;
import infiapp.com.videomaker.model.ImageModel;
import infiapp.com.videomaker.interfaces.IHandler;
import infiapp.com.videomaker.interfaces.OnAlbum;
import infiapp.com.videomaker.interfaces.OnListAlbum;
import infiapp.com.videomaker.util.Ads_Preference;
import infiapp.com.videomaker.util.Constants;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImagePickerActivity extends AppCompatActivity implements OnClickListener, OnAlbum, OnListAlbum {
    public static final String KEY_DATA_RESULT = "KEY_DATA_RESULT";
    public static final String KEY_LIMIT_MAX_IMAGE = "KEY_LIMIT_MAX_IMAGE";
    public static final String KEY_LIMIT_MIN_IMAGE = "KEY_LIMIT_MIN_IMAGE";
    public static final int PICKER_REQUEST_CODE = 1001;
    AlbumAdapter albumAdapter;
    ArrayList<ImageModel> dataAlbum = new ArrayList();
    ArrayList<ImageModel> dataListPhoto = new ArrayList();
    GridView gridViewAlbum;
    GridView gridViewListAlbum;
    HorizontalScrollView horizontalScrollView;
    LinearLayout layoutListItemSelect;
    public static int limitImageMax = 30;
    int limitImageMin = 2;
    ListAlbumAdapter listAlbumAdapter;
    public static ArrayList<ImageModel> listItemSelect;
    int pWHBtnDelete;
    int pWHItemSelected;
    ArrayList<String> pathList;
    AlertDialog sortDialog;
    TextView txtTotalImage;
    private Handler mHandler;
    private ProgressDialog pd;
    private int position = 0;
    private static final int READ_STORAGE_CODE = 1001;
    private static final int WRITE_STORAGE_CODE = 1002;

    LinearLayout banner_container;
    private AdView adView;
    ProgressBar progress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        progress=findViewById(R.id.progress);

        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new GetItemAlbum().execute();

        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_CODE);
        }

        banner_container = findViewById(R.id.banner_container);
        banner_container.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        listItemSelect = new ArrayList<>();
        pathList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            limitImageMax = bundle.getInt(KEY_LIMIT_MAX_IMAGE, 30);
            this.limitImageMin = bundle.getInt(KEY_LIMIT_MIN_IMAGE, 2);
            if (this.limitImageMin > limitImageMax) {
                finish();
            }
            if (this.limitImageMin < 1) {
                finish();
            }
        }
        this.pWHItemSelected = (((int) (((getDisplayInfo(this).heightPixels) / 100.0f) * 25.0f)) / 100) * 80;
        this.pWHBtnDelete = (this.pWHItemSelected / 100) * 25;

        btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(v -> onBackPressed());

        header = (RelativeLayout) findViewById(R.id.header);

        footer = (LinearLayout) findViewById(R.id.footer);


        this.gridViewListAlbum = (GridView) findViewById(R.id.gridViewListAlbum);
        this.txtTotalImage = (TextView) findViewById(R.id.txtTotalImage);

        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this);

        this.layoutListItemSelect = (LinearLayout) findViewById(R.id.layoutListItemSelect);
        this.horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        this.horizontalScrollView.getLayoutParams().height = this.pWHItemSelected;
        this.gridViewAlbum = (GridView) findViewById(R.id.gridViewAlbum);


        pd = new ProgressDialog(ImagePickerActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        };

        try {
            Collections.sort(this.dataAlbum, (lhs, rhs) -> lhs.getName().compareToIgnoreCase(rhs.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.albumAdapter = new AlbumAdapter(this, R.layout.piclist_row_album, this.dataAlbum);
        this.albumAdapter.setOnItem(this);

        updateTxtTotalImage();


    }


    private class GetItemAlbum extends AsyncTask<Void, Void, String> {
        private GetItemAlbum() {
        }


        protected String doInBackground(Void... params) {

            ArrayList<String> picPaths = new ArrayList<>();
            Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID};
            Cursor cursor = ImagePickerActivity.this.getContentResolver().query(allImagesuri, projection, null, null, null);
            int columnIndexData = cursor.getColumnIndexOrThrow("_data");
            try {
                if (cursor != null) {
                    cursor.moveToFirst();

                }
                do {

                    String pathFile = cursor.getString(columnIndexData);

                    ImageModel imageModel1 = new ImageModel();
                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                    String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
                    folderpaths = folderpaths + folder + "/";

                    if (!picPaths.contains(folderpaths)) {
                        picPaths.add(folderpaths);
                        imageModel1.setPathFile(pathFile);
                        imageModel1.setPathFolder(folderpaths);
                        imageModel1.setName(folder);
                        dataAlbum.add(imageModel1);
                    }

                } while (cursor.moveToNext());
                cursor.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            ImagePickerActivity.this.gridViewAlbum.setAdapter(ImagePickerActivity.this.albumAdapter);
            showListAlbum(( ImagePickerActivity.this.dataAlbum.get(0)).getPathFolder());
        }

        @Override
        protected void onPreExecute() {
            //Do Something
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Do Something
        }
    }

    private class GetItemListAlbum extends AsyncTask<Void, Void, String> {
        String pathAlbum;

        GetItemListAlbum(String pathAlbum) {
            this.pathAlbum = pathAlbum;
        }

        protected String doInBackground(Void... params) {


            File file = new File(this.pathAlbum);
            if (file.isDirectory()) {
                for (File fileTmp : file.listFiles()) {
                    if (fileTmp.exists()) {
                        boolean check = ImagePickerActivity.this.checkFile(fileTmp);
                        if (!fileTmp.isDirectory() && check) {
                            ImagePickerActivity.this.dataListPhoto.add(new ImageModel(fileTmp.getName(), fileTmp.getAbsolutePath(), fileTmp.getAbsolutePath()));
                            publishProgress(new Void[0]);
                        }
                    }
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                Collections.sort(ImagePickerActivity.this.dataListPhoto, new Comparator<ImageModel>() {
                    @Override
                    public int compare(ImageModel item, ImageModel t1) {
                        File fileI = new File(item.getPathFolder());
                        File fileJ = new File(t1.getPathFolder());
                        if (fileI.lastModified() > fileJ.lastModified()) {
                            return -1;
                        }
                        if (fileI.lastModified() < fileJ.lastModified()) {
                            return 1;
                        }
                        return 0;
                    }
                });
            } catch (Exception e) {
                //Do Something
            }
            ImagePickerActivity.this.listAlbumAdapter.notifyDataSetChanged();
            progress.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            //Do Something
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Do Something
        }
    }

    ImageView btnBack;
    ImageView btnDone;
    RelativeLayout header;
    LinearLayout footer;



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isPermissionGranted(String permission) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, permission);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
        {return true;}

        //If permission is not granted returning false
        return false;
    }


    //Requesting permission
    private void requestPermission(String permission, int code) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{permission}, code);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == READ_STORAGE_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new GetItemAlbum().execute();
            } else {
                ImagePickerActivity.this.finish();
            }
        } else if (requestCode == WRITE_STORAGE_CODE) {


            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                //Do Something
            }
        }
    }

    private boolean check(String a, ArrayList<String> list) {
        if (!list.isEmpty() && list.contains(a)) {
            return true;
        }
        return false;
    }

    public void showDialogSortAlbum() {
        CharSequence[] items = getResources().getStringArray(R.array.array_sort_value);
        final Builder builder = new Builder(this);
        builder.setTitle(getResources().getString(R.string.text_title_dialog_sort_by_album));
        Log.e("TAG", "showDialogSortAlbum");
        builder.setSingleChoiceItems(items, position, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    position = i;
                    Collections.sort(ImagePickerActivity.this.dataAlbum, new Comparator<ImageModel>() {
                        @Override
                        public int compare(ImageModel lhs, ImageModel rhs) {
                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                        }
                    });
                    ImagePickerActivity.this.refreshGridViewAlbum();
                    Log.e("TAG", "showDialogSortAlbum by NAME");
                    break;
                case 1:
                    position = i;
                    doinBackground();
                    Log.e("TAG", "showDialogSortAlbum by Size");
                    break;
                case 2:
                    position = i;
                    Collections.sort(ImagePickerActivity.this.dataAlbum, new Comparator<ImageModel>() {
                        @Override
                        public int compare(ImageModel lhs, ImageModel rhs) {
                            File fileI = new File(lhs.getPathFolder());
                            File fileJ = new File(rhs.getPathFolder());
                            long totalSizeFileI = ImagePickerActivity.getFolderSize(fileI);
                            long totalSizeFileJ = ImagePickerActivity.getFolderSize(fileJ);
                            if (totalSizeFileI > totalSizeFileJ) {
                                return -1;
                            }
                            if (totalSizeFileI < totalSizeFileJ) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    ImagePickerActivity.this.refreshGridViewAlbum();
                    Log.e("TAG", "showDialogSortAlbum by Date");
                    break;
                default:
                    break;
            }
            ImagePickerActivity.this.sortDialog.dismiss();
        });
        this.sortDialog = builder.create();
        this.sortDialog.show();
    }

    public void refreshGridViewAlbum() {
        this.albumAdapter = new AlbumAdapter(this, R.layout.piclist_row_album, this.dataAlbum);
        this.albumAdapter.setOnItem(this);
        this.gridViewAlbum.setAdapter(this.albumAdapter);
        this.gridViewAlbum.setVisibility(View.GONE);
        this.gridViewAlbum.setVisibility(View.VISIBLE);
    }

    public void showDialogSortListAlbum() {
        CharSequence[] items = getResources().getStringArray(R.array.array_sort_value);
        Builder builder = new Builder(this);
        builder.setTitle(getResources().getString(R.string.text_title_dialog_sort_by_photo));
        builder.setSingleChoiceItems(items, position, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    position = i;
                    doinBackgroundPhoto(i);
                case 1:
                    position = i;
                    doinBackgroundPhoto(i);
                case 2:
                    position = i;
                    doinBackgroundPhoto(i);
            }
            ImagePickerActivity.this.sortDialog.dismiss();
        });
        this.sortDialog = builder.create();
        this.sortDialog.show();
    }

    public void refreshGridViewListAlbum() {
        this.listAlbumAdapter = new ListAlbumAdapter(this, R.layout.piclist_row_list_album, this.dataListPhoto);
        this.listAlbumAdapter.setOnListAlbum(this);
        this.gridViewListAlbum.setAdapter(this.listAlbumAdapter);
        this.gridViewListAlbum.setVisibility(View.GONE);
        this.gridViewListAlbum.setVisibility(View.VISIBLE);
    }

    public static long getFolderSize(File directory) {
        long length = 0;
        if (directory == null) {
            return 0;
        }
        if (!directory.exists()) {
            return 0;
        }
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    boolean isCheck = false;
                    for (int k = 0; k < Constants.FORMAT_IMAGE.size(); k++) {
                        if (file.getName().endsWith(Constants.FORMAT_IMAGE.get(k))) {
                            isCheck = true;
                            break;
                        }
                    }
                    if (isCheck) {
                        length++;
                    }
                }
            }
        }
        return length;
    }


    void addItemSelect(final ImageModel item) {
        item.setId(listItemSelect.size());
        listItemSelect.add(item);
        updateTxtTotalImage();
        final View viewItemSelected = View.inflate(this, R.layout.piclist_item_selected, null);
        ImageView imageItem = viewItemSelected.findViewById(R.id.imageItem);
        ImageView btnDelete = viewItemSelected.findViewById(R.id.btnDelete);

        imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(item.getPathFile()).asBitmap().placeholder(R.drawable.piclist_icon_default).into(imageItem);


        btnDelete.setOnClickListener(v -> {
            ImagePickerActivity.this.layoutListItemSelect.removeView(viewItemSelected);
            ImagePickerActivity.this.listItemSelect.remove(item);
            ImagePickerActivity.this.updateTxtTotalImage();
            listAlbumAdapter.notifyDataSetChanged();
        });

        ImagePickerActivity.this.layoutListItemSelect.addView(viewItemSelected);
        viewItemSelected.startAnimation(AnimationUtils.loadAnimation(ImagePickerActivity.this, R.anim.abc_fade_in));
        ImagePickerActivity.this.sendScroll();

    }


    void updateTxtTotalImage() {

        this.txtTotalImage.setText(String.format(getResources().getString(R.string.text_images),new Object[]{Integer.valueOf(this.listItemSelect.size())}));
    }

    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(() -> handler.post(new Runnable() {
            @Override
            public void run() {
                ImagePickerActivity.this.horizontalScrollView.fullScroll(66);
            }
        })).start();
    }

    void showListAlbum(String pathAlbum) {
        this.listAlbumAdapter = new ListAlbumAdapter(this, R.layout.piclist_row_list_album, this.dataListPhoto);
        this.listAlbumAdapter.setOnListAlbum(this);
        this.gridViewListAlbum.setAdapter(this.listAlbumAdapter);
        this.gridViewListAlbum.setVisibility(View.VISIBLE);
        new GetItemListAlbum(pathAlbum).execute();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnDone) {
            ArrayList<String> listString = getListString(this.listItemSelect);
            if (listString.size() >= this.limitImageMin) {
                done(listString);
            } else {
                Toast.makeText(this, "Please select at lease " + this.limitImageMin + " images", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void done(ArrayList<String> listString) {
        Intent mIntent = new Intent();
        setResult(Activity.RESULT_OK, mIntent);
        mIntent.putStringArrayListExtra(KEY_DATA_RESULT, listString);
        finish();
    }

    ArrayList<String> getListString(ArrayList<ImageModel> listItemSelect) {
        ArrayList<String> listString = new ArrayList();
        for (int i = 0; i < listItemSelect.size(); i++) {
            listString.add(( listItemSelect.get(i)).getPathFile());
        }
        return listString;
    }

    private boolean checkFile(File file) {
        if (file == null) {
            return false;
        }
        if (!file.isFile()) {
            return true;
        }
        String name = file.getName();
        if (name.startsWith(".") || file.length() == 0) {
            return false;
        }
        boolean isCheck = false;
        for (int k = 0; k < Constants.FORMAT_IMAGE.size(); k++) {
            if (name.endsWith(Constants.FORMAT_IMAGE.get(k))) {
                isCheck = true;
                break;
            }
        }
        return isCheck;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static DisplayMetrics getDisplayInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }


    public void handlerDoWork(IHandler mIHandler) {
        mHandler.sendMessage(this.mHandler.obtainMessage(0, mIHandler));
    }

    private void doinBackgroundPhoto(final int position) {

        new AsyncTask<String, String, Void>() {
            @Override
            protected void onPreExecute() {
                pd.show();
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(String... strings) {

                if (position == 0) {
                    try {
                        Collections.sort(ImagePickerActivity.this.dataListPhoto, (lhs, rhs) -> lhs.getName().compareToIgnoreCase(rhs.getName()));

                    } catch (Exception e) {

                        //Do Something
                    }
                } else if (position == 1) {
                    Collections.sort(ImagePickerActivity.this.dataListPhoto, (lhs, rhs) -> {
                        File fileI = new File(lhs.getPathFolder());
                        File fileJ = new File(rhs.getPathFolder());
                        long totalSizeFileI = ImagePickerActivity.getFolderSize(fileI);
                        long totalSizeFileJ = ImagePickerActivity.getFolderSize(fileJ);
                        if (totalSizeFileI > totalSizeFileJ) {
                            return -1;
                        }
                        if (totalSizeFileI < totalSizeFileJ) {
                            return 1;
                        }
                        return 0;
                    });
                } else if (position == 2) {
                    try {
                        Collections.sort(ImagePickerActivity.this.dataListPhoto, (lhs, rhs) -> {
                            File fileI = new File(lhs.getPathFolder());
                            File fileJ = new File(rhs.getPathFolder());
                            if (fileI.lastModified() > fileJ.lastModified()) {
                                return -1;
                            }
                            if (fileI.lastModified() < fileJ.lastModified()) {
                                return 1;
                            }
                            return 0;
                        });
                    } catch (Exception e3) {
                        //Do Something
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ImagePickerActivity.this.refreshGridViewListAlbum();
                pd.dismiss();
            }
        }.execute();
    }

    private void doinBackground() {

        new AsyncTask<String, String, Void>() {
            @Override
            protected void onPreExecute() {
                pd.show();
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(String... strings) {
                Collections.sort(ImagePickerActivity.this.dataAlbum, (item, t1) -> {
                    File fileI = new File(item.getPathFolder());
                    File fileJ = new File(t1.getPathFolder());
                    if (fileI.lastModified() > fileJ.lastModified()) {
                        return -1;
                    }
                    if (fileI.lastModified() < fileJ.lastModified()) {
                        return 1;
                    }
                    return 0;
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ImagePickerActivity.this.refreshGridViewAlbum();
                pd.dismiss();
            }
        }.execute();
    }

    public void OnItemAlbumClick(int position) {
        dataListPhoto.clear();
        showListAlbum(( this.dataAlbum.get(position)).getPathFolder());
    }

    public void OnItemListAlbumClick(ImageModel item) {
        if (listItemSelect.size() < limitImageMax) {
            addItemSelect(item);
        } else {
            Toast.makeText(this, "Limit " + limitImageMax + " images", Toast.LENGTH_SHORT).show();
        }
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