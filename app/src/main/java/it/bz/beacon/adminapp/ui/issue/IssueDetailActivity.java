package it.bz.beacon.adminapp.ui.issue;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;
import it.bz.beacon.adminapp.data.viewmodel.BeaconIssueViewModel;
import it.bz.beacon.adminapp.ui.BaseDetailActivity;
import it.bz.beacon.adminapp.util.DateFormatter;

public class IssueDetailActivity extends BaseDetailActivity {

    public static final String EXTRA_ISSUE_ID = "EXTRA_ISSUE_ID";

    @BindView(R.id.beacon_name)
    protected TextView txtBeaconName;

    @BindView(R.id.problem)
    protected TextView txtProblem;

    @BindView(R.id.last_seen)
    protected TextView txtLastSeen;

    @BindView(R.id.problem_description)
    protected TextView txtProblemDescription;

    @BindView(R.id.battery_status)
    protected TextView txtBatteryStatus;

    @BindView(R.id.device_status)
    protected TextView txtDeviceStatus;

    private BeaconIssueViewModel beaconIssueViewModel;

    private boolean isEditing = false;
    private long issueId;
    private IssueWithBeacon issue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getIntent() != null) {
            issueId = getIntent().getLongExtra(EXTRA_ISSUE_ID, -1L);
        }

        beaconIssueViewModel = ViewModelProviders.of(this).get(BeaconIssueViewModel.class);
        loadIssue();
    }

    @Override
    protected void setContentEnabled(boolean enabled) {

    }

    @Override
    protected boolean validate() {
        return false;
    }

    @Override
    protected void save() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_issue_detail;
    }

    private void loadIssue() {
        showProgress(getString(R.string.loading));

        beaconIssueViewModel.getIssueWithBeaconById(issueId).observe(this, new Observer<IssueWithBeacon>() {
            @Override
            public void onChanged(IssueWithBeacon issueWithBeacon) {
                issue = issueWithBeacon;
                showData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpToolbar(getString(R.string.issue));
    }

    private void showProgress(String text) {
//        txtProgress.setText(text);
//        content.setVisibility(View.GONE);
//        progress.setVisibility(View.VISIBLE);
    }


//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id) {
//            case android.R.id.home:
//                if (isEditing) {
//                    showCloseWarning();
//                }
//                else {
//                    finish();
//                }
//                break;
//            case R.id.menu_edit:
//                showPendingData();
//                isEditing = true;
//                if (map != null) {
//                    map.setOnMapClickListener(this);
//                }
//                setContentEnabled(isEditing);
//                invalidateOptionsMenu();
//                setUpToolbar();
//                break;
//            case R.id.menu_save:
//                AdminApplication.hideKeyboard(this);
//                if (validate()) {
//                    save();
//                }
//                break;
//            default:
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    protected void showData() {
        if (issue != null) {
            txtBeaconName.setText(issue.getName());
            txtProblem.setText(issue.getProblem());
            txtLastSeen.setText(issue.getLastSeen() != null ? DateFormatter.dateToDateString(new Date(issue.getLastSeen())) : "-");
            txtProblemDescription.setText(issue.getProblemDescription());
            txtBatteryStatus.setText(getString(R.string.percent, issue.getBatteryLevel()));
            if (issue.getStatus().equals(Beacon.STATUS_OK)) {
                txtDeviceStatus.setText(getString(R.string.status_ok));
            }
            if (issue.getStatus().equals(Beacon.STATUS_BATTERY_LOW)) {
                txtDeviceStatus.setText(getString(R.string.status_battery_low));
            }
            if (issue.getStatus().equals(Beacon.STATUS_ISSUE)) {
                txtDeviceStatus.setText(getString(R.string.status_issue));
            }
            if (issue.getStatus().equals(Beacon.STATUS_NO_SIGNAL)) {
                txtDeviceStatus.setText(getString(R.string.status_no_signal));
            }
            if (issue.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                txtDeviceStatus.setText(getString(R.string.status_configuration_pending));
            }
        }
    }

    @Override
    protected void clearValidationErrors() {

    }

}
