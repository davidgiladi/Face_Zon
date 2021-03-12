package com.example.face.class_for_code;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.face.R;

import java.util.List;

public class Your_Image_Adapter extends RecyclerView.Adapter<Your_Image_Adapter.MyViewHolder> {
    private List<Uri> list_of_images;
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView your_image;
        MyViewHolder(View view) {
            super(view);
            your_image = view.findViewById(R.id.your_image);

        }
    }
    public Your_Image_Adapter(List<Uri> list_of_images) {
        this.list_of_images = list_of_images;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.your_image, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Uri image = list_of_images.get(position);
        holder.your_image.setImageURI(image);
       ;
    }
    @Override
    public int getItemCount() {
        return list_of_images.size();
    }
}