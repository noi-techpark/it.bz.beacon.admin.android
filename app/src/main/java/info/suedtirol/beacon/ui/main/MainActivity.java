package info.suedtirol.beacon.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.suedtirol.beacon.R;
import info.suedtirol.beacon.ui.BaseActivity;
import info.suedtirol.beacon.ui.about.AboutFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.container)
    protected CoordinatorLayout container;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.nav_view)
    protected NavigationView navigationView;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.corners)
    protected FrameLayout corners;

    @BindView(R.id.bottom_sheet)
    protected FrameLayout bottomSheet;

    protected BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

    protected int toolbarHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initBottomSheet();
     //   initMapFragment();

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        switchFragment(getString(R.string.beacons), BeaconsFragment.newInstance());
        setupNavigationDrawer();
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        TextView textView = mNavigationView.getHeaderView(0).findViewById(R.id.username);
//        textView.setText(getString(R.string.nav_header_subtitle, SchreyoeggApplication.getStorage().getUser().getName()));
    }

    private void initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        ViewTreeObserver vto = container.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (getSupportActionBar() != null) {
                    toolbarHeight = getSupportActionBar().getHeight();
                    bottomSheetBehavior.setPeekHeight(bottomSheet.getMeasuredHeight() - toolbarHeight);
//                    if (googleMap != null) {
//                        googleMap.setPadding(0, 0, 0, toolbarHeight);
//                    }
                }
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View view, int i) {
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
//                if (googleMap != null) {
//                    int paddingBottom = (int)((1 - v) * toolbarHeight);
//                    googleMap.setPadding(0, 0, 0, paddingBottom);
//
//                    corners.setAlpha((1 - v));
//                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_map:
                break;
            case R.id.menu_list:
                break;
            case R.id.menu_search:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        navigationView.setCheckedItem(id);

        switch (id) {
            case R.id.navigation_beacons:
                switchFragment(getString(R.string.beacons), BeaconsFragment.newInstance());
                break;
            case R.id.navigation_problems:
                break;
            case R.id.navigation_disturbances:
                break;
            case R.id.navigation_about:
                switchFragment(getString(R.string.about), AboutFragment.newInstance());
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void switchFragment(final String title, final Fragment fragment) {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        if (getSupportFragmentManager() != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerView, fragment)
                    .commit();
        }
    }
}
