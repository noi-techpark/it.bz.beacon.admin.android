// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.issue;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.event.LoadIssueEvent;
import it.bz.beacon.adminapp.data.viewmodel.BeaconIssueViewModel;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.model.IssueSolution;
import it.bz.beacon.adminapp.ui.BaseDetailActivity;
import it.bz.beacon.adminapp.util.DateFormatter;

public class IssueDetailActivity extends BaseDetailActivity {

    public static final String EXTRA_ISSUE_ID = "EXTRA_ISSUE_ID";

    @BindView(R.id.progress)
    protected ConstraintLayout progress;

    @BindView(R.id.progressText)
    protected TextView txtProgress;

    @BindView(R.id.scrollview)
    protected ScrollView content;

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

    @BindView(R.id.issue_solve)
    protected Button btnResolve;

    @BindView(R.id.resolve_container)
    protected LinearLayout containerResolver;

    @BindView(R.id.solution_container)
    protected TextInputLayout containerSolution;

    @BindView(R.id.solution_description_container)
    protected TextInputLayout containerSolutionDescription;

    @BindView(R.id.date)
    protected TextInputEditText editDate;

    @BindView(R.id.solution)
    protected TextInputEditText editSolution;

    @BindView(R.id.solution_description)
    protected TextInputEditText editSolutionDescription;

    private BeaconIssueViewModel beaconIssueViewModel;

    private Storage storage;
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

        storage = AdminApplication.getStorage();
        editDate.setText(DateFormatter.dateToDateString(new Date()));
        beaconIssueViewModel = ViewModelProviders.of(this).get(BeaconIssueViewModel.class);
        loadIssue();
    }

    @Override
    protected void setContentEnabled(boolean enabled) {

    }

    @Override
    protected void quitEditMode() {
        finish();
    }

    @Override
    protected boolean validate() {
        boolean valid = true;
        clearValidationErrors();

        if ((editSolution.getText() == null) || (TextUtils.isEmpty(editSolution.getText().toString()))) {
            containerSolution.setError(getString(R.string.mandatory));
            valid = false;
        }
        if ((editSolutionDescription.getText() == null) || (TextUtils.isEmpty(editSolutionDescription.getText().toString()))) {
            containerSolutionDescription.setError(getString(R.string.mandatory));
            valid = false;
        }
        return valid;
    }


    protected void save() {
        final ProgressDialog dialog = new ProgressDialog(IssueDetailActivity.this, R.style.AlertDialogCustom);

        IssueSolution issueSolution = new IssueSolution();
        issueSolution.setSolution(editSolution.getText().toString());
        issueSolution.setSolutionDescription(editSolutionDescription.getText().toString());
        issueSolution.setResolver(storage.getLoginUserName());

        dialog.setMessage(getString(R.string.saving_issue));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        try {
            AdminApplication.getIssueApi().updateUsingPOSTAsync(issueId, issueSolution, new ApiCallback<it.bz.beacon.adminapp.swagger.client.model.BeaconIssue>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if ((statusCode == 403) || (statusCode == 401)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(IssueDetailActivity.this, R.style.AlertDialogCustom));
                                builder.setMessage(getString(R.string.error_authorization));
                                builder.setCancelable(false);
                                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        AdminApplication.renewLogin(IssueDetailActivity.this);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            else {
                                showSnackbarWithRetry(getString(R.string.no_internet));
                            }
                        }
                    });
                }

                @Override
                public void onSuccess(it.bz.beacon.adminapp.swagger.client.model.BeaconIssue remoteBeaconIssue, int statusCode, Map<String, List<String>> responseHeaders) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (remoteBeaconIssue != null) {
                                BeaconIssue beaconIssue = new BeaconIssue();
                                beaconIssue.setId(remoteBeaconIssue.getId());
                                beaconIssue.setBeaconId(remoteBeaconIssue.getBeacon().getId());
                                beaconIssue.setProblem(remoteBeaconIssue.getProblem());
                                beaconIssue.setProblemDescription(remoteBeaconIssue.getProblemDescription());
                                beaconIssue.setReportDate(remoteBeaconIssue.getReportDate());
                                beaconIssue.setReporter(remoteBeaconIssue.getReporter());
                                beaconIssue.setResolved(remoteBeaconIssue.isResolved());
                                beaconIssue.setResolveDate(remoteBeaconIssue.getResolveDate());
                                beaconIssue.setSolution(remoteBeaconIssue.getSolution());
                                beaconIssue.setSolutionDescription(remoteBeaconIssue.getSolutionDescription());

                                beaconIssueViewModel.insert(beaconIssue, new InsertEvent() {
                                    @Override
                                    public void onSuccess(long id) {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                        }
                                        showToast(getString(R.string.saved), Toast.LENGTH_SHORT);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure() {
                                        showToast(getString(R.string.general_error), Toast.LENGTH_LONG);
                                    }
                                });
                            }
                            else {
                                showSnackbarWithRetry(getString(R.string.general_error));
                            }
                        }
                    });
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        }
        catch (ApiException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    showSnackbarWithRetry(getString(R.string.general_error));
                }
            });
        }
    }

    private void showSnackbarWithRetry(String message) {
        Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save();
                    }
                })
                .show();
    }

    private void showToast(String string, int length) {
        Toast.makeText(this, string, length).show();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_issue_detail;
    }

    private void loadIssue() {
        showProgress(getString(R.string.loading));

        beaconIssueViewModel.getIssueWithBeaconById(issueId, new LoadIssueEvent() {
            @Override
            public void onSuccess(IssueWithBeacon issueWithBeacon) {
                issue = issueWithBeacon;
                showData();
                resolve();
            }

            @Override
            public void onError() {
                Toast.makeText(IssueDetailActivity.this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpToolbar(getString(R.string.issue));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem saveItem = menu.findItem(R.id.menu_save);

        if (saveItem != null) {
            saveItem.setVisible(isEditing);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showProgress(String text) {
        txtProgress.setText(text);
        content.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

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
            if (issue.getStatus().equals(Beacon.STATUS_UNKNOWN_STATUS)) {
                txtDeviceStatus.setText(getString(R.string.status_unknown_status));
            }
            if (issue.getStatus().equals(Beacon.STATUS_NOT_ACCESSIBLE)) {
                txtDeviceStatus.setText(getString(R.string.status_not_accessible));
            }
            if (issue.getStatus().equals(Beacon.STATUS_CONFIGURATION_PENDING)) {
                txtDeviceStatus.setText(getString(R.string.status_configuration_pending));
            }
        }
        content.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void clearValidationErrors() {
        containerSolution.setError(null);
        containerSolutionDescription.setError(null);
    }

    @OnClick(R.id.issue_solve)
    public void resolve() {
        btnResolve.setVisibility(View.GONE);
        containerResolver.setVisibility(View.VISIBLE);
        isEditing = true;
        invalidateOptionsMenu();
        setUpToolbar(getString(R.string.issue));
    }

    @Override
    protected boolean shouldShowCloseWarning() {
        boolean shouldShow = false;

        if ((editSolution.getText() != null) && (!TextUtils.isEmpty(editSolution.getText().toString()))) {
            shouldShow = true;
        }
        if ((editSolutionDescription.getText() != null) && (!TextUtils.isEmpty(editSolutionDescription.getText().toString()))) {
            shouldShow = true;
        }
        return shouldShow;
    }
}
