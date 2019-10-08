package net.spintechs.qimmos.fingerprint.admin;

import android.content.Context;
import android.content.SharedPreferences;

import net.spintechs.qimmos.fingerprint.admin.tools.Params;

/**
 * Created by hamada on 05/05/2018.
 */

public class SharedPrefs {

    final static String FileName="Session";

    public static String readShared(Context context,String settingname,String defaultValue){
        SharedPreferences sharedPrefs = context.getSharedPreferences(FileName,Context.MODE_PRIVATE);
        return sharedPrefs.getString(settingname,defaultValue);
    }

    public static void saveShared(Context context,String settingname,String settingValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(FileName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(settingname,settingValue);
        editor.apply();
    }

    public static void SharedPrefesSAVE(Context ctx,String email,String lastename,String firstname,String token,String dateexpired){
        SharedPreferences prefs = ctx.getSharedPreferences("LOGIN", 0);
        SharedPreferences.Editor prefEDIT = prefs.edit();
        prefEDIT.putString(Params.PARAM_EMAIL, email);
        prefEDIT.putString(Params.PARAM_LASTE_NAME, lastename);
        prefEDIT.putString(Params.PARAM_FIRST_NAME, firstname);
        prefEDIT.putString(Params.PARAM_TOKEN, token);
        prefEDIT.putString(Params.PARAM_DATE_EXPIRED, dateexpired);
        prefEDIT.commit();
    }

}
