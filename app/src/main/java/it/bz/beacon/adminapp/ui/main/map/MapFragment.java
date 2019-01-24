package it.bz.beacon.adminapp.ui.main.map;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.ui.detail.DetailActivity;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<BeaconClusterItem> {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.map_view)
    protected MapView mapView;

    @BindView(R.id.map_progress_content)
    protected FrameLayout frameProgress;

    @BindView(R.id.progress)
    protected ProgressBar progress;

    private GoogleMap map;
    private ClusterManager<BeaconClusterItem> clusterManager;

    private BeaconViewModel beaconViewModel;
    private List<BeaconMinimal> mapBeacons = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        progress.getIndeterminateDrawable().setColorFilter(getClusterColor(), PorterDuff.Mode.SRC_ATOP);
        showProgress();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        loadData();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map, menu);
    }

    private void loadData() {
        beaconViewModel.getAll().observe(this, new Observer<List<BeaconMinimal>>() {
            @Override
            public void onChanged(@Nullable List<BeaconMinimal> beacons) {
                if (beacons != null && beacons.size() > 0) {
                    mapBeacons.clear();

                    for (BeaconMinimal beaconMinimal : beacons) {
                        if (beaconMinimal.getLat() != 0 || beaconMinimal.getLng() != 0) {
                            mapBeacons.add(beaconMinimal);
                        }
                    }

                    if (map != null) {
                        setUpClusterer();
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        showMyLocation();
        setUpClusterer();
    }

    private void showMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.location_permission_title))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create();
                dialog.show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        } else {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showMyLocation();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setUpClusterer() {
        if (isAdded() && (getContext() != null)) {
            clusterManager = new ClusterManager<>(getContext(), map);
            clusterManager.setRenderer(new BeaconClusterRenderer(getContext(), map, clusterManager, getClusterColor()));
            clusterManager.setOnClusterItemInfoWindowClickListener(this);
            clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<BeaconClusterItem>() {
                @Override
                public boolean onClusterClick(Cluster<BeaconClusterItem> cluster) {
                    map.getUiSettings().setMapToolbarEnabled(false);
                    return false;
                }
            });
            clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<BeaconClusterItem>() {
                @Override
                public boolean onClusterItemClick(BeaconClusterItem poiClusterItem) {
                    map.getUiSettings().setMapToolbarEnabled(true);
                    return false;
                }
            });
            map.setOnCameraIdleListener(clusterManager);
            map.setOnMarkerClickListener(clusterManager);
            map.setOnInfoWindowClickListener(clusterManager);
            map.setInfoWindowAdapter(new BeaconInfoWindowAdapter(getLayoutInflater()));
            addMarkers();
        }
    }

    private void addMarkers() {
        clusterManager.clearItems();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (BeaconMinimal beacon : mapBeacons) {
            BeaconClusterItem clusterItem = new BeaconClusterItem(beacon);
            clusterManager.addItem(clusterItem);
            boundsBuilder.include(clusterItem.getPosition());
        }
        clusterManager.cluster();

        try {
            LatLngBounds bounds = boundsBuilder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64));
        } catch (IllegalStateException e) {
            Log.e(AdminApplication.LOG_TAG, e.getLocalizedMessage());
        }

        hideProgress();
    }

    private void showProgress() {
        frameProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        frameProgress.setVisibility(View.GONE);
    }

    public void onClusterItemInfoWindowClick(BeaconClusterItem clusterItem) {
        showDetail(clusterItem.getBeaconMinimal());
    }

    protected int getClusterColor() {
        return ContextCompat.getColor(getContext(), R.color.primary);
    }

    protected void showDetail(BeaconMinimal beaconMinimal) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BEACON_ID, beaconMinimal.getId());
        intent.putExtra(DetailActivity.EXTRA_BEACON_NAME, beaconMinimal.getName());
        getContext().startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
