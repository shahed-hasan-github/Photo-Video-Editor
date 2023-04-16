package infiapp.com.videomaker.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

import infiapp.com.videomaker.BuildConfig;
import infiapp.com.videomaker.R;


public class UtilImageShare {


    Context context;
    String location;
    int tag;

    public UtilImageShare(Context context, String location, int tag) {
        this.context = context;
        this.location = location;
        this.tag = tag;

        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(location));


        if (tag == 1) {
            shareIntent(uri);
        } else if (tag == 2) {
            shareApp("com.whatsapp", uri);
        }


    }

    public void shareIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share) + context.getPackageName());
        context.startActivity(Intent.createChooser(intent, "Set Status : "));

    }

    public void shareApp(String appName, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "\uD83D\uDE0D *MoJly App* \uD83D\uDE0D - Download Now");
        intent.setPackage(appName);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (Exception e) {

            if (appName.equals("com.whatsapp")) {
                appName = "Whatsapp";
            } else if (appName.equals("com.instagram.android")) {
                appName = "Instagram";
            }

            Toast.makeText(context, "" + appName + " Not Installed. Try Again.", Toast.LENGTH_SHORT).show();
        }

    }


}


