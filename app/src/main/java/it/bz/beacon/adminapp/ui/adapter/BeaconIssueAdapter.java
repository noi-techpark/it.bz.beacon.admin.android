package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.ui.detail.DetailActivity;

public class BeaconIssueAdapter extends RecyclerView.Adapter<BeaconIssueAdapter.BeaconIssueViewHolder> implements Filterable {

    private Context context;
    private List<BeaconIssue> originalValues;
    private List<BeaconIssue> issues = new ArrayList<>();

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

    public void setIssues(List<BeaconIssue> issues) {
        this.issues.clear();
        this.issues.addAll(issues);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                issues = (List<BeaconIssue>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<BeaconIssue> filteredIssues = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(issues);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = originalValues.size();
                    results.values = originalValues;
                }
                else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        BeaconIssue issue = originalValues.get(i);
                        if (issue.getProblem().toLowerCase().contains(constraint.toString())
                                || issue.getProblemDescription().toLowerCase().contains(constraint.toString())) {
                            filteredIssues.add(issue);
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

        @BindView(R.id.title)
        TextView title;

        @BindView(R.id.description)
        TextView description;

        private BeaconIssueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setIssue(final BeaconIssue issue) {

            title.setText(issue.getProblem());
            description.setText(issue.getProblemDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: implement onClick
//                    Log.d(AdminApplication.LOG_TAG, "# 1 #");
//                    Intent intent = new Intent(context, DetailActivity.class);
//                    intent.putExtra(DetailActivity.EXTRA_BEACON_ID, issue.getId());
//                    intent.putExtra(DetailActivity.EXTRA_BEACON_NAME, issue.getName());
//                    context.startActivity(intent);
//                    Log.d(AdminApplication.LOG_TAG, "# 2 #");
                }
            });
        }
    }
}