package net.spintechs.qimmos.fingerprint.employe.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.spintechs.qimmos.fingerprint.employe.model.Fingerprint;


/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String db_name = "pfe.db";

    public DbHelper(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pointage(id INTEGER PRIMARY KEY AUTOINCREMENT, date VARCHAR, time VARCHAR, type VARCHAR, longitude DOUBLE, latitude DOUBLE, user_id VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS fingerprint(id INTEGER PRIMARY KEY AUTOINCREMENT, fingerprint_id VARCHAR, update_date VARCHAR, user_id VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY , user_id VARCHAR, first_name VARCHAR, last_name VARCHAR, departement VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public void dropPointages() {

        SQLiteDatabase database = this.getWritableDatabase();
        int n = database.delete("pointage", null, null);
        close();
    }

}
