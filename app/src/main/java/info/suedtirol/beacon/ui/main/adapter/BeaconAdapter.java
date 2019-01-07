package info.suedtirol.beacon.ui.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.suedtirol.beacon.R;
import info.suedtirol.beacon.data.entity.Beacon;

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.BeaconViewHolder> implements Filterable {

    private Context context;
    private List<Beacon> originalValues;
    private List<Beacon> beacons = new ArrayList<>();

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

    public void setBeacons(List<Beacon> beacons) {
        beacons.sort(new Comparator<Beacon>() {
            @Override
            public int compare(Beacon Beacon1, Beacon Beacon2) {
                if (!TextUtils.isEmpty(Beacon1.getTitle()) && !TextUtils.isEmpty(Beacon2.getTitle()))
                    return Beacon1.getTitle().compareTo(Beacon2.getTitle());
                return 0;
            }
        });
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

                beacons = (List<Beacon>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Beacon> filteredBeacons = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(beacons);
                }

                if (constraint == null || constraint.length() == 0) {

                    results.count = originalValues.size();
                    results.values = originalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        Beacon Beacon = originalValues.get(i);
                        if (Beacon.getTitle().toLowerCase().contains(constraint.toString())
                                || Beacon.getDescription().toLowerCase().contains(constraint.toString())) {
                            filteredBeacons.add(Beacon);
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

        @BindView(R.id.beacon_status)
        ImageView status;

        @BindView(R.id.beacon_title)
        TextView title;

        @BindView(R.id.beacon_description)
        TextView description;

        private BeaconViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setBeacon(final Beacon beacon) {

         //   status.setBackgroundTintList(ColorStateList.valueOf(col));
            title.setText(beacon.getTitle());
            description.setText(beacon.getDescription());
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, BeaconActivity.class);
//                    intent.putExtra(BeaconActivity.EXTRA_Beacon_ID, Beacon.getBeaconId());
//                    intent.putExtra(BeaconActivity.EXTRA_Beacon_NAME, Beacon.getName1());
//                    context.startActivity(intent);
//
//                }
//            });
        }
    }
}