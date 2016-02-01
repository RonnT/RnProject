package ru.pichesky.rosneft.model.azs;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import ru.pichesky.rosneft.helper.HelperFactory;
import ru.pichesky.rosneft.utils.CONST;
import ru.pichesky.rosneft.utils.Utility;

/**
 * Created by Andrew Vasilev on 16.07.2015.
 */
@DatabaseTable(tableName = "fuel_price")
public class PriceItem implements Serializable{

    @DatabaseField(id = true)
    private int id;

    @SerializedName("price_list")
    private Fuel priceList;

    @DatabaseField(columnName = "price_date")
    @SerializedName("price_date")
    private long priceDate;

    @DatabaseField(columnName = "92")
    private float f92;

    @DatabaseField(columnName = "95")
    private float f95;

    @DatabaseField(columnName = "98")
    private float f98;

    @DatabaseField(columnName = "diesel")
    private float fDiesel;

    @DatabaseField(columnName = "92_fora")
    private float f92Fora;

    @DatabaseField(columnName = "95_fora")
    private float f95Fora;

    @DatabaseField(columnName = "98_fora")
    private float f98Fora;

    @DatabaseField(columnName = "diesel_fora")
    private float fDieselFora;

    @DatabaseField(columnName = "gas")
    private float fGas;

    @DatabaseField(columnName = "last_update")
    private long lastUpdate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float get92() {
        return f92;
    }

    public void set92(float f92) {
        this.f92 = f92;
    }

    public float get95() {
        return f95;
    }

    public void set95(float f95) {
        this.f95 = f95;
    }

    public float get98() {
        return f98;
    }

    public void set98(float f98) {
        this.f98 = f98;
    }

    public float getDiesel() {
        return fDiesel;
    }

    public void setDiesel(float fDiesel) {
        this.fDiesel = fDiesel;
    }

    public float get92Fora() {
        return f92Fora;
    }

    public void set92Fora(float f92Fora) {
        this.f92Fora = f92Fora;
    }

    public float get95Fora() {
        return f95Fora;
    }

    public void set95Fora(float f95Fora) {
        this.f95Fora = f95Fora;
    }

    public float get98Fora() {
        return f98Fora;
    }

    public void set98Fora(float f98Fora) {
        this.f98Fora = f98Fora;
    }

    public float getDieselFora() {
        return fDieselFora;
    }

    public void setDieselFora(float fDieselFora) {
        this.fDieselFora = fDieselFora;
    }

    public float getGas() {
        return fGas;
    }

    public void setGas(float fGas) {
        this.fGas = fGas;
    }

    public long getPriceDate() {
        return priceDate;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Map<String,Float> getPriceList() {
        Map<String, Float> map = new HashMap<>();
        map.put(CONST.FUEL_92, f92);
        map.put(CONST.FUEL_95, f95);
        map.put(CONST.FUEL_98, f98);
        map.put(CONST.FUEL_DIESEL, fDiesel);
        map.put(CONST.FUEL_92_FORA, f92Fora);
        map.put(CONST.FUEL_95_FORA, f95Fora);
        map.put(CONST.FUEL_98_FORA, f98Fora);
        map.put(CONST.FUEL_DIESEL_FORA, fDieselFora);
        map.put(CONST.FUEL_GAS, fGas);
        return map;
    }

    private void copyPriceFuel(){
        f92 = priceList.f92;
        f95 = priceList.f95;
        f98 = priceList.f98;
        fDiesel = priceList.fDiesel;
        f92Fora = priceList.f92Fora;
        f95Fora = priceList.f95Fora;
        f98Fora = priceList.f98Fora;
        fDieselFora = priceList.fDieselFora;
        fGas = priceList.fGas;
    }

    public void save(Dao<PriceItem, Integer> pDao) throws SQLException {
        pDao.createOrUpdate(this);
    }

    public static boolean updateAll(final List<PriceItem> pPriceItemList) {
        if (pPriceItemList == null || pPriceItemList.size() == 0) return true;
        final long currenTimestamp = Utility.getCurrentTimestamp();
        try {
            final Dao<PriceItem, Integer> dao = HelperFactory.getHelper().getPriceItemDao();
            dao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (PriceItem priceItem : pPriceItemList) {
                        if(priceItem.priceList != null ) {
                            priceItem.copyPriceFuel();
                            priceItem.setLastUpdate(currenTimestamp);
                            priceItem.save(dao);
                        }
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

    public static PriceItem getPriceItemById(int pId){
        try {
            Dao<PriceItem, Integer> dao = HelperFactory.getHelper().getPriceItemDao();
            return dao.queryForId(pId);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return new PriceItem();
    }

    private class Fuel{
        @SerializedName("92")
        private float f92;
        @SerializedName("95")
        private float f95;
        @SerializedName("98")
        private float f98;
        @SerializedName("diesel")
        private float fDiesel;
        @SerializedName("92_fora")
        private float f92Fora;
        @SerializedName("95_fora")
        private float f95Fora;
        @SerializedName("98_fora")
        private float f98Fora;
        @SerializedName("diesel_fora")
        private float fDieselFora;
        @SerializedName("gas")
        private float fGas;
    }
}
