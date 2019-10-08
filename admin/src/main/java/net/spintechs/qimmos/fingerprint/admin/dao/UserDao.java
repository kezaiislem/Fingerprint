package net.spintechs.qimmos.fingerprint.admin.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.spintechs.qimmos.fingerprint.admin.database.DbHelper;
import net.spintechs.qimmos.fingerprint.admin.model.User;
import net.spintechs.qimmos.fingerprint.admin.tools.Params;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class UserDao {

    public boolean isEmpty(Context context) {
        boolean result = true;
        Cursor cursor;

        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM user");
        if (cursor.moveToNext()) {
            result = false;
        }

        dbHelper.close();
        return result;
    }

    public boolean insertUser(User user, Context context) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.PARAM_ID, user.getId());
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put(Params.PARAM_EMAIL, user.getEmail());
        values.put(Params.PARAM_DEPARTEMENT, user.getDepartement());
        values.put(Params.PARAM_RECRUTMENT_DATE, user.getRecrutmentDate());
        values.put(Params.PARAM_CREATED_ON, user.getCreatedOn());
        if (sqLiteDatabase.insert("user", null, values) > 0) {
            sqLiteDatabase.close();
            dbHelper.close();
            return true;
        }
        sqLiteDatabase.close();
        dbHelper.close();
        return false;

    }

    public List<User> getUsersList(Context context) {
        Cursor cursor;
        User user = null;
        List<User> list = null;

        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM user");

        list = new ArrayList<>();

        while (cursor.moveToNext()) {
            user = new User();
            user.setId(cursor.getString(0));
            user.setFirstName(cursor.getString(1));
            user.setLastName(cursor.getString(2));
            user.setEmail(cursor.getString(3));
            user.setDepartement(cursor.getString(4));
            user.setRecrutmentDate(cursor.getString(5));
            user.setCreatedOn(cursor.getString(6));
            list.add(user);
        }

        dbHelper.close();

        return list;
    }

    public void dropUser(Context context) {

        DbHelper dbHelper = new DbHelper(context);
        dbHelper.dropTable();

    }

}
