package info.suedtirol.beacon.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import butterknife.BindView;
import info.suedtirol.beacon.R;

public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.container)
    protected CoordinatorLayout container;

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
        setContentView(getLayoutResourceId());
    }

    protected abstract int getLayoutResourceId();

    protected void initBottomSheet() {
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
}
