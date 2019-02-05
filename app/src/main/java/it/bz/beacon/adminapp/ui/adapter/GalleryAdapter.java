package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconImage;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

    private Context context;
    private List<BeaconImage> images = new ArrayList<>();

    public GalleryAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public @NonNull
    GalleryAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new GalleryAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ImageViewHolder holder, int position) {
        holder.setImage(images.get(position));
    }

    public void setBeaconImages(List<BeaconImage> images) {
        this.images.clear();
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public void addBeaconImage(BeaconImage image) {
        this.images.add(0, image);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.gallery_image)
        ImageView image;

        private ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setImage(BeaconImage beaconImage) {
            ContextWrapper contextWrapper = new ContextWrapper(context);
            File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
            File file = new File(directory, beaconImage.getId() + ".png");

            if (file.exists()) {
                Picasso.with(context)
                        .load(file)
                        .placeholder(R.drawable.placeholder)
                        .into(image);
            } else {
                Picasso.with(context)
                        .load(beaconImage.getUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(image);
            }
        }
    }
}