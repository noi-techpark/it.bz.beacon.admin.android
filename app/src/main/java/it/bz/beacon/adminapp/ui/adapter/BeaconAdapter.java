package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
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
        setHasStableIds(true);
    }

    @Override
    public @NonNull
    BeaconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    }

    @Override
    public int getItemCount() {
        return beacons != null ? beacons.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return beacons.get(position).getMajor() * 100000L + beacons.get(position).getMinor();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                beacons = (List<BeaconMinimal>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchFilter = "";
                String statusFilter = Beacon.STATUS_ALL;

                if (!TextUtils.isEmpty(constraint.toString())) {
                    if (constraint.toString().indexOf('#') > 0) {
                        statusFilter = constraint.toString().substring(0, constraint.toString().indexOf('#'));
                    }

                    if (constraint.toString().indexOf('#') < constraint.toString().length() - 1) {
                        searchFilter = constraint.toString().substring(constraint.toString().indexOf('#') + 1);
                    }
                }

                FilterResults results = new FilterResults();
                List<BeaconMinimal> filteredBeacons = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(beacons);
                }

                if (searchFilter.length() == 0) {
                    if (statusFilter.equals(Beacon.STATUS_ALL)) {
                        results.count = originalValues.size();
                        results.values = originalValues;
                    } else {
                        for (int i = 0; i < originalValues.size(); i++) {
                            BeaconMinimal beaconMinimal = originalValues.get(i);
                            switch (statusFilter) {
                                case Beacon.STATUS_INSTALLED:
                                    if (beaconMinimal.getLat() != 0 || beaconMinimal.getLng() != 0) {
                                        filteredBeacons.add(beaconMinimal);
                                    }
                                    break;
                                case Beacon.STATUS_NOT_INSTALLED:
                                    if (beaconMinimal.getLat() == 0 && beaconMinimal.getLng() == 0) {
                                        filteredBeacons.add(beaconMinimal);
                                    }
                                    break;
                                default:
                                    if ((beaconMinimal.getStatus().equalsIgnoreCase(statusFilter))) {
                                        filteredBeacons.add(beaconMinimal);
                                    }
                                    break;
                            }
                        }
                        results.count = filteredBeacons.size();
                        results.values = filteredBeacons;
                    }

                } else {
                    searchFilter = searchFilter.toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        BeaconMinimal beaconMinimal = originalValues.get(i);
                        if (((beaconMinimal.getStatus().equals(statusFilter)) || (statusFilter.equals(Beacon.STATUS_ALL))) &&
                                (beaconMinimal.getName().toLowerCase().contains(searchFilter)
                                        || beaconMinimal.getManufacturerId().toLowerCase().contains(searchFilter))) {
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

        @BindView(R.id.beacon_rssi)
        TextView rssi;

        private BeaconViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setBeacon(final BeaconMinimal beaconMinimal) {

            name.setText(beaconMinimal.getName());
            major.setText(context.getString(R.string.major_format, beaconMinimal.getMajor()));
            minor.setText(context.getString(R.string.minor_format, beaconMinimal.getMinor()));
            manufacturerId.setText(beaconMinimal.getId());

            if (beaconMinimal.getStatus().equals(Beacon.STATUS_OK)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_ok)));
            }
            if ((beaconMinimal.getStatus().equals(Beacon.STATUS_BATTERY_LOW)) || (beaconMinimal.getStatus().equals(Beacon.STATUS_ISSUE))) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_warning)));
            }
            if (beaconMinimal.getStatus().equals(Beacon.STATUS_NO_SIGNAL)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_error)));
            }
            if (beaconMinimal.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_pending)));
            }

            if (beaconMinimal.getBatteryLevel() < context.getResources().getInteger(R.integer.battery_alert_level)) {
                battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_alert));
            }
            else {
                if (beaconMinimal.getBatteryLevel() < context.getResources().getInteger(R.integer.battery_half_level)) {
                    battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_50));
                }
                else {
                    battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_full));
                }
            }

            if (beaconMinimal.getRssi() != null) {
                rssi.setVisibility(View.VISIBLE);
                rssi.setText(context.getString(R.string.rssi, beaconMinimal.getRssi()));
            }
            else {
                rssi.setVisibility(View.GONE);
            }
            battery.setAlpha(0.6f);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Debug.startMethodTracing();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_BEACON_ID, beaconMinimal.getId());
                    intent.putExtra(DetailActivity.EXTRA_BEACON_NAME, beaconMinimal.getName());
                    context.startActivity(intent);
                }
            });
        }
    }
}