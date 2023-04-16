package infiapp.com.videomaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import infiapp.com.videomaker.activity.VideoEditorActivity;
import infiapp.com.videomaker.R;


public class ImageListAdapter extends Adapter<ImageListAdapter.MyViewHolder> {
    Context context;
    VideoEditorActivity.ClickAdapter clickAdapter1;
    String[] tempImgUrl;
    int videoArr = 0;

    public ImageListAdapter(Context context, int i, String[] strArr, VideoEditorActivity.ClickAdapter clickAdapter) {
        this.context = context;
        this.videoArr = i;
        this.tempImgUrl = strArr;
        this.clickAdapter1 = clickAdapter;
    }

    public int getItemCount() {
        return this.videoArr;
    }

    @RequiresApi(api = 16)
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {


        Glide.with(context).load(new File(this.tempImgUrl[i])).into(myViewHolder.imageView);

        myViewHolder.imageView.setOnClickListener(view -> ImageListAdapter.this.clickAdapter1.clickEvent(view, i));
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_img_list_, viewGroup, false));
    }

    public static class MyViewHolder extends ViewHolder {
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.gallery);
        }
    }
}
