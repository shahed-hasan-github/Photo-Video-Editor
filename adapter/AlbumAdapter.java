package infiapp.com.videomaker.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import infiapp.com.videomaker.R;
import infiapp.com.videomaker.interfaces.OnAlbum;
import infiapp.com.videomaker.model.ImageModel;

import java.io.File;
import java.util.ArrayList;


public class AlbumAdapter extends ArrayAdapter<ImageModel> {
    Context context;
    ArrayList<ImageModel> data;
    int layoutResourceId;
    OnAlbum onItem;
    int rowIndex;
    int pWidthItem = 0;
    int pHeightItem = 0;

    static class RecordHolder {
        ImageView imageItem;
        TextView txtTitle;
        LinearLayout selected;

        RecordHolder() {
        }
    }

    public AlbumAdapter(Context context, int layoutResourceId, ArrayList<ImageModel> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.pWidthItem = getDisplayInfo((Activity) context).widthPixels * 200 / 1080;
        this.pHeightItem = getDisplayInfo((Activity) context).heightPixels * 200 / 1920;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        RecordHolder holder;
        View row = convertView;
        if (row == null) {
            row = ((Activity) this.context).getLayoutInflater().inflate(this.layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.name_album);
            holder.selected = row.findViewById(R.id.selected);
            holder.imageItem = (ImageView) row.findViewById(R.id.icon_album);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        ImageModel item = this.data.get(position);
        holder.txtTitle.setText(item.getName());
        Glide.with(this.context).load(new File(item.getPathFile())).asBitmap().placeholder(R.drawable.piclist_icon_default).into(holder.imageItem);

        row.setOnClickListener(v -> {
            if (AlbumAdapter.this.onItem != null) {
                rowIndex = position;
                notifyDataSetChanged();

                onItem.OnItemAlbumClick(position);
                Log.e("oooooo", "onClick: " + position);
            }
        });

        if (rowIndex == position) {
            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.GONE);
        }
        return row;
    }

    public OnAlbum getOnItem() {
        return this.onItem;
    }

    public void setOnItem(OnAlbum onItem) {
        this.onItem = onItem;
    }

    public static DisplayMetrics getDisplayInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}
