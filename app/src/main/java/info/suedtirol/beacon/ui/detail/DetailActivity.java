package info.suedtirol.beacon.ui.detail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import info.suedtirol.beacon.R;
import info.suedtirol.beacon.ui.BaseActivity;

public class DetailActivity extends BaseActivity {

    public static final String EXTRA_BEACON_ID = "EXTRA_BEACON_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        initBottomSheet();

        setTitle("Beacon584");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_edit:
                break;
            case R.id.menu_delete:
                break;
            case R.id.menu_exit_edit:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
