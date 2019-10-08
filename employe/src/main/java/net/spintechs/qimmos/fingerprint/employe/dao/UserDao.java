package net.spintechs.qimmos.fingerprint.employe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import net.spintechs.qimmos.fingerprint.employe.database.DbHelper;
import net.spintechs.qimmos.fingerprint.employe.model.User;

import net.spintechs.qimmos.fingerprint.employe.database.DbHelper;

public class UserDao {

    public boolean isEmpty(Context context){
        boolean result = true;
        Cursor cursor;

        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM user");
        if(cursor.moveToNext()){
            result = false;
        }

        dbHelper.close();
        return result;
    }

    public boolean insertUser(User user, Context context){

        boolean result = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_id", user.getId());
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("departement", user.getDepartement());

        if(sqLiteDatabase.insert("user", null, values) > 0){
            result = true;
        }
        sqLiteDatabase.close();
        dbHelper.close();

        return result;
    }

    public User getUser(String id, Context context){

        User user = null;
        Cursor cursor;
        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM user WHERE user_id = '" +id+"'");
        if (cursor.moveToNext()) {
            user = new User();
            user.setFirstName(cursor.getString(2));
            user.setLastName(cursor.getString(3));
            user.setDepartement(cursor.getString(4));
        }
        dbHelper.close();
        return user;
    }

    public boolean reset(Context context){

        boolean result = true;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        try {
            sqLiteDatabase.execSQL("delete from user");
            sqLiteDatabase.execSQL("delete from sqlite_sequence where name='user'");
        } catch (Exception e){
            result = false;
            e.printStackTrace();
        } finally {
            sqLiteDatabase.close();
            dbHelper.close();
            return result;
        }
    }

    public long countUsers(Context context){

        long count;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        count = DatabaseUtils.queryNumEntries(sqLiteDatabase, "user");
        sqLiteDatabase.close();
        dbHelper.close();
        return count;
    }

}