package infiapp.com.videomaker.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import infiapp.com.videomaker.BuildConfig;
import infiapp.com.videomaker.R;


public class UtilsVideoDownload {

    public Context context;
    public String url;
    int isShare = 0;
    String fileName;
    ProgressDialog mProgressDialog;
    String location;
    int tag;
    int quoteId;


    public UtilsVideoDownload(Context context, String url, int isShare, int tag, int quoteId) {

        this.context = context;
        this.url = url;
        this.isShare = isShare;
        this.tag = tag;
        this.quoteId = quoteId;


        fileName = URLUtil.guessFileName(url, null, "video/*");

        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + MyAppUtils.FOLDER_NAME;

        if (!new File(folder).exists()) {

            new File(folder).mkdirs();

        }

        String location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + MyAppUtils.FOLDER_NAME + "/" + fileName;


        if (new File(location).exists()) {


            if (isShare == 1) {

                new UtilImageShare(context, location, tag);


            } else {
                Toast.makeText(context, "Already Downloaded.", Toast.LENGTH_SHORT).show();
            }

        } else {

            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.gredient_dialog));
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            DownloadTask downloadFile = new DownloadTask(context);
            downloadFile.execute(url);

            TextView tv1 = (TextView) mProgressDialog.findViewById(android.R.id.message);
            tv1.setTextSize(16);
            tv1.setTextColor(context.getColor(R.color.black));

        }
    }

    private void scanMedia(String path) {
        File file = new File(path);

        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);

        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {

                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();


                location = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + MyAppUtils.FOLDER_NAME + "/" + fileName;


                File file = new File(location);
                if (!file.exists())
                    file.createNewFile();

                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    ignored.getStackTrace();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();

            if (mProgressDialog != null && !mProgressDialog.isShowing())
                mProgressDialog.show();


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();

            }


            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(context, new String[]{new File(location).getAbsolutePath()},
                            null, (path, uri) -> {
                            });
                } else {

                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(location));

                    context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", uri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            scanMedia(location);


            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show();


            if (isShare == 1) {

                new UtilImageShare(context, location, tag);


            }

        }


    }

}
