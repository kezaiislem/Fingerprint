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
import android.widget.AdapterView;
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
import net.spintechs.qimmos.fingerprint.admin.adapters.UserAdapter;
import net.spintechs.qimmos.fingerprint.admin.model.User;
import net.spintechs.qimmos.fingerprint.admin.services.UserService;
import net.spintechs.qimmos.fingerprint.admin.tools.Params;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ISLEM-PC on 4/30/2018.
 */

public class EmployeList extends Fragment {

    ListView listView;
    UserAdapter userAdapter;
    UserService userService;
    RequestQueue requestQueue;
    private Main2Activity mContext;
    ProgressDialog progressDialog;
    Fragmentsend lisner;

    ArrayList userList;
    boolean wait;
    String message = null,token,date;
    User user;
    Button refresh;
    String url;

    public interface Fragmentsend{
        void onInputSent(User user);
    }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                user = (User) userList.get(position);
                lisner.onInputSent(user);
                //new registerEmployeTask().execute();

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getDataTask().execute();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Fragmentsend){
            lisner = (Fragmentsend) context;
        }else {
            throw new RuntimeException(context.toString()+" must implement Fragmentsend");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        lisner=null;
    }

    private void init(){

        mContext = (Main2Activity) getActivity();
        listView = getView().findViewById(R.id.emp_list);
        refresh = getView().findViewById(R.id.refresh);
        token = mContext.mToken;
        userService = new UserService();
        userList = (ArrayList) userService.getUsers(mContext);
        date = userService.getLastCreatedUser(mContext);
        requestQueue = Volley.newRequestQueue(mContext);

    }

    class getDataTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            wait = true;
            date = userService.getLastCreatedUser(mContext);
            if(date == null){
                url = Params.DEFAULT_URL + Params.ORGANISATION_SPINTECHS + "/" + Params.ACTION_USER + "/null";
            } else {
                url = Params.DEFAULT_URL + Params.ORGANISATION_SPINTECHS + "/" + Params.ACTION_USER + "/" + date.replace(" ", "%20");
            }
            //importer le contenu des EditTexts
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //Si il y a aucun erreur

                        JSONArray jsonArray = response.getJSONArray("users");

                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            user = new User();
                            user.setId(obj.getString(Params.PARAM_ID));
                            user.setFirstName(obj.getString(Params.PARAM_FIRST_NAME));
                            user.setLastName(obj.getString(Params.PARAM_LASTE_NAME));
                            user.setDepartement(obj.getString(Params.PARAM_DEPARTEMENT));
                            user.setCreatedOn(obj.getString(Params.PARAM_CREATED_ON));
                            user.setEmail(obj.getString(Params.PARAM_EMAIL));
                            if(obj.has(Params.PARAM_RECRUTMENT_DATE)) {
                                user.setRecrutmentDate(obj.getString(Params.PARAM_RECRUTMENT_DATE));
                            } else{
                                user.setRecrutmentDate("Unknown");
                            }
                            userList.add(user);
                            userService.insertUser(user, mContext);
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

            if(userList.size() == 0) {
                refresh.setVisibility(View.VISIBLE);
            } else{
                refresh.setVisibility(View.GONE);
                userAdapter = new UserAdapter(mContext, R.layout.user_row, userList);
                listView.setAdapter(userAdapter);
            }
            message = null;

        }

    }

}
