package infiapp.com.videomaker.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import infiapp.com.videomaker.activity.SwapImageActivity;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.util.KSUtil;

import java.io.File;
import java.util.ArrayList;

public class SwapperAdapter extends AbsBaseGridAdapter {
    
    Context context;
    int width;
    View grid;

    public SwapperAdapter(Context context, ArrayList<String> pathList, int columnCount) {
        super(context, pathList, columnCount);
        this.context = context;
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels; // width of the device
        width = screenWidth / 2;

    }

    @Override
    public int getCount() {
        return KSUtil.videoPathList.size();
    }

    @Override
    public String getItem(int position) {
        return KSUtil.videoPathList.get(position);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = new View(context);
        if (convertView == null) {
            grid = inflater.inflate(R.layout.rearrange_item_grid, null);

        } else {
            grid = convertView;
        }

        grid = inflater.inflate(R.layout.rearrange_item_grid, null);


        ImageView imageView = (ImageView) grid
                .findViewById(R.id.item_img);


        ImageView delete = (ImageView) grid
                .findViewById(R.id.delete);
        delete.setOnClickListener(v -> {

            File file = new File(KSUtil.videoPathList.get(position));
            if (file.exists()){
                file.delete();
            }
            KSUtil.videoPathList.remove(position);
            notifyDataSetChanged();
        });

        ImageView edit = (ImageView) grid
                .findViewById(R.id.edit);
        edit.setOnClickListener(v -> ((SwapImageActivity)context).editorIntent(position,getItem(position)));


        Glide.with(context).load(getItem(position))
                .into(imageView);
        return grid;
    }


    public void refreshlist() {
        notifyDataSetChanged();
    }

}