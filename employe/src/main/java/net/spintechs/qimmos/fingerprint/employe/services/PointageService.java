package net.spintechs.qimmos.fingerprint.employe.services;

import android.content.Context;

import net.spintechs.qimmos.fingerprint.employe.dao.PointageDao;
import net.spintechs.qimmos.fingerprint.employe.model.Pointage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.spintechs.qimmos.fingerprint.employe.tools.Params.*;

public class PointageService {

    private PointageDao getDao(){
        return new PointageDao();
    }

    public boolean insertPointage(Pointage pointage, Context context){

        return getDao().insertPointage(pointage, context);

    }

    public JSONArray getPointages(Context context){

        List<Object> mapPointages = new ArrayList<>();
        PointageDao pointageDao = getDao();
        List<Pointage> pointages = pointageDao.getPointageList(context);

        for (Pointage pointage : pointages) {

            Map<String, Object> mapPointage = new HashMap<>();
            mapPointage.put(PARAM_DATE, pointage.getDate());
            mapPointage.put(PARAM_TIME, pointage.getTime());
            mapPointage.put(PARAM_TYPE, pointage.getType());
            mapPointage.put(PARAM_LOCATION_LONGITUDE, pointage.getLongitude());
            mapPointage.put(PARAM_LOCATION_LATITUDE, pointage.getLatitude());
            mapPointage.put(PARAM_USER_ID, pointage.getUser().getId());
            mapPointage.put(PARAM_ID,pointage.getId());

            mapPointages.add(new JSONObject(mapPointage));
        }
        JSONArray jsonObject = new JSONArray(mapPointages);
        return jsonObject;

    }

    public boolean dropPointage(int id, Context context){
        return getDao().dropPointage( id, context);
    }

    public String getUserId(int id, Context context){
        PointageDao pointageDao = getDao();
        return pointageDao.getUserId( id, context);
    }

    public Boolean isEmpty (Context context){
        return getDao().isEmpty(context);
    }

    public void dropPointages(Context context){
        getDao().dropPointages(context);
    }

}
