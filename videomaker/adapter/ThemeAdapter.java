package infiapp.com.videomaker.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import infiapp.com.videomaker.MyApplication;
import infiapp.com.videomaker.activity.VideoThemeActivity;
import infiapp.com.videomaker.R;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import infiapp.com.videomaker.theme.mask.AllTheme;

import infiapp.com.videomaker.theme.service.ServiceAnim;
import infiapp.com.videomaker.theme.util.FileUtils;


public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.Holder> {
    private MyApplication application = MyApplication.getInstance();
    private LayoutInflater inflater;
    private List<AllTheme> list;
    private VideoThemeActivity activity;

    int position = 0;

    public class Holder extends RecyclerView.ViewHolder {
        ImageView cbSelect;
        private View clickableView;
        private ImageView ivThumb;


        public Holder(View v) {
            super(v);
            this.cbSelect = v.findViewById(R.id.cbSelect);
            this.ivThumb = v.findViewById(R.id.ivThumb);
            this.clickableView = v.findViewById(R.id.clickableView);

        }
    }

    public ThemeAdapter(VideoThemeActivity pvmwsPreviewActivity) {
        this.activity = pvmwsPreviewActivity;
        this.list = new ArrayList(Arrays.asList(AllTheme.values()));
        this.inflater = LayoutInflater.from(pvmwsPreviewActivity);
    }

    public Holder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View view = this.inflater.inflate(R.layout.theme_items, paramViewGroup, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((activity.getResources()
                .getDisplayMetrics().widthPixels * 200 / 1080),
                (activity.getResources()
                        .getDisplayMetrics().widthPixels * 200 / 1080));
        params.setMargins(10, 10, 10, 10);

        view.setLayoutParams(params);
        return new Holder(view);
    }

    public void onBindViewHolder(Holder holder, final int pos) {
        AllTheme pvswsThemes = this.list.get(pos);
        Glide.with(this.application).load(Integer.valueOf(pvswsThemes.getThemeDrawable())).into(holder.ivThumb);


        holder.cbSelect.setVisibility(View.GONE);
        if (position == pos) {
            holder.cbSelect.setVisibility(View.VISIBLE);
        }

        holder.clickableView.setOnClickListener((View v) -> {
            if (list.get(pos) != application.selectedTheme) {
                position = pos;
                deleteThemeDir(application.selectedTheme.toString());
                application.videoImages.clear();
                application.selectedTheme = list.get(pos);
                application.setCurrentTheme(application.selectedTheme.toString());
                activity.reset();
                Intent intent = new Intent(application, ServiceAnim.class);
                intent.putExtra(ServiceAnim.EXTRA_SELECTED_THEME, application.getCurrentTheme());
                application.startService(intent);
                notifyDataSetChanged();
            }

        });
    }

    private void deleteThemeDir(final String dir) {
        new Thread() {
            public void run() {
                FileUtils.deleteThemeDir(dir);
            }
        }.start();
    }

    public List<AllTheme> getList() {
        return this.list;
    }

    public int getItemCount() {
        return this.list.size();
    }
}
