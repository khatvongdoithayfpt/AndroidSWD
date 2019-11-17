package com.example.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ui.activity.R;

import java.io.InputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {

    private List<Uri> data;
    private List<String> links;
    private Context context;

    public ImageAdapter(List<Uri> data,List<String> links, Context context) {
        this.data = data;
        this.context = context;
        this.links = links;
    }

    @NonNull
    @Override
    public ImageAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_image_adapter, parent, false);
        ImageAdapter.Holder holder = new ImageAdapter.Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.Holder holder, int position) {
        if(data!= null){
            Uri uri = data.get(position);
            setImageView(holder.imageView, uri);
            holder.imageView.setTag(uri);
        }else{
            String url = links.get(position);
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        }

    }


    @Override
    public int getItemCount() {
        if(data!= null){
            return data.size();
        }else{
            return links.size();
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_child);
        }
    }

    private void setImageView(ImageView imageView, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
