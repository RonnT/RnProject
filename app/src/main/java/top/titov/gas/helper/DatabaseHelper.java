package top.titov.gas.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import top.titov.gas.model.Region;
import top.titov.gas.model.azs.Azs;
import top.titov.gas.model.azs.Favorites;
import top.titov.gas.model.azs.PriceItem;


public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "rnbrends";
    private static final int DATABASE_VERSION = 2; // because 1 - empty db, but we have to update it initially

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private Dao<Azs, Integer> mAzsDao = null;
    private Dao<PriceItem, Integer> mPriceItemDao = null;
    private Dao<Region, Integer> mRegionDao = null;
    private Dao<Favorites, Integer> mFavoritesDao = null;

    public Dao<Azs, Integer> getAzsDao() throws SQLException {
        if (mAzsDao == null) mAzsDao = getDao(Azs.class);
        return  mAzsDao;
    }

    public Dao<PriceItem, Integer> getPriceItemDao() throws SQLException {
        if (mPriceItemDao == null) mPriceItemDao = getDao(PriceItem.class);
        return mPriceItemDao;
    }

    public Dao<Region, Integer> getRegionDao() throws SQLException {
        if (mRegionDao == null) mRegionDao = getDao(Region.class);
        return mRegionDao;
    }

    public Dao<Favorites, Integer> getFavoritesDao() throws SQLException{
        if(mFavoritesDao == null) mFavoritesDao = getDao(Favorites.class);
        return mFavoritesDao;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
    }

    @Override
    public void close() {
        super.close();
    }
}
