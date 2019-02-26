package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.location.Location;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;
import it.bz.beacon.adminapp.ui.issue.IssueDetailActivity;

public class BeaconIssueAdapter extends RecyclerView.Adapter<BeaconIssueAdapter.BeaconIssueViewHolder> implements Filterable {

    private Context context;
    private List<IssueWithBeacon> originalValues;
    private List<IssueWithBeacon> issues = new ArrayList<>();
    private LatLng currentLocation = null;

    public BeaconIssueAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public @NonNull
    BeaconIssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listitem_issue, parent, false);
        return new BeaconIssueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BeaconIssueViewHolder holder, int position) {
        holder.setIssue(issues.get(position));
    }

    public void setIssues(List<IssueWithBeacon> issues) {
        this.issues.clear();
        this.issues.addAll(issues);
        notifyDataSetChanged();
    }

    public void setCurrentLocation(LatLng location) {
        this.currentLocation = location;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return issues != null ? issues.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return issues.get(position).getId();
    }

    private double distanceBetween(LatLng point1, LatLng point2) {
        float[] results = new float[1];
        Location.distanceBetween(point1.latitude, point1.longitude, point2.latitude, point2.longitude, results);
        return results[0];
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                issues = (List<IssueWithBeacon>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchFilter = "";
                int radiusFilter = 0;

                if (!TextUtils.isEmpty(constraint.toString())) {
                    if (constraint.toString().indexOf('#') > 0) {
                        searchFilter = constraint.toString().substring(0, constraint.toString().indexOf('#'));
                    }

                    if (constraint.toString().indexOf('#') < constraint.toString().length() - 1) {
                        radiusFilter = Integer.parseInt(constraint.toString().substring(constraint.toString().indexOf('#') + 1));
                    }
                }

                FilterResults results = new FilterResults();
                List<IssueWithBeacon> filteredIssues = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(issues);
                }

                if (searchFilter.length() == 0) {
                    if (radiusFilter == 0) {
                        results.count = originalValues.size();
                        results.values = originalValues;
                    }
                    else {
                        for (int i = 0; i < originalValues.size(); i++) {
                            IssueWithBeacon issueWithBeacon = originalValues.get(i);
                            if ((issueWithBeacon.getLat() != 0 && issueWithBeacon.getLng() != 0)) {
                                if (currentLocation != null) {
                                    if (distanceBetween(currentLocation, new LatLng(issueWithBeacon.getLat(), issueWithBeacon.getLng())) < radiusFilter) {
                                        filteredIssues.add(issueWithBeacon);
                                    }
                                }
                                // TODO: decide if issues should be added to filtered list if current location is not available
//                                else {
//                                    filteredIssues.add(issueWithBeacon);
//                                }
                            }
                        }
                        results.count = filteredIssues.size();
                        results.values = filteredIssues;
                    }

                }
                else {
                    searchFilter = searchFilter.toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        IssueWithBeacon issueWithBeacon = originalValues.get(i);
                        if (radiusFilter > 0) {
                            if (currentLocation != null) {
                                if ((distanceBetween(currentLocation, new LatLng(issueWithBeacon.getLat(), issueWithBeacon.getLng())) < radiusFilter) &&
                                        (issueWithBeacon.getProblem().toLowerCase().contains(searchFilter)
                                                || issueWithBeacon.getProblemDescription().toLowerCase().contains(searchFilter)
                                                || issueWithBeacon.getName().toLowerCase().contains(searchFilter))) {
                                    filteredIssues.add(issueWithBeacon);
                                }
                            }
                        }
                        else {
                            if (issueWithBeacon.getProblem().toLowerCase().contains(searchFilter)
                                    || issueWithBeacon.getProblemDescription().toLowerCase().contains(searchFilter)
                                    || issueWithBeacon.getName().toLowerCase().contains(searchFilter)) {
                                filteredIssues.add(issueWithBeacon);
                            }
                        }
                    }
                    results.count = filteredIssues.size();
                    results.values = filteredIssues;
                }
                return results;
            }
        };
    }

    class BeaconIssueViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.issue_beacon)
        TextView title;

        @BindView(R.id.issue_description)
        TextView description;

        @BindView(R.id.issue_status_icon)
        ImageView status;

        @BindView(R.id.issue_battery_icon)
        ImageView battery;

        private BeaconIssueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setIssue(final IssueWithBeacon issue) {

            title.setText(issue.getProblem());
            description.setText(issue.getProblemDescription());

            if (issue.getStatus().equals(Beacon.STATUS_OK)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_ok)));
            }
            if ((issue.getStatus().equals(Beacon.STATUS_BATTERY_LOW)) || (issue.getStatus().equals(Beacon.STATUS_ISSUE))) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_warning)));
            }
            if (issue.getStatus().equals(Beacon.STATUS_NO_SIGNAL)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_error)));
            }
            if (issue.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                ImageViewCompat.setImageTintList(status, ColorStateList.valueOf(context.getColor(R.color.status_pending)));
            }

            if (issue.getBatteryLevel() < context.getResources().getInteger(R.integer.battery_alert_level)) {
                battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_alert));
            }
            else {
                if (issue.getBatteryLevel() < context.getResources().getInteger(R.integer.battery_half_level)) {
                    battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_50));
                }
                else {
                    battery.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_battery_full));
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, IssueDetailActivity.class);
                    intent.putExtra(IssueDetailActivity.EXTRA_ISSUE_ID, issue.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}