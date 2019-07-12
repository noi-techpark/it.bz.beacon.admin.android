package it.bz.beacon.adminapp.ui.issue;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.swagger.client.ApiCallback;
import it.bz.beacon.adminapp.swagger.client.ApiException;
import it.bz.beacon.adminapp.swagger.client.model.IssueCreation;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.Storage;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.viewmodel.BeaconIssueViewModel;
import it.bz.beacon.adminapp.ui.BaseActivity;
import it.bz.beacon.adminapp.util.DateFormatter;

import static it.bz.beacon.adminapp.ui.detail.DetailActivity.EXTRA_BEACON_ID;

public class NewIssueActivity extends BaseActivity {

    @BindView(R.id.problem_name_container)
    protected TextInputLayout containerName;

    @BindView(R.id.problem_description_container)
    protected TextInputLayout containerDescription;

    @BindView(R.id.problem_name)
    protected TextInputEditText editName;

    @BindView(R.id.problem_description)
    protected TextInputEditText editDescription;

    @BindView(R.id.reporter)
    protected TextInputEditText editReporter;

    @BindView(R.id.report_date)
    protected TextInputEditText editReportDate;

    private Storage storage;
    private String beaconId;
    private BeaconIssueViewModel beaconIssueViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setUpToolbar();

        if (getIntent() != null) {
            beaconId = getIntent().getStringExtra(EXTRA_BEACON_ID);
        }

        storage = AdminApplication.getStorage();
        beaconIssueViewModel = ViewModelProviders.of(this).get(BeaconIssueViewModel.class);

        editReporter.setText(storage.getLoginUserName());
        editReportDate.setText(DateFormatter.dateToDateString(new Date()));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_new_issue;
    }

    private void setUpToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                AdminApplication.hideKeyboard(this);
                showCloseWarning();
                return true;
            case R.id.menu_save:
                AdminApplication.hideKeyboard(this);
                if (validate()) {
                    save();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        boolean valid = true;
        clearValidationErrors();

        if ((editName.getText() == null) || (TextUtils.isEmpty(editName.getText().toString()))) {
            containerName.setError(getString(R.string.mandatory));
            valid = false;
        }
        if ((editDescription.getText() == null) || (TextUtils.isEmpty(editDescription.getText().toString()))) {
            containerDescription.setError(getString(R.string.mandatory));
            valid = false;
        }
        return valid;
    }

    protected void save() {
        final ProgressDialog dialog = new ProgressDialog(NewIssueActivity.this, R.style.AlertDialogCustom);

        IssueCreation issueCreation = new IssueCreation();
        issueCreation.setBeaconId(beaconId);
        issueCreation.setProblem(editName.getText().toString());
        issueCreation.setProblemDescription(editDescription.getText().toString());
        issueCreation.setReporter(storage.getLoginUserName());

        dialog.setMessage(getString(R.string.saving_issue));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        try {
            AdminApplication.getIssueApi().createUsingPOST2Async(issueCreation, new ApiCallback<it.bz.beacon.adminapp.swagger.client.model.BeaconIssue>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if ((statusCode == 403) || (statusCode == 401)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(NewIssueActivity.this, R.style.AlertDialogCustom));
                                builder.setMessage(getString(R.string.error_authorization));
                                builder.setCancelable(false);
                                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        AdminApplication.renewLogin(NewIssueActivity.this);
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

    private void clearValidationErrors() {
        containerName.setError(null);
        containerDescription.setError(null);
    }

    private void showCloseWarning() {
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setMessage(R.string.close_warning)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).create();
        dialog.show();
    }
}
