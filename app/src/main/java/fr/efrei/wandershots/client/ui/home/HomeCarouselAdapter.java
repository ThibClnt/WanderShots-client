package fr.efrei.wandershots.client.ui.home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

import fr.efrei.wandershots.client.R;
import fr.efrei.wandershots.client.data.Place;

public class HomeCarouselAdapter extends RecyclerView.Adapter<HomeCarouselAdapter.HomeCarouselViewHolder> {

    private Context context;
    private RequestManager glide;
    private final List<Place> places;

    public HomeCarouselAdapter(List<Place> places, RequestManager glide) {
        this.places = places;
        this.glide = glide;
    }

    @NonNull
    @Override
    public HomeCarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item, parent, false);
        return new HomeCarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCarouselViewHolder holder, int position) {
        Place place = places.get(position);
        holder.updateImage(place.getImageUrl(), glide);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class HomeCarouselViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public HomeCarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image_view);
        }

        public void updateImage(String imageUrl, RequestManager glide) {
            glide.load(imageUrl).into(imageView);
        }
    }
}
