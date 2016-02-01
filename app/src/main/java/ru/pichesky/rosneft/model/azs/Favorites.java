package ru.pichesky.rosneft.model.azs;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

import ru.pichesky.rosneft.helper.HelperFactory;

/**
 * Created by Dmitry on 03.11.2015.
 */
@DatabaseTable(tableName = "favorites")
public class Favorites {
    @DatabaseField(id = true)
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public static void saveFavorites(int data) {
        try {
            Dao<Favorites, Integer> dao = HelperFactory.getHelper().getFavoritesDao();
            Favorites favorites = new Favorites();
            favorites.setId(data);
            dao.createOrUpdate(favorites);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFavorites(int data){
        try {
            Dao<Favorites, Integer> dao = HelperFactory.getHelper().getFavoritesDao();
            dao.deleteById(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
