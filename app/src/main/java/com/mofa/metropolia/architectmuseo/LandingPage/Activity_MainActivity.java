package com.mofa.metropolia.architectmuseo.LandingPage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.mofa.metropolia.architectmuseo.Constains_BackendAPI_Url;
import com.mofa.metropolia.architectmuseo.POIDetail.Activity_POIActivity;
import com.mofa.metropolia.architectmuseo.R;
import com.mofa.metropolia.architectmuseo.Utils.PermissionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import io.proximi.proximiiolibrary.Proximiio;
import io.proximi.proximiiolibrary.ProximiioFactory;
import io.proximi.proximiiolibrary.ProximiioGeofence;
import io.proximi.proximiiolibrary.ProximiioListener;

import static com.mofa.metropolia.architectmuseo.Utils.DialogHelper.createProgressDialog;

public class Activity_MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private static HashSet<ProximiioGeofence> entered;
    private static ProximiioListener listener;

    public static int foreground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MultiDex.install(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.Landing_RV);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /**/
        //getting the categories data from back end and attach the adapter to recycle view
        getCateData(new cateCallBack() {
            @Override
            public void onSuccess(ArrayList<Object_RVItem> Items) {
                mAdapter = new Adapter_RVAdapter(Items, getBaseContext());
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        if (entered == null) {
            entered = new HashSet<>();
        }

        boolean allow = PermissionHelper.isLocationPermissionAllow(getApplicationContext());
        if (listener == null) {
            listener = new ProximiioListener() {
                @Override
                public void output(JSONObject json) {
                    try {
                        if (json.has("navigation") && json.has("geofence") && json.has("title") && json.getBoolean("navigation")) {
                            String geofenceName = json.getString("geofence");
                            for (ProximiioGeofence geofence : entered) {
                                if (geofence.getName().equals(geofenceName)) {
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + geofence.getLat() + "," + geofence.getLon() + "&mode=w");
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");

                                    PendingIntent contentIntent = PendingIntent.getActivity(Activity_MainActivity.this, 0, mapIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                                    NotificationManager notificationManager = (NotificationManager) Activity_MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                                    Notification notification = new Notification.Builder(Activity_MainActivity.this)
                                            .setContentIntent(contentIntent)
                                            .setSmallIcon(R.drawable.ic_navigation_black_36dp)
                                            .setContentTitle(json.getString("title"))
                                            .setPriority(Notification.PRIORITY_HIGH)
                                            .build();

                                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                    notificationManager.notify(1, notification);
                                    break;
                                }
                            }
                        } else if (json.has("navigation") && json.has("id") && json.has("title") && !json.getBoolean("navigation")) {
                            Intent intent = new Intent();
                            try {
                                intent.putExtra(Activity_POIActivity.ARG_ID, json.getInt("id"));
                            } catch (JSONException e) {
                                intent.putExtra(Activity_POIActivity.ARG_ID, Integer.parseInt(json.getString("id")));
                            }
                            intent.setClass(getBaseContext(), Activity_POIActivity.class);

                            if (foreground > 0) {
                                startActivity(intent);
                            } else {
                                PendingIntent contentIntent = PendingIntent.getActivity(Activity_MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                NotificationManager notificationManager = (NotificationManager) Activity_MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                                Notification notification = new Notification.Builder(Activity_MainActivity.this)
                                        .setContentIntent(contentIntent)
                                        .setSmallIcon(R.drawable.ic_web_black_24dp)
                                        .setContentTitle(json.getString("title"))
                                        .setPriority(Notification.PRIORITY_HIGH)
                                        .build();

                                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                notificationManager.notify(2, notification);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("Activity_MainActivity", "Has returned true but value was missing!");
                        e.printStackTrace();
                    }
                }

                @Override
                public void geofenceEnter(ProximiioGeofence geofence) {
                    entered.add(geofence);
                }

                @Override
                public void geofenceExit(ProximiioGeofence geofence) {
                    entered.remove(geofence);
                }
            };
        }
        if (allow) {
            Proximiio proximiio = ProximiioFactory.getProximiio(this, this);
            proximiio.setAuth("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImlzcyI6ImM0ZmMxMWFhOTEzMzQ5NTc5NjE1NWRmMDAwMGQ3MTJiIiwidHlwZSI6ImFwcGxpY2F0aW9uIiwiYXBwbGljYXRpb25faWQiOiI4ZTBmZmIwZi0xODVkLTQ4YjgtYTBlYi04Njg1MzgxMzRmYTYifQ.pyNLL6x_Br5cg7lfT9ih8E_Z6hjYQQ4MtaO3IWlWY44");
            proximiio.clearListeners();
            proximiio.addListener(listener);
        }
    }


    private interface cateCallBack {
        void onSuccess(ArrayList<Object_RVItem> Items);
    }

    private void getCateData(final cateCallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final ProgressDialog PD = createProgressDialog(this);

        JsonArrayRequest cataReq = new JsonArrayRequest(Request.Method.GET, Constains_BackendAPI_Url.URL_GetCatagories, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("CateResponse", response.length() + "");
                        ArrayList<Object_RVItem> result = new ArrayList<>();
                        String cataTemp = null, imgTemp = null;
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                cataTemp = response.getJSONObject(i).getString("cata_name");
                                imgTemp = response.getJSONObject(i).getString("cata_url");
                                imgTemp = imgTemp.replace("\\", "");
                                Log.e("response", imgTemp);
                            } catch (JSONException e) {
                                Log.e("CataResponseErr", e.toString());
                            }
                            result.add(new Object_RVItem(cataTemp, imgTemp));
                        }
                        callBack.onSuccess(result);
                        PD.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CataReqErr", error.toString());
                        Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        PD.dismiss();
                    }
                });

        queue.add(cataReq);
        PD.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getCateData(new cateCallBack() {
                @Override
                public void onSuccess(ArrayList<Object_RVItem> Items) {
                    mAdapter = new Adapter_RVAdapter(Items, getBaseContext());
                    mRecyclerView.setAdapter(mAdapter);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        foreground++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        foreground--;
    }
}

