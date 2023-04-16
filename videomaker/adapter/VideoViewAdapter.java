package infiapp.com.videomaker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import infiapp.com.videomaker.BuildConfig;
import infiapp.com.videomaker.activity.VideoListActivity;
import infiapp.com.videomaker.model.VideoviewModel;
import infiapp.com.videomaker.util.MyAppUtils;
import infiapp.com.videomaker.util.Utils;
import infiapp.com.videomaker.R;


public class VideoViewAdapter extends Adapter<VideoViewAdapter.MyViewHolder> {
    ArrayList<VideoviewModel> videoArr;
    private Activity context;

    public VideoViewAdapter(Activity context, ArrayList<VideoviewModel> arrayList) {
        this.context = context;
        this.videoArr = arrayList;
    }



    public static String removeLastChars(String str, int chars) {

        String string = str.substring(0, str.length() - chars);
        String lastWord = str.substring(str.lastIndexOf(" ") + 1);
        if (lastWord.trim().toLowerCase().equals("v")) {

            return string;

        } else {
            return str;
        }
    }


    public void setDataList(ArrayList<VideoviewModel> dataListMe) {
        this.videoArr = dataListMe;
        notifyDataSetChanged();

    }



    @Override
    public int getItemCount() {
        return this.videoArr.size();
    }

    @Override
    public int getItemViewType(int i) {
        return this.videoArr.get(i) != null ? 1 : 0;
    }

    @RequiresApi(api = 16)
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        int itemViewType = myViewHolder.getItemViewType();
        if (itemViewType == 0) {
            if (MyAppUtils.verifyInstallerId(context) || BuildConfig.DEBUG) {

            }


        } else if (itemViewType == 1) {


            String videoTitle = (this.videoArr.get(i)).getTitle().trim().replaceAll("[0-9]", "").replace("_", " ").replace("boo", "");

            String titleCapital = removeLastChars(MyAppUtils.capitalize(videoTitle).trim(), 1);

            myViewHolder.videoName.setText(titleCapital);

            Picasso.get().load((this.videoArr.get(i)).getVideoThumb() != null ? (this.videoArr.get(i)).getVideoThumb() : "").placeholder(R.drawable.bg_card).into(myViewHolder.videoThumb);

            myViewHolder.videoThumb.setOnClickListener(view -> {
                if (!MyAppUtils.isConnectingToInternet(context)) {

                    Toast.makeText(VideoViewAdapter.this.context, "Please Connect to Internet.", Toast.LENGTH_SHORT).show();


                } else if (VideoViewAdapter.this.videoArr.get(i) != null) {

                    Utils.staticVideoModelData = VideoViewAdapter.this.videoArr.get(i);

                    VideoviewModel videoviewModelData = Utils.staticVideoModelData;

                    if (videoviewModelData == null || videoviewModelData.getVideoLink() == null) {
                        Toast.makeText(VideoViewAdapter.this.context, "Slow Network Connection. Try Again.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    VideoViewAdapter.this.context.startActivity(new Intent(VideoViewAdapter.this.context, VideoListActivity.class)
                            .putExtra("mdata", videoArr)
                            .putExtra("position", i));

                } else {
                    Toast.makeText(VideoViewAdapter.this.context, "Something went wrong! please check internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View inflate;
        if (i == 0) {
            inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ad_view, viewGroup, false);
        } else {
            inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, viewGroup, false);
        }
        return new MyViewHolder(inflate);
    }

    public static class MyViewHolder extends ViewHolder {
        private ImageView videoThumb;
        private TextView videoName;
        private FrameLayout fbPlaceHolder;


        public MyViewHolder(View view) {
            super(view);
            videoThumb = view.findViewById(R.id.video_thumb);
            videoName = view.findViewById(R.id.video_name);
            fbPlaceHolder = view.findViewById(R.id.fl_adplaceholder);

        }
    }

}
