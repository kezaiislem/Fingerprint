package net.spintechs.qimmos.fingerprint.employe;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import net.spintechs.qimmos.fingerprint.employe.model.Pointage;
import net.spintechs.qimmos.fingerprint.employe.model.User;
import net.spintechs.qimmos.fingerprint.employe.services.FingerprintService;
import net.spintechs.qimmos.fingerprint.employe.services.PointageService;
import net.spintechs.qimmos.fingerprint.employe.services.UserService;
import net.spintechs.qimmos.fingerprint.employe.tools.DateUtility;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rscja.deviceapi.Fingerprint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static net.spintechs.qimmos.fingerprint.employe.tools.Params.*;

public class HomeInterface extends AppCompatActivity {


    //TextView locationStatus, pageId;
    //Button login;
    ImageButton reload;
    RadioButton radioAuto, radioEntry, radioExit;
    TextView fullname, departement, days, day1, year;

    PointageService pointageService;
    UserService userService;
    Pointage pointage;
    User user;
    public Fingerprint mFingerprint;
    LocationListener locationListener;
    LocationManager locationManager;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    Context mContext;
    NotificationManager notificationManager;
    Notification notification;
    Date date, date1;
    Dialog dialog;
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    Double location_long, location_lat;
    String type, message, url;
    int res, score;
    boolean result, wait;
    Calendar calendar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_interface);
        init();
        getLocation();
        radioEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "entry";
            }
        });

        radioExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "exit";
            }
        });

        radioAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "auto";
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadFingerprints().execute();
            }
        });

    }

    private void init() {

        //pageId = findViewById(R.id.pageid);
        mContext = this;
        //locationStatus = findViewById(R.id.locini);
        location_long = null;
        location_lat = null;
        pointageService = new PointageService();
        userService = new UserService();
        requestQueue = Volley.newRequestQueue(mContext);
        radioAuto = findViewById(R.id.auto);
        radioEntry = findViewById(R.id.entry);
        radioExit = findViewById(R.id.exit);
        reload = findViewById(R.id.reload);

        calendar = Calendar.getInstance();

        type = "auto";

        try {

            mFingerprint = Fingerprint.getInstance();

        } catch (Exception ex) {

        }

        mFingerprint.init();

        //new LoadFingerprints().execute();
        new CheckFingerprintsValidity().execute();

        notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.defaults = 0;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139) {
            date = new Date();

            if (!pointageService.isEmpty(mContext)) {

                JSONArray jsonArray = pointageService.getPointages(mContext);
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {

                        JSONObject params = jsonArray.getJSONObject(i);
                        url = DEFAULT_URL + ORGANISATION_SPINTECHS + "/" + ACTION_POINTAGE + "/insert";
                        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    pointageService.dropPointage(Integer.parseInt(response.getString("id")), mContext);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null) {
                                    if (networkResponse.statusCode == 400) {
                                        Toast.makeText(getApplicationContext(), "ERROR 400", Toast.LENGTH_LONG).show();
                                    } else if (networkResponse.statusCode == 404) {
                                        Toast.makeText(getApplicationContext(), "ERROR 404", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "ERROR 500", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });

                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (location_lat == null || location_long == null) {
                Toast.makeText(mContext, "failed to get location, try again please !", Toast.LENGTH_SHORT).show();
            } else {
                new PointageTask().execute();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public class PointageTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            boolean exeSucc = false;

            if (!mFingerprint.getImage()) {
                return "err";
            }

            if (mFingerprint.genChar(Fingerprint.BufferEnum.B1)) {
                int[] result = null;
                int exeCount = 0;

                do {
                    exeCount++;
                    result = mFingerprint.search(Fingerprint.BufferEnum.B1, 0, 1000);

                } while (result == null && exeCount < 3);

                if (result != null) {
                    res = result[0];
                    score = result[1];

                    return "ok";
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notification.ledARGB = Color.BLUE;
            notificationManager.notify(0, notification);
            //Initialisation Du ProgressDialogue
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Authentification ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            notificationManager.cancel(0);

            wait = true;


            if (s == null) {
                Toast.makeText(mContext, "fingerprint doesn't exist", Toast.LENGTH_SHORT).show();
                notification.ledARGB = Color.RED;
                new LightControl().start();
                final MediaPlayer errorbip = MediaPlayer.create(mContext, R.raw.wrong);
                errorbip.start();
                wait = false;
                progressDialog.cancel();

            } else if (s == "err") {

                Toast.makeText(mContext, "failed to get fingerprint, try again please !", Toast.LENGTH_SHORT).show();
                notification.ledARGB = Color.RED;
                new LightControl().start();
                final MediaPlayer errorbip = MediaPlayer.create(mContext, R.raw.wrong);
                errorbip.start();
                wait = false;
                progressDialog.cancel();

            } else {
                //Afficher le PID et le SCORE
                //pageId.setText(" = " + res + " Score = " + score);
                //Changement Du message
                progressDialog.setMessage("Connection ...");
                url = DEFAULT_URL + ORGANISATION_SPINTECHS + "/" + ACTION_POINTAGE + "/insert";

                try {
                    JSONObject params = new JSONObject();
                    params.put(PARAM_DATE, dateFormat.format(date));
                    params.put(PARAM_TIME, timeFormat.format(date));
                    params.put(PARAM_TYPE, type);
                    params.put(PARAM_LOCATION_LONGITUDE, location_long);
                    params.put(PARAM_LOCATION_LATITUDE, location_lat);
                    final String id = pointageService.getUserId(res, mContext);

                    if (id != null) {
                        params.put(PARAM_USER_ID, id);


                        //importer le contenu des EditTexts
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                notification.ledARGB = Color.GREEN;
                                new LightControl().start();

                                user = userService.getUser(id, mContext);
                                if (user != null) {
                                    successDialogue(user);
                                    new CancelDialogue().start();
                                }

                                wait = false;
                                progressDialog.cancel();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Errur De connection Avec le Serveur
                                //Toast.makeText(mContext,"No Connection",Toast.LENGTH_LONG).show();
                                pointage = new Pointage();
                                pointage.setDate(dateFormat.format(date));
                                pointage.setTime(timeFormat.format(date));
                                pointage.setType(type);
                                pointage.setLongitude(location_long);
                                pointage.setLatitude(location_lat);
                                user = new User();
                                user.setId(pointageService.getUserId(res, mContext));
                                pointage.setUser(user);
                                result = pointageService.insertPointage(pointage, mContext);
                                if (result) {
                                    notification.ledARGB = Color.GREEN;
                                    new LightControl().start();
                                    user = userService.getUser(user.getId(), mContext);
                                    if (user != null) {
                                        successDialogue(user);
                                        new CancelDialogue().start();
                                    }
                                } else {
                                    notification.ledARGB = Color.RED;
                                    new LightControl().start();
                                    Toast.makeText(mContext, "Erreur d'ajout a la BDD interne", Toast.LENGTH_LONG).show();
                                    final MediaPlayer errorbip = MediaPlayer.create(mContext, R.raw.wrong);
                                    errorbip.start();
                                }
                                wait = false;
                                progressDialog.cancel();
                            }
                        });
                        // Lencement du requette
                        requestQueue.add(jsonObjectRequest);
                    } else {
                        Toast.makeText(mContext, "User Doesnt Exist", Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.cancel();
                }

            }


        }

    }

    private void successDialogue(User user) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.uidcard);

        fullname = dialog.findViewById(R.id.fullname);
        departement = dialog.findViewById(R.id.departement);
        days = dialog.findViewById(R.id.day);
        day1 = dialog.findViewById(R.id.day1);
        year = dialog.findViewById(R.id.date);

        fullname.setText(user.getLastName() + " " + user.getFirstName());
        departement.setText(user.getDepartement());

        date1 = calendar.getTime();
        days.setText(""+DateUtility.getDayInt(date1));
        day1.setText(""+DateUtility.getDayString(date1));
        year.setText(""+DateUtility.getMonthString(date1)+" "+DateUtility.getYear(date1));

        final MediaPlayer successbip = MediaPlayer.create(mContext, R.raw.success);
        successbip.start();
        dialog.show();
    }

    // Fonction Pour Desterminer la Localisation
    private void getLocation() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // locationStatus.setText("Ready");
                //locationStatus.setTextColor(Color.GREEN);
                location_lat = location.getLatitude();
                location_long = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(mContext, "Please enable GPS", Toast.LENGTH_SHORT).show();
            }
        };

        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        } catch (SecurityException E) {
            Toast.makeText(mContext, "Need Permissions", Toast.LENGTH_SHORT).show();
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

    }

    public class LightControl extends Thread {

        @Override
        public void run() {
            try {
                notificationManager.notify(0, notification);
                sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                notificationManager.cancel(0);
            }
        }
    }

    public class CancelDialogue extends Thread {

        @Override
        public void run() {
            try {
                sleep(3000);
                dialog.cancel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class LoadFingerprints extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading Fingerprints ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... integers) {

            wait = true;
            message = null;
            url = DEFAULT_URL + ORGANISATION_SPINTECHS + "/" + PARAM_FINGERPRINT + "/beta";

            JSONObject params = new JSONObject();
            FingerprintService fingerprintService = new FingerprintService();
            try {
                params.put("request", fingerprintService.getFingerprints(mContext));
                Log.e("fingers", params.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONArray insert, update;
                    FingerprintService fingerprintService = new FingerprintService();
                    try {
                        insert = response.getJSONArray("insert");
                        update = response.getJSONArray("update");
                        fingerprintService.insertFingerprints(insert, mContext, mFingerprint);
                        fingerprintService.updateFingerprints(update, mContext, mFingerprint);
                        message = "success";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

            while (wait) {
            }

            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.cancel();

            if (message != null) {
                Toast.makeText(mContext, "" + message, Toast.LENGTH_LONG).show();
            }

        }

    }

    class CheckFingerprintsValidity extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Checking Fingerprints ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean result = false;
            long n = mFingerprint.validTempleteNum();
            if (n < 1) {
                if (!userService.isEmpty(mContext)) {
                    if (userService.resetAll(mContext)) {
                        result = true;
                    }
                } else {
                    result = true;
                }
            } else {
                if (n != userService.countUsers(mContext)) {
                    if (mFingerprint.empty() && userService.resetAll(mContext)) {
                        result = true;
                    }
                } else {
                    result = true;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progressDialog.cancel();
            if (result == true) {
                new LoadFingerprints().execute();
            } else {
                Toast.makeText(mContext, "Error While Checking Fingerprints Please Reinstall The Application", Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }

}
