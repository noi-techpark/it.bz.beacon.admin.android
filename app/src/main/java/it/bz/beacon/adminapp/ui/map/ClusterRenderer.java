// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.util.BitmapTools;

public class ClusterRenderer extends DefaultClusterRenderer<BaseClusterItem> {

    private Context context;
    private final IconGenerator clusterIconGenerator;

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<BaseClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.clusterIconGenerator = new IconGenerator(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(BaseClusterItem item, MarkerOptions markerOptions) {
        final Drawable drawable = ContextCompat.getDrawable(context, Beacon.getMarkerId(item.getStatus()));
        Bitmap bitmap = BitmapTools.drawableToBitmap(drawable);
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOptions.icon(markerDescriptor);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<BaseClusterItem> cluster, MarkerOptions markerOptions){

        final Drawable clusterIcon = ContextCompat.getDrawable(context, R.drawable.ic_cluster);

        clusterIconGenerator.setBackground(clusterIcon);

        //modify padding for one or two digit numbers
        if (cluster.getSize() < 10) {
            clusterIconGenerator.setContentPadding(54, 36, 0, 0);
        } else if (cluster.getSize() < 100) {
            clusterIconGenerator.setContentPadding(50, 36, 0, 0);
        } else if (cluster.getSize() < 1000) {
            clusterIconGenerator.setContentPadding(38, 36, 0, 0);
        } else {
            clusterIconGenerator.setContentPadding(26, 36, 0, 0);
        }
        clusterIconGenerator.setTextAppearance(context, R.style.MapClusterAppearance);

        Bitmap icon = clusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<BaseClusterItem> cluster) {
        return cluster.getSize() > 10;
    }
}
