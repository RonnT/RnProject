package ru.pichesky.rosneft.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import ru.pichesky.rosneft.helper.HelperFactory;


/**
 * Created by Andrew Vasilev on 15.01.2015.
 */

@DatabaseTable(tableName = "region")
public class Region implements Serializable {
    private static final String COL_REGION_ID = "region_id";

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(columnName = COL_REGION_ID, index = true)
    private int regionId;

    public Region() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegionId() {
        return regionId;
    }

    public static List<Region> getAll() {
        try {
            Dao<Region, Integer> dao = HelperFactory.getHelper().getRegionDao();
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Region getByRegionId(int pDistrictId) {
        try {
            Dao<Region, Integer> dao = HelperFactory.getHelper().getRegionDao();
            return dao.queryBuilder()
                    .where()
                    .eq(COL_REGION_ID, pDistrictId)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
