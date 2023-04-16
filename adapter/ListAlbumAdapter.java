package infiapp.com.videomaker.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import infiapp.com.videomaker.activity.ImagePickerActivity;
import infiapp.com.videomaker.interfaces.OnListAlbum;
import infiapp.com.videomaker.R;
import infiapp.com.videomaker.model.ImageModel;

import java.util.ArrayList;


public class ListAlbumAdapter extends ArrayAdapter<ImageModel> {
    Context context;
    ArrayList<ImageModel> data;
    int layoutResourceId;
    OnListAlbum onListAlbum;
    boolean counter;

    static class RecordHolder {
        ImageView click;
        ImageView imageItem;
        RelativeLayout layoutRoot;

        RecordHolder() {
        }
    }

    public ListAlbumAdapter(Context context, int layoutResourceId, ArrayList<ImageModel> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        counter = true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final RecordHolder holder;
        View row = convertView;
        if (row == null) {
            row = ((Activity) this.context).getLayoutInflater().inflate(this.layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.imageItem = row.findViewById(R.id.imageItem);
            holder.click = row.findViewById(R.id.click);
            holder.layoutRoot = row.findViewById(R.id.layoutRoot);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final ImageModel item = this.data.get(position);
        Glide.with(context).load(item.getPathFile()).asBitmap().placeholder(R.drawable.piclist_icon_default).into(holder.imageItem);

        holder.click.setVisibility(View.GONE);
        if (ImagePickerActivity.listItemSelect.size() > 0) {
            for (int i = 0; i < ImagePickerActivity.listItemSelect.size(); i++) {
                ImageModel item1 = ImagePickerActivity.listItemSelect.get(i);
                if (item1.getPathFile().equals(item.getPathFile())) {
                    holder.click.setVisibility(View.VISIBLE);
                }
            }
        }

        if (ImagePickerActivity.listItemSelect.size() < ImagePickerActivity.limitImageMax) {
            counter = true;
        }
        row.setOnClickListener(v -> {
            if (ListAlbumAdapter.this.onListAlbum != null) {
                ListAlbumAdapter.this.onListAlbum.OnItemListAlbumClick(item);
            }

            if (counter) {
                holder.click.setVisibility(View.VISIBLE);
            }
            if (ImagePickerActivity.listItemSelect.size() == ImagePickerActivity.limitImageMax) {
                counter = false;
            }
        });
        return row;
    }

    public OnListAlbum getOnListAlbum() {
        return this.onListAlbum;
    }

    public void setOnListAlbum(OnListAlbum onListAlbum) {
        this.onListAlbum = onListAlbum;
    }

    public static DisplayMetrics getDisplayInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}

