package net.spintechs.qimmos.fingerprint.admin;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import net.spintechs.qimmos.fingerprint.admin.tools.Params;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String url;
    TextView title;
    Button connect;
    TextInputLayout textInputEmail;
    TextInputLayout textInputPassword;
    TextInputLayout textInputOrganisation;
    ProgressDialog prog;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        init();

        //Action Clic bouton Connecter
        connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if( validationEmail() && validationPassword() && validationOrganisation() ) {

                    startDialogue(view.getContext() , "Authentification");

                    JSONObject params = new JSONObject();
                    try {
                        url = Params.DEFAULT_URL+ textInputOrganisation.getEditText().getText().toString().replaceAll("\\s","")+"/"+ Params.ACTION_LOGIN+"/"+ Params.PARAM_FINGERPRINT;
                        params.put("email", textInputEmail.getEditText().getText().toString());
                        params.put("password", textInputPassword.getEditText().getText().toString());

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            public void onResponse(JSONObject response) {

                                try {
                                    SharedPrefs.saveShared(LoginActivity.this, "Session", "false");
                                    SharedPrefs.SharedPrefesSAVE(LoginActivity.this, response.getString(Params.PARAM_EMAIL), response.getString(Params.PARAM_LASTE_NAME), response.getString(Params.PARAM_FIRST_NAME), response.getString(Params.PARAM_TOKEN), response.getString(Params.PARAM_DATE_EXPIRED));
                                    Intent imLoged = new Intent(LoginActivity.this, Main2Activity.class);
                                    startActivity(imLoged);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                prog.cancel();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null) {
                                    if(networkResponse.statusCode == 400){
                                        textInputEmail.setError("Wrong email");
                                        textInputPassword.setError("Wrong password");
                                        Toast.makeText(getApplicationContext(), "Wrong Email or Password", Toast.LENGTH_LONG).show();
                                    } else if (networkResponse.statusCode == 404){
                                        Toast.makeText(getApplicationContext(), "ERROR 404", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "ERROR 500", Toast.LENGTH_LONG).show();
                                    }
                                }
                                prog.cancel();
                            }
                        });
                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void init(){

        //Initialisation du font pour
        //Typeface myCostumFont=Typeface.createFromAsset(getAssets(),"fonts/GloriaHallelujah.ttf");

        //Initialisation Switch Bouttons et Les text

        title = findViewById(R.id.title);
        textInputEmail = findViewById(R.id.email);
        textInputPassword = findViewById(R.id.password);
        textInputOrganisation = findViewById(R.id.organisation);
        connect = findViewById(R.id.connect);

        //Application Du font sur le Switch et le Titre
        //title.setTypeface(myCostumFont);

        //Initialisation Du Volley
        requestQueue = Volley.newRequestQueue(this);

    }

    public boolean validationOrganisation(){
        String passwordInput= textInputOrganisation.getEditText().getText().toString().trim();
        if(passwordInput.isEmpty()){
            textInputOrganisation.setError("Field can't be empty");
            return false;
        }else {
            textInputOrganisation.setError(null);
            return true;
        }
    }

    public boolean validationEmail(){
        String emailInput= textInputEmail.getEditText().getText().toString().trim();
        if(emailInput.isEmpty()){
            textInputEmail.setError("Field can't be empty");
            return false;
        }else {
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
                textInputEmail.setError(null);
                return true;
            }else {
                textInputEmail.setError("Wrong Email Format");
                return false;
            }
        }
    }

    public boolean validationPassword(){
        String passwordInput= textInputPassword.getEditText().getText().toString().trim();
        if(passwordInput.isEmpty()){
            textInputPassword.setError("Field can't be empty");
            return false;
        }else {
            textInputPassword.setError(null);
            return true;
        }
    }

    public void startDialogue (Context context, String message){

        prog = new ProgressDialog(context);
        prog.setMessage(message);
        prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prog.setCancelable(false);
        prog.show();

    }
}