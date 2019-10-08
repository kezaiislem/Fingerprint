package net.spintechs.qimmos.fingerprint.employe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.spintechs.qimmos.fingerprint.employe.database.DbHelper;
import net.spintechs.qimmos.fingerprint.employe.model.Pointage;
import net.spintechs.qimmos.fingerprint.employe.model.User;

import net.spintechs.qimmos.fingerprint.employe.database.DbHelper;
import net.spintechs.qimmos.fingerprint.employe.model.Pointage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class PointageDao {

    public boolean isEmpty(Context context) {
        boolean result = true;
        Cursor cursor;

        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM pointage");
        if (cursor.moveToNext()) {
            result = false;
        }
        dbHelper.close();

        return result;
    }

    public boolean insertPointage(Pointage pointage, Context context) {

        int id = -1;
        boolean result = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", pointage.getDate());
        values.put("time", pointage.getTime());
        values.put("type", pointage.getType());
        values.put("longitude", pointage.getLongitude());
        values.put("latitude", pointage.getLatitude());
        values.put("user_id", pointage.getUser().getId());

        try {
            id = (int) sqLiteDatabase.insert("pointage", null, values);
            if (id != -1) {
                result = true;
            }
        } catch (Exception e) {

        } finally {
            sqLiteDatabase.close();
            dbHelper.close();
            return result;
        }
    }

    public List<Pointage> getPointageList(Context context) {
        Cursor cursor;
        Pointage pointage = null;
        List<Pointage> list = null;

        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM pointage");

        list = new ArrayList<>();

        while (cursor.moveToNext()) {
            pointage = new Pointage();
            pointage.setId(cursor.getInt(0));
            pointage.setDate(cursor.getString(1));
            pointage.setTime(cursor.getString(2));
            pointage.setType(cursor.getString(3));
            pointage.setLongitude(cursor.getDouble(4));
            pointage.setLatitude(cursor.getDouble(5));
            User user = new User();
            user.setId(cursor.getString(6));
            pointage.setUser(user);
            list.add(pointage);
        }

        dbHelper.close();

        return list;
    }

    public String getUserId(int id, Context context) {
        String result = null;
        Cursor cursor;
        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM fingerprint WHERE id = " + id);
        if (cursor.moveToNext()) {
            result = cursor.getString(3);
        }
        dbHelper.close();
        return result;
    }

    public void dropPointages(Context context) {

        DbHelper dbHelper = new DbHelper(context);
        dbHelper.dropPointages();
        dbHelper.close();

    }

    public boolean dropPointage(int id, Context context) {

        boolean bool = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        int count = sqLiteDatabase.delete("pointage", "id = ?", new String[]{"" + id});
        if (count > 0) {
            bool = true;
        }
        sqLiteDatabase.close();
        dbHelper.close();
        return bool;
    }

}
