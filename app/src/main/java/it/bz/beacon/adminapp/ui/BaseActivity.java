package it.bz.beacon.adminapp.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import it.bz.beacon.adminapp.R;

public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.container)
    protected CoordinatorLayout container;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    protected int toolbarHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
    }

    protected abstract int getLayoutResourceId();
}
