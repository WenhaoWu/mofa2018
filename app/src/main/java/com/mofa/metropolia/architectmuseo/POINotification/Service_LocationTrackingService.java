package com.mofa.metropolia.architectmuseo.POINotification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.mofa.metropolia.architectmuseo.Constains_BackendAPI_Url;
import com.mofa.metropolia.architectmuseo.Object_POI;
import com.mofa.metropolia.architectmuseo.POIDetail.Activity_POIActivity;
import com.mofa.metropolia.architectmuseo.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.proximi.proximiiolibrary.Proximiio;
import io.proximi.proximiiolibrary.ProximiioFactory;
import io.proximi.proximiiolibrary.ProximiioListener;

public class Service_LocationTrackingService extends IntentService {
    private Proximiio proximiio;
    private ProximiioListener listener;

    private static final String TAG = "MyService";
    public static final String RESPONSE_DISTANCE = "ResponseDistance";
    public static final String RESPONSE_ARRAYLIST = "ResponseArraylist";

    //5s
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    //2.5s
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected String locationStr;
    //protected Network_POIListGetter POIList;


    public Service_LocationTrackingService() {
        super(Service_LocationTrackingService.class.getName());
    }

    //call this method when the service start. The service starts every 10s
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "Service is Running");
        proximiio = ProximiioFactory.getProximiio(this, null);
        listener = new ProximiioListener() {
            @Override
            public void position(double lat, double lon, double accuracy) {
                locationStr= lat+"&lng="+lon;
                getPoiList();
            }
        };
        proximiio.addListener(listener);
    }

    @Override
    public void onDestroy() {
        proximiio.removeListener(listener);
    }

    private void getPoiList() {
        if (locationStr == null){
            Toast.makeText(getBaseContext(),"No Location Service...",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            String url = Constains_BackendAPI_Url.URL_POIList_Distant+locationStr;
            RequestQueue queue = Volley.newRequestQueue(this);
            final JsonArrayRequest ListReq = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            List<Object_POI> resultList = new ArrayList<>();
                            String name = null;
                            int dis=0, time=0, id=0;
                            for (int i=0; i<response.length(); i++){
                                try {
                                    dis = response.getJSONObject(i).getInt("distance");
                                    name = response.getJSONObject(i).getString("poi_name");
                                    time = response.getJSONObject(i).getInt("time");
                                    id = response.getJSONObject(i).getInt("id");
                                } catch (Exception e) {
                                    Log.e("ResponseDisError", e.toString());
                                }
                                resultList.add(new Object_POI(0,0, name,id,null,null,dis,0,0,null,time,null,null,null));
                            }
                            addDisList(resultList);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG+" volleyError", error.toString());
                        }
                    });
            queue.add(ListReq);
        }
    }

    /*
    //Requests location updates from the FusedLocationApi.
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    //Removes location updates from the FusedLocationApi.
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
    */

    public void addDisList(List<Object_POI> poiList){

        for (int i=0; i<poiList.size(); i++){
            Log.e("TestDis", poiList.get(i).getDisTo()+"");
            if (poiList.get(i).getDisTo()<100){ // means the distance from this device to this poi less than 100
                pushNotification(poiList.get(i).getID(), poiList.get(i).getName());
            }
        }

    }

    /*
    private void sendBackByBroadcast(double result) {
        Intent intent = new Intent();
        intent.setAction(CamActivity.Receiver_DistanceResponseReceiver.PROCESS_RESPONSE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(RESPONSE_DISTANCE, result + "");
        sendBroadcast(intent);
    }
    */
    private void pushNotification(int POI_ID, String POI_Name) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(POI_Name)
                .setContentText("Find and Scan");
        mBuilder.setAutoCancel(true);

        //Set up the activity that it jumps to
        Intent resultIntent = new Intent(this, Activity_POIActivity.class);
        resultIntent.putExtra(Activity_POIActivity.ARG_ID, POI_ID);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Activity_POIActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        //Activate the notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(POI_ID, mBuilder.build());

    }

    /*
    private interface VolleyCallback{
        void onSuccess(double result);
    }

    public double get_Distance(final VolleyCallback callback, double curLat, double curLong, double disLat, double disLong){
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+curLat+"%2C"+curLong+
                "&destination="+disLat+"%2C"+disLong+"&sensor=false&mode=%22DRIVING%22";
        Log.e("URL", url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG + " response", response.toString());
                        try {
                            JSONArray routeArray = response.getJSONArray("routes");
                            JSONObject routes = routeArray.getJSONObject(0);

                            JSONArray newTempARr = routes.getJSONArray("legs");
                            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                            double result = distOb.getDouble("value");
                            //Log.e(TAG+" response", distOb.toString());
                            callback.onSuccess(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG+" error", error.toString());
                    }
                });

        queue.add(jsonObjReq);
        return 0;
    }
    */
}
