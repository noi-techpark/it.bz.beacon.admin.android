package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.ui.detail.DetailActivity;

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconViewHolder> implements Filterable {

    private Context context;
    private List<BeaconMinimal> originalValues;
    private List<BeaconMinimal> beacons = new ArrayList<>();

    public BeaconAdapter(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull BeaconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listitem_beacon, parent, false);
        return new BeaconViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconViewHolder holder, int position) {
        holder.setBeacon(beacons.get(position));
    }

    public void setBeacons(List<BeaconMinimal> beacons) {
        this.beacons.clear();
        this.beacons.addAll(beacons);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                beacons = (List<BeaconMinimal>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<BeaconMinimal> filteredBeacons = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(beacons);
                }

                if (constraint == null || constraint.length() == 0) {

                    results.count = originalValues.size();
                    results.values = originalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        BeaconMinimal beaconMinimal = originalValues.get(i);
                        if (beaconMinimal.getName().toLowerCase().contains(constraint.toString())
                                || beaconMinimal.getName().toLowerCase().contains(constraint.toString())
                                || beaconMinimal.getManufacturerId().toLowerCase().contains(constraint.toString())) {
                            filteredBeacons.add(beaconMinimal);
                        }
                    }
                    results.count = filteredBeacons.size();
                    results.values = filteredBeacons;
                }
                return results;
            }
        };
    }

    class BeaconViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.beacon_name)
        TextView name;

        @BindView(R.id.beacon_major)
        TextView major;

        @BindView(R.id.beacon_minor)
        TextView minor;

        @BindView(R.id.info_status_icon)
        ImageView status;

        @BindView(R.id.info_battery_icon)
        ImageView battery;

        @BindView(R.id.beacon_manufacturer_id)
        TextView manufacturerId;

        private BeaconViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setBeacon(final BeaconMinimal beaconMinimal) {

            name.setText(beaconMinimal.getName());
            major.setText("Major: " + beaconMinimal.getMajor());
            minor.setText("Minor: " + beaconMinimal.getMinor());
            manufacturerId.setText(beaconMinimal.getManufacturerId());

            if (beaconMinimal.getStatus().equals(Beacon.STATUS_OK)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_ok)));
            }
            if (beaconMinimal.getStatus().equals(Beacon.STATUS_BATTERY_LOW)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_warning)));
            }
            if (beaconMinimal.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_pending)));
            }

            if (beaconMinimal.getBatteryLevel() < 34) {
                battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_alert));
            }
            else {
                if (beaconMinimal.getBatteryLevel() < 66) {
                    battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_50));
                }
                else {
                    battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_full));
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_BEACON_ID, beaconMinimal.getId());
                    intent.putExtra(DetailActivity.EXTRA_BEACON_NAME, beaconMinimal.getName());
                    context.startActivity(intent);
                }
            });
        }
    }
}