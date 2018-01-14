package com.mofa.metropolia.architectmuseo.POIListView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mofa.metropolia.architectmuseo.LandingPage.Activity_MainActivity;
import com.mofa.metropolia.architectmuseo.POINotification.Receiver_AlarmReceiver;
import com.mofa.metropolia.architectmuseo.R;

import io.proximi.proximiiolibrary.Proximiio;
import io.proximi.proximiiolibrary.ProximiioFactory;
import io.proximi.proximiiolibrary.ProximiioListener;

public class Activity_POIMainListView extends AppCompatActivity{
    private Proximiio proximiio;
    private ProximiioListener listener;

    public static final String TAG_CATE = "Tag_Categories";

    private static final String TAG = "POIMainActivity";

    protected String locationStr;

    public static double mlat, mlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poimain_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.poi_list_toolbar);
        toolbar.setTitle("MFA");
        setSupportActionBar(toolbar);

        Adapter_MyViewPagerAdapter viewPagerAdapter =
                new Adapter_MyViewPagerAdapter(getSupportFragmentManager());

        if (getIntent().getStringExtra(TAG_CATE) != null) {
            viewPagerAdapter.setCateStr(getIntent().getStringExtra(TAG_CATE));
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount() - 1);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.fixed_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        if (proximiio == null){
            proximiio = ProximiioFactory.getProximiio(this, this);
        }
        listener = new ProximiioListener() {
            @Override
            public void position(double lat, double lon, double accuracy) {

                if (mlat == 0 || mlong == 0) {
                    locationStr = lat + "&lng=" + lon;
                    mlat = lat;
                    mlong = lon;
                }
            }
        };
        proximiio.addListener(listener);
        Activity_MainActivity.foreground++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mlat = 0;
        mlong = 0;
        proximiio.removeListener(listener);
        Activity_MainActivity.foreground--;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__poi, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.putExtra(Activity_SearchResultActivity.Tag_SearchQuery, query);
                intent.putExtra(Activity_SearchResultActivity.TAG_LOCATION_LAT, mlat);
                intent.putExtra(Activity_SearchResultActivity.TAG_LOCATION_LNG, mlong);
                intent.setClass(getBaseContext(), Activity_SearchResultActivity.class);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

        searchView.setOnQueryTextListener(listener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.location_start) {
            scheduleAlarm();
            return true;
        } else if (id == R.id.location_stop) {
            cancelAlarm();
            return true;
        } else if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), Receiver_AlarmReceiver.class);

        //create a pendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent
                .getBroadcast(this, Receiver_AlarmReceiver.REQUEST_CODE,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //alarm is set from now
        long firstMillis = System.currentTimeMillis();
        //setup periodic alarm every 10 seconds
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 5 * 1000, pIntent);

    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), Receiver_AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent
                .getBroadcast(this, Receiver_AlarmReceiver.REQUEST_CODE,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
}
