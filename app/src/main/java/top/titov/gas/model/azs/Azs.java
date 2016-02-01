package top.titov.gas.model.azs;

import android.location.Location;

import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import top.titov.gas.helper.FilterHelper;
import top.titov.gas.helper.HelperFactory;
import top.titov.gas.helper.PrefsHelper;
import top.titov.gas.utils.CONST;
import top.titov.gas.utils.Utility;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
@DatabaseTable(tableName = "azs")
public class Azs implements ClusterItem, Serializable {

    private static final String ACTION_UPDATE = "update";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_ADD = "add";

    private static final String COL_LATITUDE = "lat";
    private static final String COL_LONGITUDE = "lng";
    private static final String WASH = "wash";
    private static final String SHOP = "shop";
    private static final String CAFE = "cafe";
    private static final String TIRE = "tire";

    @DatabaseField(generatedId = true)
    private int ids;

    @DatabaseField()
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private int brand;

    @DatabaseField
    private String address;

    @DatabaseField
    private String contacts;

    @DatabaseField
    private String email;

    @DatabaseField
    private String lat;

    @DatabaseField
    private String lng;

    @DatabaseField(columnName = "reg_price")
    @SerializedName("reg_price")
    private String regPrice;

    @DatabaseField(columnName = "mid_price")
    @SerializedName("mid_price")
    private String midPrice;

    @DatabaseField(columnName = "pre_price")
    @SerializedName("pre_price")
    private String prePrice;

    @DatabaseField(columnName = "diesel_price")
    @SerializedName("diesel_price")
    private String dieselPrice;

    @DatabaseField(defaultValue = "false")
    private boolean isFavorite;

    @DatabaseField(defaultValue = "0")
    private int shop;

    @DatabaseField(defaultValue = "0")
    private int cafe;

    @DatabaseField(defaultValue = "0")
    private int wash;

    @DatabaseField(defaultValue = "0")
    private int tire;

    @DatabaseField(defaultValue = "0")
    private int visa;

    @DatabaseField(columnName = "92", defaultValue = "0")
    private int f92;

    @DatabaseField(columnName = "95", defaultValue = "0")
    private int f95;

    @DatabaseField(columnName = "98", defaultValue = "0")
    private int f98;

    @DatabaseField(columnName = "diesel", defaultValue = "0")
    private int fDiesel;

    @DatabaseField(columnName = "gas", defaultValue = "0")
    private int fGas;

    @SerializedName("available")
    private FuelAvailable fuelAvailable;

    @SerializedName("services")
    private ServiceAvailable serviceAvailable;

    private String action = ACTION_ADD;

    private float distances;

    private transient Location mLocation = null;

    protected static Dao<Azs, Integer> sDao;

    private static final int
            AZS_SAVED = 1,
            AZS_DELETED = 1;

    static {
        try {
            sDao = HelperFactory.getHelper().getAzsDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return Integer.getInteger(id);
    }

    public String getName() {
        return name;
    }

    public int getBrand() {
        return brand;
    }

    public String getAddress() {
        return address;
    }

    public String getContacts() {
        return contacts;
    }

    public String getEmail() {
        return email;
    }

    public double getLat() {
        return Double.valueOf(lat);
    }

    public double getLng() {
        return Double.valueOf(lng);
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Map<String, Integer> getServiceAvailable() {
        Map<String, Integer> map = new HashMap<>();
        map.put(CONST.FILTER_WASH, getWash());
        map.put(CONST.FILTER_TIRE, getTire());
        map.put(CONST.FILTER_CAFE, getCafe());
        map.put(CONST.FILTER_SHOP, getShop());
        map.put(CONST.FILTER_VISA, getVisa());
        return map;
    }

    public Map<String, Integer> getFuelAvailable() {
        Map<String, Integer> map = new HashMap<>();
        map.put(CONST.FILTER_FUEL_92, getF92());
        map.put(CONST.FILTER_FUEL_95, getF95());
        map.put(CONST.FILTER_FUEL_98, getF98());
        map.put(CONST.FILTER_FUEL_DIESEL, getfDiesel());
        map.put(CONST.FILTER_FUEL_GAS, getfGas());
        return map;

    }

    public float getDistance() {
        return distances;                               //TODO calculate distance
    }

    public Location getLocation() {
        if (mLocation != null) return mLocation;

        mLocation = new Location("Point");
        mLocation.setLatitude(getLat());
        mLocation.setLongitude(getLng());

        return mLocation;
    }

    public float getSelectedFuelPrice() {
        return PriceItem.getPriceItemById(Integer.valueOf(id)).getPriceList().get(FilterHelper.getFuelTypeForApi());
    }

    public float getFuelPrice(String pFuelForHuman) {
        return PriceItem.getPriceItemById(Integer.valueOf(id)).getPriceList().get(FilterHelper.getFuelTypeForApi(pFuelForHuman));
    }

    public float getForaFuelPrice(String pFuelForHuman) {
        String latinFuelName = FilterHelper.getFuelTypeForApi(pFuelForHuman) + "_fora";
        Map<String,Float> priceList = PriceItem.getPriceItemById(Integer.valueOf(id)).getPriceList();
        float price = priceList.containsKey(latinFuelName) ? priceList.get(latinFuelName) : 0;
        return price;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(getLat(), getLng());
    }

    public void update(Dao<Azs, Integer> pDao) throws Exception {
        if (action.equals(ACTION_ADD) ||
                action.equals(ACTION_UPDATE)) save(pDao);
        else if (action.equals(ACTION_DELETE)) delete(pDao);
        else throw new Exception("Action error");
    }

    public static boolean updateAll(final List<Azs> pAzsList) {

        if (pAzsList == null || pAzsList.size() == 0) return true;

        try {
            sDao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (Azs azsItem : pAzsList) {
                        azsItem.update(sDao);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void save(Dao<Azs, Integer> pDao) throws SQLException {
        pDao.createOrUpdate(this);
    }

    public void delete(Dao<Azs, Integer> pDao) throws SQLException {
        pDao.delete(this);
    }

    public static Azs getById(int pId) {
        try {
            return sDao.queryForId(pId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteById(int pId) {
        try {
            return sDao.deleteById(pId) == AZS_DELETED;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Azs> getAll() {
        try {
            return sDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUpdatedPrice(){
        PriceItem priceItem = new PriceItem().getPriceItemById(Integer.valueOf(id));
        if(priceItem == null || priceItem.getLastUpdate() + CONST.FUEL_PRICE_UPDATE_INTERVAL < Utility.getCurrentTimestamp())
            return false;
        return true;

    }

    public static List<Azs> getAzsOnMapWithFilter(Projection pMapProjection) {
        VisibleRegion visibleRegion = pMapProjection.getVisibleRegion();
        double leftLng = visibleRegion.farLeft.longitude;
        double rightLng = visibleRegion.farRight.longitude;
        double topLat = visibleRegion.farLeft.latitude;
        double bottomLat = visibleRegion.nearLeft.latitude;
        List<Azs> result = new ArrayList<>();
        try {
            Where where = sDao.queryBuilder().where();
            where.gt(COL_LATITUDE, bottomLat).and();
            where.lt(COL_LATITUDE, topLat).and();
            where.gt(COL_LONGITUDE, leftLng).and();
            where.lt(COL_LONGITUDE, rightLng).and();
            where.eq(FilterHelper.getFuelTypeForApi(PrefsHelper.getInstance().getFilterFuelType()), 1);
            if(PrefsHelper.getInstance().getFilterWash())
                where.and().eq(WASH, 1);
            if(PrefsHelper.getInstance().getFilterService())
                where.and().eq(TIRE, 1);
            if(PrefsHelper.getInstance().getFilterShop())
                where.and().eq(SHOP, 1);
            if(PrefsHelper.getInstance().getFilterCafe())
                where.and().eq(CAFE, 1);
            result.addAll(where.query());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isEmpty() {
        try {
            return sDao.countOf() == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void addFavorite() {
        Favorites.saveFavorites(Integer.valueOf(id));
        setIsFavorite(true);
        try {
            sDao.update(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFavorite() {
        Favorites.deleteFavorites(Integer.valueOf(id));
        setIsFavorite(false);
        try {
            sDao.update(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public static List<Azs> getFavorites() {
        try {
            return sDao.queryBuilder().where().eq("isFavorite", true).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAction() {
        return action;
    }

    public int getShop() {
        return shop;
    }

    public void setShop(int shop) {
        this.shop = shop;
    }

    public int getCafe() {
        return cafe;
    }

    public void setCafe(int cafe) {
        this.cafe = cafe;
    }

    public int getWash() {
        return wash;
    }

    public void setWash(int wash) {
        this.wash = wash;
    }

    public int getTire() {
        return tire;
    }

    public void setTire(int tire) {
        this.tire = tire;
    }

    public int getVisa() {
        return visa;
    }

    public void setVisa(int visa) {
        this.visa = visa;
    }

    public int getF92() {
        return f92;
    }

    public void setF92(int f92) {
        this.f92 = f92;
    }

    public int getF95() {
        return f95;
    }

    public void setF95(int f95) {
        this.f95 = f95;
    }

    public int getF98() {
        return f98;
    }

    public void setF98(int f98) {
        this.f98 = f98;
    }

    public int getfDiesel() {
        return fDiesel;
    }

    public void setfDiesel(int fDiesel) {
        this.fDiesel = fDiesel;
    }

    public int getfGas() {
        return fGas;
    }

    public void setfGas(int fGas) {
        this.fGas = fGas;
    }

    public void copyServiceAvailable() {
        setShop(serviceAvailable.getShop());
        setCafe(serviceAvailable.getCafe());
        setWash(serviceAvailable.getWash());
        setTire(serviceAvailable.getTire());
        setVisa(serviceAvailable.getVisa());
    }

    public void copyFuelAvailable(){
        setF92(fuelAvailable.getF92());
        setF95(fuelAvailable.getF95());
        setF98(fuelAvailable.getF98());
        setfDiesel(fuelAvailable.getfDiesel());
        setfGas(fuelAvailable.getfGas());
    }

    private class ServiceAvailable implements Serializable {
        private int shop;
        private int cafe;
        private int wash;
        private int tire;
        private int visa;

        public int getShop() {
            return shop;
        }

        public void setShop(int shop) {
            this.shop = shop;
        }

        public int getCafe() {
            return cafe;
        }

        public void setCafe(int cafe) {
            this.cafe = cafe;
        }

        public int getWash() {
            return wash;
        }

        public void setWash(int wash) {
            this.wash = wash;
        }

        public int getTire() {
            return tire;
        }

        public void setTire(int tire) {
            this.tire = tire;
        }

        public int getVisa() {
            return visa;
        }

        public void setVisa(int visa) {
            this.visa = visa;
        }
    }

    public class FuelAvailable implements Serializable{
        @SerializedName("92")
        private int f92;
        @SerializedName("95")
        private int f95;
        @SerializedName("98")
        private int f98;
        @SerializedName("diesel")
        private int fDiesel;
        @SerializedName("gas")
        private int fGas;

        public int getF92() {
            return f92;
        }

        public void setF92(int f92) {
            this.f92 = f92;
        }

        public int getF95() {
            return f95;
        }

        public void setF95(int f95) {
            this.f95 = f95;
        }

        public int getF98() {
            return f98;
        }

        public void setF98(int f98) {
            this.f98 = f98;
        }

        public int getfDiesel() {
            return fDiesel;
        }

        public void setfDiesel(int fDiesel) {
            this.fDiesel = fDiesel;
        }

        public int getfGas() {
            return fGas;
        }

        public void setfGas(int fGas) {
            this.fGas = fGas;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Azs azs = (Azs) o;

        return id == azs.id;

    }
}
