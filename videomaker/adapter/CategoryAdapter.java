package infiapp.com.videomaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

import infiapp.com.videomaker.model.ModelCategoryImgage;
import infiapp.com.videomaker.R;


public class CategoryAdapter extends Adapter<CategoryAdapter.MyViewHolder> {
    int intPos = 0;
    Context context;
    List<ModelCategoryImgage> modelCategoryImgages;
    MyClickListener mListener;

    public CategoryAdapter(Context context, List<ModelCategoryImgage> arrayList) {
        this.context = context;
        this.modelCategoryImgages = arrayList;
    }

    public void setOnItemClickListener(MyClickListener mListener) {

        this.mListener = mListener;

    }

    public int getItemCount() {
        return this.modelCategoryImgages.size();
    }

    @RequiresApi(api = 16)
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final ModelCategoryImgage modelCategoryImgage = modelCategoryImgages.get(i);

        if (i == 0 || i == 11 || i==22) {
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient1);
        }else if(i == 1 || i == 12 || i==23){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient2);
        }else if(i == 2 || i == 13 || i==24){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient3);
        }else if(i == 3 || i == 14 || i==25){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient4);
        }else if(i == 4 || i == 15 || i==26){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient5);
        }else if(i == 5 || i == 16 || i==27){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient6);
        }else if(i == 6 || i == 17 || i==28){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient1);
        }else if(i == 7 || i == 18 || i==29){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient2);
        }else if(i == 8 || i == 19 || i==30){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient3);
        }else if(i == 9 || i == 20 || i==31){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient4);
        }else if(i == 10 || i == 21 || i==32){
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient5);
        }else{
            myViewHolder.imageView.setBackgroundResource(R.drawable.gradient2);
        }

        myViewHolder.textView.setText(modelCategoryImgage.getCategory());



        if (this.intPos == i) {
            myViewHolder.view.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.view.setVisibility(View.GONE);
        }

        myViewHolder.itemView.setOnClickListener(view -> {
            intPos = i;
            mListener.onItemClick(intPos, modelCategoryImgages);
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category, viewGroup, false));
    }

    public interface MyClickListener {

        void onItemClick(int position, List<ModelCategoryImgage> catData);

    }

    public static class MyViewHolder extends ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private View view;

        public MyViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.cat_img);
            this.textView = (TextView) view.findViewById(R.id.cat_title);
            this.view = view.findViewById(R.id.view);
        }
    }
}
