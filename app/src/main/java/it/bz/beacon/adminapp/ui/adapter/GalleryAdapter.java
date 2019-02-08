package it.bz.beacon.adminapp.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
//import com.stfalcon.imageviewer.StfalconImageViewer;
//import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconImage;
import it.bz.beacon.adminapp.ui.detail.DetailActivity;
import it.bz.beacon.adminapp.ui.detail.ImageFullscreenActivity;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

    private Context context;
    private OnImageDeleteListener deleteListener;
    private List<BeaconImage> images = new ArrayList<>();

    public GalleryAdapter(Context context, OnImageDeleteListener deleteListener) {
        this.context = context;
        this.deleteListener = deleteListener;
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

    public void removeBeaconImage(BeaconImage image) {
        this.images.remove (image);
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

        private void setImage(final BeaconImage beaconImage) {
            ContextWrapper contextWrapper = new ContextWrapper(context);
            File directory = contextWrapper.getDir(context.getString(R.string.image_folder), Context.MODE_PRIVATE);
            final File file = new File(directory, beaconImage.getFileName());

            if (file.exists()) {
                Picasso.with(context)
                        .load(file)
                        .placeholder(R.drawable.placeholder)
                        .into(image);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (deleteListener != null) {
                        if (deleteListener.onDeleteRequested()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
                            builder.setTitle(context.getString(R.string.details_delete));
                            builder.setMessage(context.getString(R.string.delete_warning));
                            builder.setPositiveButton(context.getString(R.string.details_delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (deleteListener != null) {
                                        deleteListener.onDelete(beaconImage);
                                    }
                                    dialog.dismiss();
                                }
                            });
                            builder.setNeutralButton(context.getString(R.string.cancel), null);
                            builder.show();
                        }
                    }
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    new StfalconImageViewer.Builder<>(context, images, new ImageLoader<File>() {
//
//                        @Override
//                        public void loadImage(ImageView imageView, File image) {
//                            Picasso.with(context)
//                                    .load(image)
//                                    .placeholder(R.drawable.placeholder)
//                                    .into(imageView);
//                        }
//                    }).show();
                    Intent intent = new Intent(context, ImageFullscreenActivity.class);
                    intent.putExtra(ImageFullscreenActivity.EXTRA_IMAGE_FILENAME, beaconImage.getFileName());
                    context.startActivity(intent);
                }
            });
        }
    }

    public interface OnImageDeleteListener {
        void onDelete(BeaconImage beaconImage);
        boolean onDeleteRequested();
    }
}