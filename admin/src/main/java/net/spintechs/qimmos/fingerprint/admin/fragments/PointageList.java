package net.spintechs.qimmos.fingerprint.admin.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.spintechs.qimmos.fingerprint.admin.Main2Activity;
import net.spintechs.qimmos.fingerprint.admin.R;
import net.spintechs.qimmos.fingerprint.admin.adapters.PointageAdapter;
import net.spintechs.qimmos.fingerprint.admin.model.Pointage;
import net.spintechs.qimmos.fingerprint.admin.model.User;
import net.spintechs.qimmos.fingerprint.admin.tools.Params;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class PointageList extends Fragment {

    Button refresh;
    ListView listView;

    List list_ptg;
    Context mContext;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    PointageAdapter pointageAdapter;
    Pointage pointage;
    User user;

    String message;
    final String url = Params.DEFAULT_URL+ Params.ORGANISATION_SPINTECHS+"/"+ Params.ACTION_POINTAGE+"/"+ Params.PARAM_POINTAGE_DAYS;
    Boolean wait;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        new getDataTask().execute();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getDataTask().execute();
            }
        });

    }

    private void init(){

        mContext = (Main2Activity) getActivity();
        listView = (ListView) getView().findViewById(R.id.emp_list);
        refresh = getView().findViewById(R.id.refresh);

    }

    class getDataTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            wait = true;
            requestQueue = Volley.newRequestQueue(mContext);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //Si il y a aucun erreur
                        list_ptg = new ArrayList();
                        JSONArray jsonArray = response.getJSONArray("pointages");

                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            user = new User();
                            user.setFirstName(obj.getString(Params.PARAM_FIRST_NAME));
                            user.setLastName(obj.getString(Params.PARAM_LASTE_NAME));
                            pointage = new Pointage();
                            pointage.setUser(user);
                            pointage.setDate(obj.getString(Params.PARAM_DATE));
                            pointage.setTime(obj.getString(Params.PARAM_TIME));
                            pointage.setType(obj.getString(Params.PARAM_TYPE));
                            list_ptg.add(pointage);

                        }

                        wait = false;
                        message = "";

                    } catch (JSONException e) {
                        e.printStackTrace();
                        wait = false;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        if(networkResponse.statusCode == 401){
                            Toast.makeText(mContext, "ERROR 401", Toast.LENGTH_LONG).show();
                        } else if (networkResponse.statusCode == 404){
                            Toast.makeText(mContext, "ERROR 404", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "ERROR 500", Toast.LENGTH_LONG).show();
                        }
                    }
                    wait = false;
                }
            });

            requestQueue.add(jsonObjectRequest);

            while(wait);

            return message;

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please Wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.cancel();

            if(message == null) {
                refresh.setVisibility(View.VISIBLE);
            } else{
                refresh.setVisibility(View.GONE);
                pointageAdapter = new PointageAdapter(mContext, R.layout.pointage_row, list_ptg);
                listView.setAdapter(pointageAdapter);
            }
            message = null;

        }

    }
}
