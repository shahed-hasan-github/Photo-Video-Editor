package infiapp.com.videomaker.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import infiapp.com.videomaker.R;
import infiapp.com.videomaker.interfaces.CustomItemClickListener;

import java.io.File;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.FileHolder> {

    List<String> videoFiles;
    Context context;
    int width;
    int height;
    CustomItemClickListener listener;

    public VideoAdapter(List<String> fileList, Context context, CustomItemClickListener listener) {
        this.videoFiles = fileList;
        this.context = context;
        this.listener = listener;
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        width = screenWidth;
        height = screenHeight;
    }

    public class FileHolder extends RecyclerView.ViewHolder {

        ImageView videoThumb;
        ImageView share;
        ImageView delete;
        ImageView play;
        RelativeLayout videoLay;
        RelativeLayout lay;

        public FileHolder(View itemView) {
            super(itemView);
            videoThumb = itemView.findViewById(R.id.videoThumbIV);
            share = itemView.findViewById(R.id.share);
            delete = itemView.findViewById(R.id.delete);
            videoLay = itemView.findViewById(R.id.videoLay);
            play = itemView.findViewById(R.id.play);
            lay = itemView.findViewById(R.id.lay);

        }
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myvideolay, parent, false);

        return new FileHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, final int position) {
        Glide.with(context).load(videoFiles.get(position)).override(500, 500)
                .into(holder.videoThumb);

        holder.itemView.setOnClickListener((View v) ->
                        listener.onItemClick(v, position)
                //do something
        );


        holder.share.setOnClickListener((View v) ->

                        share(videoFiles.get(position))
                //do something
        );

        holder.delete.setOnClickListener((View v) -> {
            new File(videoFiles.get(position)).delete();
            delete(position);
            Toast.makeText(context, "Delete Successfully!!!", Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public void delete(int position) {
        videoFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videoFiles.size());
    }

    void share(String path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("video/*");
        Uri photoURI = FileProvider.getUriForFile(
                context.getApplicationContext(),
                context.getApplicationContext()
                        .getPackageName() + ".provider", new File(path));
        share.putExtra(Intent.EXTRA_STREAM,
                photoURI);
        context.startActivity(Intent.createChooser(share, "Share via"));
    }
}
