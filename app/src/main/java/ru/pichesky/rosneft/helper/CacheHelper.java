package ru.pichesky.rosneft.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Yagupov Ruslan on 11.08.2015.
 */
public class CacheHelper {

    private static CacheHelper sInstance = null;

    private CacheHelper() {}

    public static CacheHelper getInstance() {
        if (sInstance == null) sInstance = new CacheHelper();
        return sInstance;
    }

    public <T extends Serializable> boolean saveObject(File pFile, T pObject) {

        if (!pFile.exists()) return false;

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pFile));
            out.writeObject(pObject);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public  <T extends Serializable> T loadObject(File pOpenedFile) {
        T object = null;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(pOpenedFile));
            object = (T) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}
