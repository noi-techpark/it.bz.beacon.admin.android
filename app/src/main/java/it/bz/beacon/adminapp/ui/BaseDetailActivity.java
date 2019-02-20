package it.bz.beacon.adminapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;

public abstract class BaseDetailActivity extends BaseActivity {

    protected boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
    }

    protected void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.show();
    }

    protected void setUpToolbar(String title) {
        if (isEditing) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cancel);
            }
            toolbar.setTitle(getString(R.string.details_edit));
        }
        else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            }
            toolbar.setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    protected abstract void setContentEnabled(boolean enabled);
    protected abstract void quitEditMode();
    protected abstract boolean validate();
    protected abstract void save();
    protected abstract void showData();
    protected abstract void clearValidationErrors();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (isEditing) {
                    showCloseWarning();
                }
                else {
                    finish();
                }
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

    protected void showCloseWarning() {
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
                        quitEditMode();
                    }
                }).create();
        dialog.show();
    }
}
