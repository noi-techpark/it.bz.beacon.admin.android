// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.issue.map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;
import it.bz.beacon.adminapp.data.viewmodel.BeaconIssueViewModel;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.RadiusFilterEvent;
import it.bz.beacon.adminapp.ui.issue.IssueDetailActivity;
import it.bz.beacon.adminapp.ui.map.BaseClusterItem;
import it.bz.beacon.adminapp.ui.map.BeaconInfoWindowAdapter;
import it.bz.beacon.adminapp.ui.map.ClusterRenderer;

public class IssuesMapFragment extends Fragment implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<BaseClusterItem> {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @BindView(R.id.map_view)
    protected MapView mapView;

    @BindView(R.id.map_progress_content)
    protected FrameLayout frameProgress;

    @BindView(R.id.progress)
    protected ProgressBar progress;

    private GoogleMap map;
    private ClusterManager<BaseClusterItem> clusterManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation = null;

    private BeaconIssueViewModel beaconIssueViewModel;
    private List<IssueWithBeacon> mapIssues = new ArrayList<>();
    private int radiusFilter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        beaconIssueViewModel = ViewModelProviders.of(this).get(BeaconIssueViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
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
        beaconIssueViewModel.getAllIssuesWithBeacon().observe(this, new Observer<List<IssueWithBeacon>>() {
            @Override
            public void onChanged(List<IssueWithBeacon> issueWithBeacons) {
                if (issueWithBeacons != null && issueWithBeacons.size() > 0) {
                    mapIssues.clear();
                    for (IssueWithBeacon issueWithBeacon : issueWithBeacons) {
                        if ((issueWithBeacon.getLat() != 0 && issueWithBeacon.getLng() != 0)) {
                            if (radiusFilter > 0) {
                                if (currentLocation != null) {
                                    if (distanceBetween(currentLocation, new LatLng(issueWithBeacon.getLat(), issueWithBeacon.getLng())) < radiusFilter) {
                                        mapIssues.add(issueWithBeacon);
                                    }
                                }
                            }
                            else {
                                mapIssues.add(issueWithBeacon);
                            }

                        }
                    }
                    if (map != null) {
                        setUpClusterer();
                    }
                }
            }
        });
    }

    private double distanceBetween(LatLng point1, LatLng point2) {
        float[] results = new float[1];
        Location.distanceBetween(point1.latitude, point1.longitude, point2.latitude, point2.longitude, results);
        return results[0];
    }

    @Subscribe
    public void onRadiusFilterChanged(RadiusFilterEvent event) {
        radiusFilter = event.getRadius();
        loadData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        showMyLocation();
        setUpClusterer();

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
            if (!success) {
                Log.e(AdminApplication.LOG_TAG, "Style parsing failed.");
            }
        }
        catch (Resources.NotFoundException e) {
            Log.e(AdminApplication.LOG_TAG, "Can't find style. Error: ", e);
        }
    }

    private void showMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setTitle(getString(R.string.location_permission_title))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create();
                dialog.show();
            }
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }
        else {
            map.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });
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
            if (clusterManager == null) {
                clusterManager = new ClusterManager<>(getContext(), map);
            }
            clusterManager.setAlgorithm(new GridBasedAlgorithm<BaseClusterItem>());
            clusterManager.setRenderer(new ClusterRenderer(getContext(), map, clusterManager));
            clusterManager.setOnClusterItemInfoWindowClickListener(this);
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
        for (IssueWithBeacon beacon : mapIssues) {
            IssueClusterItem clusterItem = new IssueClusterItem(beacon);
            clusterManager.addItem(clusterItem);
            boundsBuilder.include(clusterItem.getPosition());
        }
        clusterManager.cluster();

        try {
            LatLngBounds bounds = boundsBuilder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64));
        }
        catch (IllegalStateException e) {
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

    protected int getClusterColor() {
        return ContextCompat.getColor(getContext(), R.color.primary);
    }

    protected void showDetail(long id) {
        Intent intent = new Intent(getContext(), IssueDetailActivity.class);
        intent.putExtra(IssueDetailActivity.EXTRA_ISSUE_ID, id);
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
        PubSub.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        PubSub.getInstance().unregister(this);
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

    @Override
    public void onClusterItemInfoWindowClick(BaseClusterItem clusterItem) {
        showDetail(clusterItem.getId());
    }
}
