package com.mofa.metropolia.architectmuseo.POIListView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.mofa.metropolia.architectmuseo.Utils.DialogHelper.createProgressDialog;

public class Fragment_TabFragment extends Fragment {

    private static final String ARG_PARM1 = "SortingMethodID";
    private static final String ARG_PARM3 = "CategoryString";
    private static final String TAG = "Fragment_Tab";

    private Adapter_ListAdapter adapter;
    private RecyclerView listView;

    private ArrayList<Object_POI> pois;
    private String url;

    public static Fragment_TabFragment newInstance(int ID, String cateStr) {
        Fragment_TabFragment fragment = new Fragment_TabFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARM1, ID);
        args.putString(ARG_PARM3, cateStr);

        fragment.setArguments(args);
        return fragment;
    }

    //Empty constructor is needed to implement newInstance
    public Fragment_TabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pois = new ArrayList<>();

        final int sortingMethodID = getArguments().getInt(ARG_PARM1);
        final String categoryStr;
        switch (sortingMethodID) {
            case 1:
                //setListViewByMostviewed();
                // '?' here because it is the second parameter
                categoryStr = "?cata="+getArguments().getString(ARG_PARM3);
                if (!categoryStr.equals("?cata=All")){
                    url = Constains_BackendAPI_Url.URL_POIList_Popular+categoryStr;
                }
                else {
                    url = Constains_BackendAPI_Url.URL_POIList_Popular;
                }
                break;
            case 2:
                //setListViewByRecomend();
                url = Constains_BackendAPI_Url.URL_POIList_Suggest;
                break;
            default:
                //url = "http://dev.mw.metropolia.fi/mofa/Wikitude_1/geoLocator/poi.json";
                url = Constains_BackendAPI_Url.URL_POIList_Distant;
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final ArrayList<Object_POI> result = new ArrayList<>();

        CoordinatorLayout myView = (CoordinatorLayout) inflater
                .inflate(R.layout.fragment_poi_list_tab, container, false);

        FloatingActionButton fab_cam = (FloatingActionButton)myView
                .findViewById(R.id.poi_list_fab_cam);
        fab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionHelper.isCameratPermissionAllow(getContext())){
                    Toast.makeText(getContext(), "No Permission", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getContext(), CamActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });

        listView = (RecyclerView) myView.findViewById(R.id.POIlistview);
        listView.setHasFixedSize(true);

        return myView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        final int sortingMethodID = getArguments().getInt(ARG_PARM1);

        if(isVisibleToUser && pois.isEmpty()){
            this.networkCall(new ListCallBack() {
                @Override
                public void onSuccess(ArrayList<Object_POI> Items) {
                    pois = Items;
                    adapter = new Adapter_ListAdapter(getContext(), pois, sortingMethodID);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    listView.setLayoutManager(mLayoutManager);
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    private void networkCall(final ListCallBack callBack){

        final ProgressDialog progressDialog = createProgressDialog(getActivity());

        final int sortingMethodID = getArguments().getInt(ARG_PARM1);

        final ArrayList<Object_POI> result = new ArrayList<>();

        RequestQueue queue = Network_SerialQueue.getSerialRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.e("Response size", response.length() + "");

                        for (int i = 0; i < response.length(); i++) {

                            String name = null, imgBase64 = null, reason = null;
                            int id = 0, disTo=0, rate_count=0;
                            Double rate_score=0.0;
                            switch (sortingMethodID){
                                case 1:
                                    try {
                                        rate_count = response.getJSONObject(i).getInt("rate_count");
                                        rate_score = response.getJSONObject(i).getDouble("rate_score");
                                        name = response.getJSONObject(i).getString("poi_name");
                                        imgBase64 = response.getJSONObject(i).getString("image");
                                        id = response.getJSONObject(i).getInt("id");
                                    } catch (Exception e) {
                                        Log.e("ResponsePopError", e.toString());
                                    }
                                    break;
                                case 2:
                                    try {
                                        id = response.getJSONObject(i).getInt("id");
                                        name = response.getJSONObject(i).getString("poi_name");
                                        imgBase64 = response.getJSONObject(i).getString("image");
                                        reason = response.getJSONObject(i).getString("reason");
                                    } catch (Exception e) {
                                        Log.e("ResponseSugError", e.toString());
                                    }

                                default:
                                    break;

                            }
                            Object_POI temp = new Object_POI(0, 0, name, id, imgBase64,null,disTo,rate_score,
                                    rate_count, null,0, reason,null,null);
                            result.add(temp);
                        }

                        callBack.onSuccess(result);
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG + " error", error.toString());
                        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
        );

        JsonArrayRequest modelNamesReq = new JsonArrayRequest(
                Request.Method.GET, Constains_BackendAPI_Url.URL_GetModelsName, null,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        ArrayList<String> nameList = new ArrayList<>();
                        ArrayList<Long> dateList = new ArrayList<>();
                        for (int i=0; i<response.length(); i++){
                            String nameTemp = null;
                            Long dateTemp = 0l;
                            try {
                                nameTemp = response.getJSONObject(i).getString("poi_name");
                                dateTemp = response.getJSONObject(i).getLong("date");
                            } catch (Exception e) {
                                Log.e("Name List Json Error", e.toString());
                            }
                            nameList.add(nameTemp);
                            dateList.add(dateTemp);
                        }
                        /**/

                        for (int i = 0; i < nameList.size(); i++){
                            Download3dModels(nameList.get(i), dateList.get(i));
                        }


                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Name List Req Error", error.toString());
                        progressDialog.dismiss();
                    }
                }
        );

        queue.add(jsonArrayRequest);
//        queue.add(modelNamesReq);
        progressDialog.show();

    }

    private Boolean Download3dModels(String fileName, long date) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        fileName= fileName.trim();

        File dir = new File(Environment.getExternalStorageDirectory()+"/3dModels"+"/");

        if (!dir.exists()){
            dir.mkdirs();
        }

        try {
            File file = new File (dir, fileName+".wt3");
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            //Log.e("Phone",sdf.format(file.lastModified()));
            //Log.e("DataBase",sdf.format(new Date(date*1000l)));
            if (file.exists() && file.lastModified() >= date*1000l){

                //Log.e("3d model not downloaded",sdf.format(file.lastModified()));
                return false;
            }
            else{
                String url = Constains_BackendAPI_Url.URL_3dModels+fileName+".wt3";
                URL downloadUrl = new URL(url);
                URLConnection ucon = downloadUrl.openConnection();
                ucon.connect();

                InputStream is = ucon.getInputStream();

                FileOutputStream fos = new FileOutputStream(file);

                byte data[] = new byte[1024];

                int current = 0;
                while ((current = is.read(data))!=-1){
                    fos.write(data,0,current);
                }
                is.close();
                fos.flush();
                fos.close();

                //Log.e("DownloadSucceed", fileName+".wt3 Downloaded");
                return true;
            }

        } catch (Exception e) {
            Log.e("Download3dError", e.toString());
            return false;
        }
    }

    private interface ListCallBack {
        void onSuccess(ArrayList<Object_POI> Items);
    }

}
