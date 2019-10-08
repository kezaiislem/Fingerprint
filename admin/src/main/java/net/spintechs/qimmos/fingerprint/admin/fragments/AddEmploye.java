package net.spintechs.qimmos.fingerprint.admin.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rscja.deviceapi.Fingerprint;

import net.spintechs.qimmos.fingerprint.admin.Main2Activity;
import net.spintechs.qimmos.fingerprint.admin.R;
import net.spintechs.qimmos.fingerprint.admin.model.User;
import net.spintechs.qimmos.fingerprint.admin.tools.Params;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ISLEM-PC on 4/30/2018.
 */


public class AddEmploye extends Fragment {

    TextView fullName, departement, email, recrutmentDate;
    Button save;

    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    Main2Activity mContext;
    JsonObjectRequest jsonObjectRequest;

    static final String url = Params.DEFAULT_URL + Params.ORGANISATION_SPINTECHS + "/" + Params.PARAM_FINGERPRINT + "/insert";
    User user;
    String fingerprintChar, message;
    boolean wait;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cardid2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        user = bundle.getParcelable("message");

        init();

        fullName.setText(user.getFirstName() + " " + user.getLastName());
        departement.setText(user.getDepartement());
        email.setText(user.getEmail());
        recrutmentDate.setText(user.getRecrutmentDate());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddFingerprint().execute();
            }
        });

    }

    private void init() {
        save = getView().findViewById(R.id.post);
        mContext = (Main2Activity) getActivity();
        fullName = getView().findViewById(R.id.fullname);
        departement = getView().findViewById(R.id.departemnt);
        email = getView().findViewById(R.id.email);
        recrutmentDate = getView().findViewById((R.id.recrutmentdate));
        requestQueue = Volley.newRequestQueue(mContext);
    }

    class AddFingerprint extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            boolean exeSucc = false;
            message = null;

            if (!mContext.mFingerprint.getImage()) {
                return null;
            }

            if (mContext.mFingerprint.genChar(Fingerprint.BufferEnum.B1)) {
                exeSucc = true;
            }

            if (!mContext.mFingerprint.getImage()) {
                return null;
            }

            if (mContext.mFingerprint.genChar(Fingerprint.BufferEnum.B2)) {
                exeSucc = true;
            }

            if (mContext.mFingerprint.regModel()) {
                exeSucc = true;
            }

            if (exeSucc) {

                fingerprintChar = mContext.mFingerprint.upChar(Fingerprint.BufferEnum.B1);

                if (fingerprintChar != null) {

                    wait = true;
                    JSONObject params = new JSONObject();

                    try {

                        params.put(Params.PARAM_FINGERPRINT_CHAR, fingerprintChar);
                        params.put(Params.PARAM_USER_ID, user.getId());
                        params.put(Params.PARAM_TOKEN, mContext.mToken);

                        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                message = "Fingerprint Saved Succcessfully";
                                wait = false;
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null) {
                                    if (networkResponse.statusCode == 401) {
                                        Toast.makeText(mContext, "ERROR 401", Toast.LENGTH_LONG).show();
                                    } else if (networkResponse.statusCode == 404) {
                                        Toast.makeText(mContext, "ERROR 404", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(mContext, "ERROR 500", Toast.LENGTH_LONG).show();
                                    }
                                }
                                wait = false;
                            }
                        });

                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        wait = false;
                        e.printStackTrace();
                    }
                }
            }
            while (wait) {
            }
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

            if (message != null) {
                Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
            }

        }
    }

}
