package net.spintechs.qimmos.fingerprint.employe.services;

import android.content.Context;

import net.spintechs.qimmos.fingerprint.employe.dao.FingerprintDao;
import net.spintechs.qimmos.fingerprint.employe.model.Fingerprint;
import net.spintechs.qimmos.fingerprint.employe.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.spintechs.qimmos.fingerprint.employe.tools.Params.*;

public class FingerprintService {

    private FingerprintDao getDao(){
        return new FingerprintDao();
    }

    public void insertFingerprints(JSONArray fingerprints, Context context, com.rscja.deviceapi.Fingerprint mFingerprint) throws JSONException {

        int rowId;
        FingerprintDao fingerprintDao = getDao();
        UserService userService = new UserService();
        Fingerprint fingerprint = new Fingerprint();
        User user = new User();
        for(int i=0 ; i<fingerprints.length() ; i++){
            JSONObject jsonObject = fingerprints.getJSONObject(i);
            fingerprint.setFingerprintId(jsonObject.getString(PARAM_ID));
            fingerprint.setUpdateDate(jsonObject.getString(PARAM_UPDATE_DATE));
            fingerprint.setUserId(jsonObject.getString(PARAM_USER_ID));
            rowId = fingerprintDao.insertFingerprint(fingerprint, context);
            if(rowId != -1){
                user.setId(jsonObject.getString(PARAM_USER_ID));
                user.setFirstName(jsonObject.getString(PARAM_FIRST_NAME));
                user.setLastName(jsonObject.getString(PARAM_LASTE_NAME));
                user.setDepartement(jsonObject.getString(PARAM_DEPARTEMENT));
                if(userService.insertUser(user, context)){
                    mFingerprint.downChar(com.rscja.deviceapi.Fingerprint.BufferEnum.B1, jsonObject.getString(PARAM_FINGERPRINT_CHAR));
                    mFingerprint.storChar(com.rscja.deviceapi.Fingerprint.BufferEnum.B1, rowId);
                } else {
                    fingerprintDao.dropFingerprint(rowId, context);
                }
            }
        }

    }

    public void updateFingerprints(JSONArray fingerprints, Context context, com.rscja.deviceapi.Fingerprint mFingerprint) throws JSONException {

        int rowId;
        FingerprintDao fingerprintDao = getDao();
        Fingerprint fingerprint = new Fingerprint();
        for(int i=0 ; i<fingerprints.length() ; i++){
            JSONObject jsonObject = fingerprints.getJSONObject(i);
            fingerprint.setUpdateDate(jsonObject.getString(PARAM_UPDATE_DATE));
            fingerprint.setFingerprintId(jsonObject.getString(PARAM_ID));
            rowId = fingerprintDao.updateFingerprint(fingerprint, context);
            if(rowId != -1){
                mFingerprint.downChar(com.rscja.deviceapi.Fingerprint.BufferEnum.B1, jsonObject.getString(PARAM_FINGERPRINT_CHAR));
                mFingerprint.storChar(com.rscja.deviceapi.Fingerprint.BufferEnum.B1, rowId);
            }
        }

    }

    public JSONArray getFingerprints(Context context){

        List<Object> mapPointages = new ArrayList<>();
        FingerprintDao fingerprintDao = getDao();
        List<Fingerprint> fingerprints = fingerprintDao.getFingerprintList(context);

        for (Fingerprint fingerprint : fingerprints) {

            Map<String, Object> mapPointage = new HashMap<>();
            mapPointage.put(PARAM_ID, fingerprint.getFingerprintId());
            mapPointage.put(PARAM_UPDATE_DATE, fingerprint.getUpdateDate());
            mapPointages.add(new JSONObject(mapPointage));
        }
        JSONArray jsonObject = new JSONArray(mapPointages);
        return jsonObject;

    }

}
