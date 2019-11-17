package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.HistoryRecord;
import com.example.ui.activity.R;
import com.example.callback.DoCallBack;

import java.io.File;
import java.util.List;

import static com.example.utils.Utils.setImageView;

public class SavedImageAdapter extends RecyclerView.Adapter<SavedImageAdapter.Holder> {

    private List<HistoryRecord> data;
    private Context context;

    public SavedImageAdapter(List data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_image_adapter, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        HistoryRecord historyRecord = data.get(position);
        String path = historyRecord.getChild();
        File file = new File(path);
        setImageView(holder.imageView, file);
        holder.imageView.setTag(historyRecord);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_child);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HistoryRecord historyRecord = (HistoryRecord) v.getTag();
                    DoCallBack doCallBack = (DoCallBack) context;
                    doCallBack.doCallback(historyRecord);
                }
            });
        }
    }
}
