package fr.efrei.wandershots.client.ui.home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.entities.Picture;

public class HomeCarouselAdapter extends RecyclerView.Adapter<HomeCarouselAdapter.HomeCarouselViewHolder> {

    private final RequestManager glide;
    private final List<Picture> pictures;

    public HomeCarouselAdapter(List<Picture> pictures, RequestManager glide) {
        this.pictures = pictures;
        this.glide = glide;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures.clear();
        this.pictures.addAll(pictures);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeCarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item, parent, false);
        return new HomeCarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCarouselViewHolder holder, int position) {
        Picture picture = pictures.get(position);
        holder.updateImage(picture.getImage(), glide);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public static class HomeCarouselViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public HomeCarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image_view);
        }

        public void updateImage(byte[] imageData, RequestManager glide) {
            glide.load(imageData).into(imageView);
        }
    }
}
