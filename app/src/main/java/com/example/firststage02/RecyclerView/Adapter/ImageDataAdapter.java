package com.example.firststage02.RecyclerView.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firststage02.Database.ImageEntity;
import com.example.firststage02.Fragment.DataFragment;
import com.example.firststage02.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageDataAdapter extends RecyclerView.Adapter<ImageDataAdapter.viewHolder> {

    private List<ImageEntity> datalist;
    private Context context;

    public ImageDataAdapter(List<ImageEntity> datalist, DataFragment context) {
        this.datalist = datalist;
        this.context = context.getContext();
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_data_sample ,parent ,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {

        ImageEntity imageEntity = datalist.get(position);
        int id = imageEntity.getId();
        String str = Integer.toString(id);
        holder.textView.setText(str);
        byte[] bitarry = imageEntity.getImage();
        Bitmap bm = BitmapFactory.decodeByteArray(bitarry,0,bitarry.length);
        holder.image.setImageBitmap(bm);

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView image;
        public viewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.image_id);
            image = itemView.findViewById(R.id.imageview);
        }
    }
}
