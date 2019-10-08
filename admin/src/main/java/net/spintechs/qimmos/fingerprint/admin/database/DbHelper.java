package net.spintechs.qimmos.fingerprint.admin.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String db_name = "pfe.db";
    private static final String insertUserQuery = "INSERT INTO user VALUES ( ?, ?, ?, ?, ?, ?)";

    public DbHelper(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user(id VARCHAR PRIMARY KEY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, departement VARCHAR, recrutment_date VARCHAR, created_on VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public void dropTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS user");
        onCreate(database);
    }

}
