package top.titov.gas.extension;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import top.titov.gas.R;
import top.titov.gas.helper.BitmapHelper;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.utils.CONST;

/**
 * Created by Andrew Vasilev on 24.07.2015.
 */
public class AzsClusterRenderer extends DefaultClusterRenderer<Azs>{

    private Context mContext;

    public AzsClusterRenderer(Context pContext, GoogleMap pMap, ClusterManager<Azs> pClusterManager) {
        super(pContext, pMap, pClusterManager);

        mContext = pContext;
    }

    @Override
    protected void onBeforeClusterItemRendered(Azs azs, MarkerOptions markerOptions) {
        markerOptions.anchor(0.5F, 0.5F);
        float price = azs.isUpdatedPrice() ? azs.getSelectedFuelPrice() : 0;
        int id = azs.getId();
        int brand = azs.getBrand();

        if (price == 0) {
            int ic_type = brand == CONST.AZS_TYPE_0 ? R.drawable.ic_azs_type_0 : R.drawable.ic_azs_type_1;
            markerOptions.icon(BitmapDescriptorFactory.fromResource(ic_type));
        } else {
            Bitmap icon = BitmapHelper.getAzsMarkerWithPrice(mContext, price, azs.isFavorite(), brand);
            if (icon != null) markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Azs> cluster, MarkerOptions markerOptions) {
        Bitmap iconWithCount = BitmapHelper.getAzsMarkerClustered(mContext, cluster.getSize());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconWithCount));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() >= 2;
    }
}
