package it.bz.beacon.adminapp.ui.main.beacon.map;

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

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
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
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.StatusFilterEvent;
import it.bz.beacon.adminapp.ui.detail.DetailActivity;
import it.bz.beacon.adminapp.ui.map.BaseClusterItem;
import it.bz.beacon.adminapp.ui.map.BeaconInfoWindowAdapter;
import it.bz.beacon.adminapp.ui.map.ClusterRenderer;
import it.bz.beacon.beaconsuedtirolsdk.NearbyBeaconManager;
import it.bz.beacon.beaconsuedtirolsdk.data.event.LoadAllBeaconsEvent;

public class MapFragment extends Fragment implements OnMapReadyCallback,
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

    private BeaconViewModel beaconViewModel;
    private Map<String, it.bz.beacon.beaconsuedtirolsdk.data.entity.Beacon> infos = new HashMap<>();
    private List<BeaconMinimal> mapBeacons = new ArrayList<>();
    private String statusFilter = Beacon.STATUS_ALL;

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
        infos.clear();
        if (getContext() != null) {
            NearbyBeaconManager.getInstance().getAllBeacons(new LoadAllBeaconsEvent() {
                @Override
                public void onSuccess(List<it.bz.beacon.beaconsuedtirolsdk.data.entity.Beacon> list) {
                    if (list != null) {
                        MapFragment.this.infos =
                                list.stream().collect(Collectors.toMap(it.bz.beacon.beaconsuedtirolsdk.data.entity.Beacon::getId, Function.identity()));
                    }

                    loadBeaconData();
                }

                @Override
                public void onError() {
                    loadBeaconData();
                }
            });
        }
    }

    private void loadBeaconData() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    beaconViewModel.getAll().observe(MapFragment.this, new Observer<List<BeaconMinimal>>() {
                        @Override
                        public void onChanged(@Nullable List<BeaconMinimal> beacons) {
                            if (beacons != null && beacons.size() > 0) {
                                mapBeacons.clear();
                                for (BeaconMinimal beaconMinimal : beacons) {
                                    if (beaconMinimal.getLat() == 0 && beaconMinimal.getLng() == 0) {
                                        beaconMinimal.setStatus(Beacon.STATUS_NOT_INSTALLED);
                                    }

                                    if (statusFilter.equals(Beacon.STATUS_INSTALLED)) {
                                        if (beaconMinimal.getLat() != 0 || beaconMinimal.getLng() != 0) {
                                            mapBeacons.add(beaconMinimal);
                                        }
                                    } else if (beaconMinimal.getStatus().equalsIgnoreCase(statusFilter) || statusFilter.equals(Beacon.STATUS_ALL)) {
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
            });
        }
    }

    @Subscribe
    public void onStatusFilterChanged(StatusFilterEvent event) {
        statusFilter = event.getStatus();
        loadData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        startLocating();
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



    private LocationRequest locationRequest = new LocationRequest();
    private LocationCallback locationCallback = new LocationCallback();

    private void startLocating() {
        if (getActivity() != null && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                        .setTitle(getString(R.string.location_permission_title))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                            }
                        })
                        .create();
                dialog.show();
            }
            else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            }
        } else {
            doStartLocating();
        }
    }

    @RequiresPermission(value = Manifest.permission.ACCESS_FINE_LOCATION)
    private void doStartLocating() {
        if (map != null) {
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);
        }
    }

    private void stopLocating() {
        map.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            map.setMyLocationEnabled(false);
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocating();
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
        for (BeaconMinimal beacon : mapBeacons) {
            if (beacon.getStatus().equalsIgnoreCase(Beacon.STATUS_NOT_INSTALLED)) {
                it.bz.beacon.beaconsuedtirolsdk.data.entity.Beacon info = infos.get(beacon.getId());
                if (info != null) {
                    beacon.setProvisoricLat(info.getLatitude().floatValue());
                    beacon.setProvisoricLng(info.getLongitude().floatValue());
                }
            }
            BeaconClusterItem clusterItem = new BeaconClusterItem(beacon);
            if (clusterItem.getPosition() != null) {
                clusterManager.addItem(clusterItem);
                boundsBuilder.include(clusterItem.getPosition());
            }
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

    protected void showDetail(String id, String name) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_BEACON_ID, id);
        intent.putExtra(DetailActivity.EXTRA_BEACON_NAME, name);
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
        startLocating();
        mapView.onResume();
        PubSub.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocating();
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
        showDetail(clusterItem.getBeaconId(), clusterItem.getTitle());
    }
}
