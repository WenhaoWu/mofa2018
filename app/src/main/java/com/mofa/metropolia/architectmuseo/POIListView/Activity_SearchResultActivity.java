package com.mofa.metropolia.architectmuseo.POIListView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mofa.metropolia.architectmuseo.Constains_BackendAPI_Url;
import com.mofa.metropolia.architectmuseo.LandingPage.Activity_MainActivity;
import com.mofa.metropolia.architectmuseo.Object_POI;
import com.mofa.metropolia.architectmuseo.POIDetail.Activity_POIActivity;
import com.mofa.metropolia.architectmuseo.R;
import com.mofa.metropolia.architectmuseo.Utils.DialogHelper;
import com.mofa.metropolia.architectmuseo.Utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Activity_SearchResultActivity extends AppCompatActivity {

    public static final String Tag_SearchQuery = "SEARCH_QUERY";
    public static final String Tag_SearchMode = "SEARCH_MODE";

    public static final String TAG_LOCATION_LAT = "LOCATION_LAT";
    public static final String TAG_LOCATION_LNG = "LOCATION_LNG";

    RequestQueue serialQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serialQueue = Network_SerialQueue.getSerialRequestQueue(this);

        final double cur_lat = getIntent().getDoubleExtra(TAG_LOCATION_LAT, Activity_POIMainListView.mlat);
        final double cur_lng = getIntent().getDoubleExtra(TAG_LOCATION_LNG, Activity_POIMainListView.mlong);

        final String searchQuery = getIntent().getStringExtra(Tag_SearchQuery);
        String query = searchQuery.trim();
        String url = Constains_BackendAPI_Url.URL_POISearch+ query;

        final TextView title = (TextView)findViewById(R.id.search_query);

        String mode = getIntent().getStringExtra(Tag_SearchMode);

        if (mode!=null){
            switch (mode){
                case "SearchYear":
                    int year = Integer.parseInt(query);
                    int from = year / (int)10;
                    from = from * 10;
                    int to = from + 9;
                    String years = " "+from+"-"+to;
                    title.setText(years);
                    break;
                case "SearchDesigner":
                    SpannableString content = new SpannableString(" "+query);
                    content.setSpan(new UnderlineSpan(), 1, content.length(), 0);
                    title.setText(content);
                    title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WebView webview = new WebView(getBaseContext());
                            setContentView(webview);
                            try {
                                String postData = "keyword=" + URLEncoder.encode(searchQuery, "UTF-8");
                                Log.e("PostData", postData);
                                webview.postUrl("http://www.mfa.fi/hakutulokset", postData.getBytes());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
        else {
            title.setText(" "+searchQuery+":");

        }


        final ProgressDialog PD = DialogHelper.createProgressDialog(Activity_SearchResultActivity.this);

        final ListView searchList = (ListView)findViewById(R.id.POISearchListview);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                          Log.e("SearchResultSize", response.length() + "");
                        if (response.length() == 0){
                            Toast.makeText(getBaseContext(), "Nothing to show", Toast.LENGTH_LONG).show();
                        }
                        else {
                            final ArrayList<Object_POI> resultList = new ArrayList<>();
                            int id = 0;
                            String name = null, imgBase64 = null, website= null;
                            double lat=0, lng=0, disTo = 0;
                            for (int i=0; i<response.length();i++){
                                try {
                                    name = response.getJSONObject(i).getString("poi_name");
                                    imgBase64 = response.getJSONObject(i).getString("image");
                                    id = response.getJSONObject(i).getInt("id");
                                    lat = response.getJSONObject(i).getDouble("lat");
                                    lng = response.getJSONObject(i).getDouble("lng");
                                    website = response.getJSONObject(i).getString("website");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                disTo= CalculationByDistance(cur_lat, cur_lng, lat, lng);
                                Object_POI temp = new Object_POI(lat,lng,name, id, imgBase64, null, disTo, 0, 0,
                                                                null, 0, null,website,null);
                                resultList.add(temp);
                            }

                            Log.e("ListSize", resultList.size()+"");
                            searchArrayAdapter adapter = new searchArrayAdapter(getBaseContext(),resultList);
                            searchList.setAdapter(adapter);

                            searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Activity_POIActivity.ARG_ID, resultList.get(position).getID());
                                    intent.setClass(getBaseContext(), Activity_POIActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        PD.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SearchVolleyError", error.toString());
                        PD.dismiss();
                    }
                });

        serialQueue.add(jsonArrayRequest);
        PD.show();

    }

    private class searchArrayAdapter extends ArrayAdapter<Object_POI>{

        private Context mContext;
        private List<Object_POI> values;

        public searchArrayAdapter(Context context, List<Object_POI> objects) {
            super(context, -1, objects);
            this.mContext = context;
            this.values = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.layout_poi_list_row_layout, parent, false);

            TextView Title = (TextView)rowView.findViewById(R.id.POIRowFriLine);
            TextView SecLine = (TextView)rowView.findViewById(R.id.POIRowSecLine);
            ImageView imgView = (ImageView)rowView.findViewById(R.id.POIRowImage);

            String uri = values.get(position).getImgBase64();
            ImageLoader.loadImageMax(getContext(), uri, imgView, 200,100);

            Title.setText(values.get(position).getName());

            String s = String.format("%.2f", values.get(position).getDisTo());
            SecLine.setText(s+" km");

            return rowView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__poi, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager)getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent();
                intent.putExtra(Activity_SearchResultActivity.Tag_SearchQuery, query);
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

    //http://stackoverflow.com/questions/14394366/find-distance-between-two-points-on-map-using-google-map-api-v2
    //offline method to calculate distance between two location
    //Not real time walking distance only geometric distance
    public double CalculationByDistance(double latS, double lngS, double latE, double lngE) {
        int Radius=6371;//radius of earth in Km
        double dLat = Math.toRadians(latE-latS);
        double dLon = Math.toRadians(lngE-lngS);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(latS)) * Math.cos(Math.toRadians(latE)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));

        double valueResult= Radius*c;

        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));

        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));

        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        return Radius * c;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Activity_MainActivity.foreground++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Activity_MainActivity.foreground--;
    }
}






















