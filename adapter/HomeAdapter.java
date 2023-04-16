package infiapp.com.videomaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import infiapp.com.videomaker.model.VideoviewModel;
import infiapp.com.videomaker.R;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomViewHolder> {

    Activity context;
    HomeAdapter.OnItemClickListener listener;
    ArrayList<VideoviewModel> dataList;


    public HomeAdapter(Activity context, ArrayList<VideoviewModel> dataList, HomeAdapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }


    @Override
    public HomeAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        HomeAdapter.CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }



    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final HomeAdapter.CustomViewHolder holder, final int i) {
        final VideoviewModel item = dataList.get(i);


        holder.bind(i, item, listener);


    }

    public interface OnItemClickListener {
        void onItemClick(int positon, VideoviewModel item, View view);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {


        private ImageView share;
        private ImageView download;
        private ImageView useNow;

        public CustomViewHolder(View view) {
            super(view);

            share = view.findViewById(R.id.share);
            download = view.findViewById(R.id.download);
            useNow = view.findViewById(R.id.useNow);

        }


        public void bind(final int postion, final VideoviewModel item, final HomeAdapter.OnItemClickListener listener) {


            download.setOnClickListener(v -> listener.onItemClick(postion, item, v));

            share.setOnClickListener(v -> listener.onItemClick(postion, item, v));

            useNow.setOnClickListener(v -> listener.onItemClick(postion, item, v));

        }

    }
}