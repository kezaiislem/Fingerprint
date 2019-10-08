package net.spintechs.qimmos.fingerprint.employe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.spintechs.qimmos.fingerprint.employe.database.DbHelper;
import net.spintechs.qimmos.fingerprint.employe.model.Fingerprint;

import net.spintechs.qimmos.fingerprint.employe.database.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class FingerprintDao {

    public boolean isEmpty(Context context){
        boolean result = true;
        Cursor cursor;

        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM fingerprint");
        if(cursor.moveToNext()){
            result = false;
        }

        dbHelper.close();
        return result;
    }

    public int insertFingerprint(Fingerprint fingerprint, Context context){
        int id = -1;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fingerprint_id", fingerprint.getFingerprintId());
        values.put("update_date", fingerprint.getUpdateDate());
        values.put("user_id", fingerprint.getUserId());

        try {
            id = (int) sqLiteDatabase.insert("fingerprint", null, values);
        }catch(Exception e){
            sqLiteDatabase.close();
        }
        sqLiteDatabase.close();
        dbHelper.close();

        return id;
    }

    public int updateFingerprint(Fingerprint fingerprint, Context context){

        int result = -1;
        DbHelper dbHelper = new DbHelper(context);
        Cursor cursor = dbHelper.getData("SELECT id FROM fingerprint WHERE fingerprint_id = '"+fingerprint.getFingerprintId()+"'");
        if(cursor.moveToNext()){
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
            ContentValues args = new ContentValues();
            args.put("update_date", fingerprint.getUpdateDate());
            int n = sqLiteDatabase.update("fingerprint",args,"fingerprint_id = ? ", new String[]{fingerprint.getFingerprintId()});
            if(n>0){
                dbHelper.close();
                result = cursor.getInt(0);
            }
        }
        dbHelper.close();
        return result;
    }

    public List<Fingerprint> getFingerprintList(Context context){
        Cursor cursor;
        Fingerprint fingerprint = null;
        List<Fingerprint> list = null;

        DbHelper dbHelper = new DbHelper(context);
        cursor = dbHelper.getData("SELECT * FROM fingerprint");

        list = new ArrayList<>();

        while(cursor.moveToNext()){
            fingerprint = new Fingerprint();
            fingerprint.setId(cursor.getInt(0));
            fingerprint.setFingerprintId(cursor.getString(1));
            fingerprint.setUpdateDate(cursor.getString(2));
            fingerprint.setUserId(cursor.getString(3));
            list.add(fingerprint);
        }

        dbHelper.close();

        return list;
    }

    public boolean dropFingerprint(int id, Context context){

        boolean bool = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        int count = sqLiteDatabase.delete("fingerprint","id = ?",new String[]{""+id});
        if(count > 0 ){
            bool = true;
        }
        sqLiteDatabase.close();
        dbHelper.close();
        return bool;
    }

}
