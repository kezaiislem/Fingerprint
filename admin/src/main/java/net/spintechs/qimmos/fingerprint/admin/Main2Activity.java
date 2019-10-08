package net.spintechs.qimmos.fingerprint.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rscja.deviceapi.Fingerprint;

import net.spintechs.qimmos.fingerprint.admin.fragments.AddEmploye;
import net.spintechs.qimmos.fingerprint.admin.fragments.EmployeList;
import net.spintechs.qimmos.fingerprint.admin.fragments.PointageList;
import net.spintechs.qimmos.fingerprint.admin.model.User;
import net.spintechs.qimmos.fingerprint.admin.tools.Params;


public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , EmployeList.Fragmentsend {

    public Fingerprint mFingerprint;
    public String mFullName,mEmail,mToken,mExipiredDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if(checkUser()) {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View v = navigationView.getHeaderView(0);
            TextView textView = v.findViewById(R.id.TextEmailNav);
            TextView textView2 = v.findViewById(R.id.Welcom);
            textView2.setText("Welcom "+mFullName);
            textView.setText(""+mEmail);

            init();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_main, new EmployeList());

            ft.commit();

        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_group) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_main,new EmployeList());
            ft.commit();
        } else if (id == R.id.nav_polist) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_main, new PointageList());
            ft.commit();
        }else if (id == R.id.nav_exit) {
            SharedPrefs.saveShared(Main2Activity.this,"Session","true");
            Intent imLoged = new Intent(Main2Activity.this,LoginActivity.class);
            startActivity(imLoged);
            Main2Activity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void init(){

        try {

            mFingerprint = Fingerprint.getInstance();

        } catch (Exception ex) {
            Toast.makeText(Main2Activity.this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        mFingerprint.init();

    }

    @Override
    public void onInputSent(User user) {
        AddEmploye addEmploye= new AddEmploye();
        Bundle bundle = new Bundle();
        bundle.putParcelable("message", user);
        addEmploye.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main,addEmploye,null);
        ft.addToBackStack(null);
        ft.commit();
    }

    public Boolean checkUser(){

        Boolean check = Boolean.valueOf(SharedPrefs.readShared(Main2Activity.this,"Session","true"));
        Intent intent = new Intent(Main2Activity.this,LoginActivity.class);
        intent.putExtra("Session",check);

        if(check){
            startActivity(intent);
            Main2Activity.this.finish();
            return false;
        } else{
            SharedPreferences SP = getApplicationContext().getSharedPreferences("LOGIN", 0);
            mFullName = SP.getString(Params.PARAM_LASTE_NAME, null)+" "+SP.getString(Params.PARAM_FIRST_NAME, null);
            mEmail = SP.getString(Params.PARAM_EMAIL,null);
            mToken = SP.getString(Params.PARAM_TOKEN, null);
            mExipiredDate = SP.getString(Params.PARAM_DATE_EXPIRED, null);
        }
        return true;
    }
}
