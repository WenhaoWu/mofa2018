package com.mofa.metropolia.architectmuseo.POIListView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mofa.metropolia.architectmuseo.Constains_BackendAPI_Url;
import com.mofa.metropolia.architectmuseo.Object_POI;
import com.mofa.metropolia.architectmuseo.POIDetail.Activity_POIActivity;
import com.mofa.metropolia.architectmuseo.POIRecognition.CamActivity;
import com.mofa.metropolia.architectmuseo.R;
import com.mofa.metropolia.architectmuseo.Utils.ImageLoader;
import com.mofa.metropolia.architectmuseo.Utils.PermissionHelper;

import org.json.JSONArray;

import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.mofa.metropolia.architectmuseo.Utils.DialogHelper.createProgressDialog;

/**
 * Created by wenhao on 25.11.2017.
 */

public class Fragment_Location extends Fragment {

    private static final String TAG = "LocationFragment";
    private static final String ARG_PARM3 = "CategoryString";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int LOCATION_REQUEST_CODE = 101;

    private Adapter_ListAdapter adapter;
    private RecyclerView listView;
    private ProgressDialog progressDialog;
    private RelativeLayout emptyContainer;

    public static Fragment_Location newInstance(String cateStr) {
        Fragment_Location fragment = new Fragment_Location();

        Bundle args = new Bundle();
        args.putString(ARG_PARM3, cateStr);

        fragment.setArguments(args);
        return fragment;
    }

    //Empty constructor is needed to implement newInstance
    public Fragment_Location() {
    }

    private String buildUrlWithPermission(){
        final LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(LOCATION_SERVICE);

        assert locationManager != null;
        @SuppressLint("MissingPermission")
        final Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        String  locationStr = loc.getLatitude() + "&lng=" + loc.getLongitude();

        String categoryStr = "&cata="+getArguments().getString(ARG_PARM3);
        String url = "";
        if (!categoryStr.equals("&cata=All")){
            url = Constains_BackendAPI_Url.URL_POIList_Distant +locationStr+categoryStr;
        }
        else {
            url = Constains_BackendAPI_Url.URL_POIList_Distant +locationStr;
        }

        return url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        CoordinatorLayout myView = (CoordinatorLayout) inflater
                .inflate(R.layout.fragment_poi_list_tab, container, false);

        listView = (RecyclerView) myView.findViewById(R.id.POIlistview);
        progressDialog = createProgressDialog(getActivity());
        emptyContainer = (RelativeLayout)myView.findViewById(R.id.empty_container);

        listView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(mLayoutManager);

        FloatingActionButton fab_cam = (FloatingActionButton)myView.findViewById(R.id.poi_list_fab_cam);
        fab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionHelper.isCameratPermissionAllow(getContext())){
                    Toast.makeText(getContext(), "Need Permission", Toast.LENGTH_SHORT).show();

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CAMERA)) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST_CODE);

                    } else {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST_CODE);
                    }


                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getContext(), CamActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });

        emptyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean allow = PermissionHelper.isLocationPermissionAllow(getContext());
                if (allow){
                    requestData();
                    return;
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);

                } else {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);
                }
            }
        });

        this.requestData();

        return myView;
    }

    private void requestData(){
        boolean allow = PermissionHelper.isLocationPermissionAllow(getContext());

        if (allow){
            listView.setVisibility(View.VISIBLE);
            emptyContainer.setVisibility(View.GONE);
            String url = buildUrlWithPermission();
            buildList(url);
        }
        else{
            Toast.makeText(getContext(), "No Permission", Toast.LENGTH_SHORT).show();
            listView.setVisibility(View.GONE);
            emptyContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: code "+requestCode );
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent();
                    intent.setClass(getContext(), CamActivity.class);
                    intent.putExtra("mode", 1);
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    this.requestData();

                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

            }
        }
    }


    private void buildList(String url){
        final ArrayList<Object_POI> result = new ArrayList<>();

        RequestQueue queue = Network_SerialQueue.getSerialRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            String name, imgBase64;
                            int id, disTo, rate_count=0;
                            Double rate_score=0.0;
                            try {
                                disTo = response.getJSONObject(i).getInt("distance");
                                name = response.getJSONObject(i).getString("poi_name");
                                imgBase64 = response.getJSONObject(i).getString("image");
                                id = response.getJSONObject(i).getInt("id");
                                Object_POI temp = new Object_POI(0, 0, name, id,
                                        imgBase64,null,disTo,rate_score,
                                        rate_count, null,0, null,null,null);
                                result.add(temp);

                            } catch (Exception e) {
                                Log.e("ResponseDisError", e.toString());
                            }
                        }
                        adapter = new Adapter_ListAdapter(getContext(), result, 0);
                        listView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
        progressDialog.show();
    }
}
