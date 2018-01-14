package com.mofa.metropolia.architectmuseo.POIDetail;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.liangfeizc.slidepageindicator.CirclePageIndicator;
import com.mofa.metropolia.architectmuseo.Constains_BackendAPI_Url;
import com.mofa.metropolia.architectmuseo.LandingPage.Activity_MainActivity;
import com.mofa.metropolia.architectmuseo.POIListView.Activity_SearchResultActivity;
import com.mofa.metropolia.architectmuseo.POIListView.Fragment_TabFragment;
import com.mofa.metropolia.architectmuseo.POIRecognition.CamActivity;
import com.mofa.metropolia.architectmuseo.R;
import com.mofa.metropolia.architectmuseo.Utils.DialogHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_POIActivity extends AppCompatActivity {

    //Arguments that used by other activity
    public final static String ARG_Name = "PoiName";
    public final static String ARG_Des = "PoiDescript";
    public final static String ARG_ID = "PoiId";

    //Declare UI element
    private CirclePageIndicator mPageIndicator;
    private FloatingActionButton fab_navi, fab_share, fab_web;
    private FloatingActionsMenu fam_web;
    private ImageButton imgbtn_3d, imgbtn_audio, imgbtn_video, imgbtn_language;
    private Button btn_designer, btn_year;
    private TextView readMore,desTextView, titleTextView, compeTextView, creditTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_poi_detail);

        //Getting ID of POI from intent, intent only defined in Activity_POIMainListView, default id is 42(Central railway station)
        final int POI_id = getIntent().getIntExtra(ARG_ID,42);

        //SP for storing images(base64 string) and storing previous rate value
        final SharedPreferences sp = getSharedPreferences("my_prefs", MODE_PRIVATE);

        /**/
        final Toolbar toolbar = (Toolbar)findViewById(R.id.poi_detail_toolbar);
        setSupportActionBar(toolbar);


        /*
        Initilize the percentage of each layout, but at this point only imsSliderFrame is depends on screen height
        */
        //get the screen height pixel
        int height = getResources().getDisplayMetrics().heightPixels;
        FrameLayout imgSlider = (FrameLayout)findViewById(R.id.imgSliderFrame);
        //get the root layout params, since the image slider is under linearlayout.
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imgSlider.getLayoutParams();
        params.height = height *4 /10;
        imgSlider.setLayoutParams(params);


        titleTextView = (TextView)findViewById(R.id.POITitle);
        desTextView = (TextView)findViewById(R.id.POIDescription);
        readMore = (TextView) findViewById(R.id.poi_detail_readmore);
        compeTextView = (TextView)findViewById(R.id.poi_detail_compe);
        creditTextView = (TextView)findViewById(R.id.poi_detail_credit);
        ColorStateList def = titleTextView.getTextColors();

        btn_designer = (Button)findViewById(R.id.poi_detail_designerBtn);
        btn_designer.setTextColor(def);
        btn_year = (Button)findViewById(R.id.poi_detail_yearBtn);
        btn_year.setTextColor(def);

        //Defining img slide
        final ViewPager pager = (ViewPager)findViewById(R.id.image_pager);
        final Adapter_ImageSlideAdapter slideAdapter = new Adapter_ImageSlideAdapter(getSupportFragmentManager());

        //set image slider picture, title and description. We get it from backend
        getDetail(new DetailCallback() {
            @Override
            public void onSuccess(List<String> pic_List, final String title, final String descrip, final double lat, final double lng,
                                  final String model_flag, final String designer, final String year,
                                  final Map<String,String> lang_map, final Map<String, String> audio_map, final String u2bLink,
                                  final String website, final String competition, final String credits) {

                toolbar.setTitle(title);

                //storing the picture list to sharedPreferences
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("Picture_size", pic_List.size());
                for (int i = 0; i < pic_List.size(); i++) {
                    editor.remove("Picture_" + i);
                    editor.putString("Picture_" + i, pic_List.get(i));
                }
                editor.commit();

                //deal with the image slide and the indicator
                slideAdapter.setList(pic_List);
                pager.setAdapter(slideAdapter);
                pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mPageIndicator = (CirclePageIndicator) findViewById(R.id.imageIndicator);
                mPageIndicator.setViewPager(pager);

                //POI Title
                titleTextView.setText(title);

                //Architeture competition
                compeTextView.setText(competition);

                //Credit
                creditTextView.setText(credits);

                //Navigation btn. It opens google map to navigate
                fab_navi = (FloatingActionButton) findViewById(R.id.poi_detail_fab_navi);
                fab_navi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "google.navigation:q=" + lat + "," + lng;
                        Log.e("uri", uri);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                    }
                });

                //Share btn, no facebook cause it is a violation to its content policy
                fab_share = (FloatingActionButton) findViewById(R.id.poi_detail_fab_share);
                fab_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("plain/text");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "#MFA I love " + title + "!");
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));
                    }
                });

                //web fab menu
                fab_web = (FloatingActionButton)findViewById(R.id.poi_detail_fab_web);
                final String[] websites = website.split(",",-1);
                fab_web.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Website",websites[0]);

                        if (!websites[0].equals(null)){
                            showDesignerPopup(v,websites,1);
                        }
                        else {
                            Toast.makeText(getBaseContext(),R.string.detail_toast_web,Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                //3d img btn, check flag first
                imgbtn_3d = (ImageButton) findViewById(R.id.poi_detail_3dbtn);
                imgbtn_3d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model_flag != "null") {
                            Intent intent = new Intent();
                            intent.setClass(getBaseContext(), CamActivity.class);
                            intent.putExtra("mode", 2);
                            intent.putExtra("title", title);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lng);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getBaseContext(), "This POI doesn't support 3D model yet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //video btn, open youtube app to play video
                imgbtn_video = (ImageButton) findViewById(R.id.poi_detail_videobtn);
                imgbtn_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(u2bLink)));
                        }
                        catch (Exception e){
                            Toast.makeText(getBaseContext(),R.string.detail_toast_video, Toast.LENGTH_SHORT).show();
                        }

                        //next code to make a video overlay in cam view
                        /*Intent intent = new Intent();
                        intent.setClass(getBaseContext(), CamActivity.class);
                        intent.putExtra("mode", 3);
                        startActivity(intent);*/
                    }
                });

                //Putting the map keys into the popup menu as items, 1 means map flag is lang_map
                doDescrip(descrip);
                imgbtn_language = (ImageButton) findViewById(R.id.poi_detail_lngbtn);
                imgbtn_language.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(v, lang_map, 1);
                    }
                });

                //Putting the audio map keys into popup menu as items
                imgbtn_audio = (ImageButton)findViewById(R.id.poi_detail_audiobtn);
                imgbtn_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (audio_map.isEmpty()){
                            Toast.makeText(getBaseContext(), R.string.detail_toast_audio,Toast.LENGTH_SHORT).show();
                        }{
                            showPopup(v, audio_map, 2);
                        }

                    }
                });

                /*
                show the first designer if in the backend have multiple
                the rest will be placed in a popup list.
                */
                if (designer != null){
                    final String[] Designers = designer.split(",",-1);
                    if (designer.equals(Designers[0])){
                        btn_designer.setText(designer);
                        btn_designer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.putExtra(Activity_SearchResultActivity.Tag_SearchQuery, btn_designer.getText());
                                intent.putExtra(Activity_SearchResultActivity.Tag_SearchMode, "SearchDesigner");
                                intent.setClass(getBaseContext(), Activity_SearchResultActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        btn_designer.setText(Designers[0]+"...");
                        btn_designer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDesignerPopup(v, Designers,0);
                            }
                        });
                    }

                }

                //Search years
                btn_year.setText(year);
                btn_year.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(Activity_SearchResultActivity.Tag_SearchQuery, year);
                        intent.putExtra(Activity_SearchResultActivity.Tag_SearchMode, "SearchYear");
                        intent.setClass(getBaseContext(), Activity_SearchResultActivity.class);
                        startActivity(intent);
                    }
                });

            }

        }, POI_id);

        //Deal with the rating bar
        RatingBar ratingBar = (RatingBar)findViewById(R.id.POIRatingBar);

        if (sp.contains("POI_Rate"+POI_id)){
            ratingBar.setRating(sp.getFloat("POI_Rate"+POI_id,0));
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //ratingBar.setEnabled(false);
                float oldRate = 0;

                SharedPreferences.Editor ed = sp.edit();
                if (sp.getFloat("POI_Rate" + POI_id, 0) != 0) {
                    oldRate = sp.getFloat("POI_Rate" + POI_id, 0);
                    ed.remove("POI_Rate" + POI_id);
                }
                ed.putFloat("POI_Rate" + POI_id, rating);
                ed.commit();

                //send a request to backend to update the rateSum and rateCount;
                /*********************/
                toRate(new RateCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getBaseContext(), result, Toast.LENGTH_SHORT).show();
                    }
                }, POI_id, rating, oldRate);

            }
        });

    }


    private void doDescrip(final String descrip) {

        final Intent intent = new Intent();
        intent.putExtra(Activity_Description.ARG_DES, descrip);
        intent.setClass(getBaseContext(), Activity_Description.class);

        desTextView.setText(descrip);

        desTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

    }

    private interface DetailCallback {
        void onSuccess(List<String> pic_List, String title, String descrip, double lat, double lng, String model_flag,
                       String designer, String year, Map<String,String> lang_map,
                       Map<String,String> audio_map, String u2bLink, String website,
                       String competition, String credit);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public double getDetail(final DetailCallback callback, int id){

        String url = Constains_BackendAPI_Url.URL_POIDetail+ String.valueOf(id);

        final ProgressDialog PD = DialogHelper.createProgressDialog(Activity_POIActivity.this);

        final List<String> PicResult = new ArrayList<String>();
        final Map<String,String>LangMap = new HashMap<>();
        final Map<String, String>audioMap = new HashMap<>();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String title_temp = null, descrip_temp = null, designer= null, year=null,
                                u2bLink=null, website= null, competition=null,model_flag=null, credit = null;
                        double lat=0, lng=0;
                        int pic_count=0, lang_count=0, audio_count=0;
                        String keyTemp = null, valueTemp=null;

                        try {
                            title_temp = response.getJSONObject(0).getString("poi_name");
                            descrip_temp = response.getJSONObject(0).getString("description");
                            lat = response.getJSONObject(0).getDouble("lat");
                            lng = response.getJSONObject(0).getDouble("lng");
                            model_flag = response.getJSONObject(0).getString("Model_flag");
                            designer = response.getJSONObject(0).getString("designer");
                            year = response.getJSONObject(0).getString("year");
                            u2bLink = response.getJSONObject(0).getString("Youtube_Link");
                            website = response.getJSONObject(0).getString("website");
                            competition = response.getJSONObject(0).getString("architecture_composition");
                            credit = response.getJSONObject(0).getString("credit");

                            //Parse pictures
                            pic_count = response.getJSONObject(1).getJSONArray("multiple_image").length();
                            for (int i=0; i<pic_count; i++){
                                PicResult.add(response.getJSONObject(1).getJSONArray("multiple_image").getString(i));
                            }

                            //Parse languages
                            lang_count = response.getJSONArray(2).length();
                            keyTemp = "English";
                            valueTemp = descrip_temp;
                            LangMap.put(keyTemp,valueTemp);
                            for (int i=0; i<lang_count;i++){
                                keyTemp = response.getJSONArray(2).getJSONObject(i).getString("lang_name");
                                valueTemp = response.getJSONArray(2).getJSONObject(i).getString("lang_description");
                                LangMap.put(keyTemp,valueTemp);
                            }

                            //Parse Audios
                            audio_count = response.getJSONArray(3).length();
                            for (int i=0; i<audio_count;i++){
                                keyTemp = response.getJSONArray(3).getJSONObject(i).getString("track_title");
                                valueTemp = response.getJSONArray(3).getJSONObject(i).getString("track_id");
                                audioMap.put(keyTemp,valueTemp);
                            }


                        } catch (Exception e) {
                            Log.e("JsonPharseError", e.toString());
                        }

                        PD.dismiss();
                        callback.onSuccess(PicResult, title_temp, descrip_temp, lat, lng, model_flag,
                                            designer, year,LangMap, audioMap, u2bLink, website,competition,credit);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        PD.dismiss();
                        Log.e("Volley error", error.toString());
                    }
                });

        queue.add(jsonArrayRequest);
        PD.show();
        return 0;
    }

    public interface RateCallback{
        void onSuccess(String result);
    }

    public void toRate(final RateCallback callback, int id, float newRate, float oldRate){
        String parms = "id="+id+"&rate="+newRate+"&oldRate="+oldRate;
        String url = Constains_BackendAPI_Url.URL_POIRate+parms;
        Log.e("RateURL", url);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rate error", error.toString());
                    }
                }
        );

        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__poimain_detail, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelable(ARG_ID, );
    }

    private boolean isPlaying = false;
    private void showPopup(View v, final Map<String,String> map, final int mapFlag){

        PopupMenu popup = new PopupMenu(this,v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_detail_popup, popup.getMenu());

        final Intent svc = new Intent(getApplicationContext(),Service_audioService.class);

        int id = 100;
        for (String keyTmep: map.keySet()){
            id++;
            popup.getMenu().add(0,id,100,keyTmep);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                String valueTemp = map.get(item.getTitle());

                switch (mapFlag){
                    case 1:
                        doDescrip(valueTemp);
                        break;
                    case 2:
                        if (isPlaying){
                            getApplicationContext().stopService(svc);
                        }
                        else {
                            svc.putExtra(Service_audioService.ARG_TRACK, valueTemp);
                            getApplicationContext().startService(svc);
                        }
                        isPlaying = !isPlaying;
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        popup.show();
    }

    private void showDesignerPopup(View v, String[] designers, final int flag) {
        PopupMenu pm = new PopupMenu(this, v);
        MenuInflater inflater = pm.getMenuInflater();
        inflater.inflate(R.menu.menu_designer_popup, pm.getMenu());
        for (int i=0; i<designers.length; i++){
            pm.getMenu().add(1,i,100,designers[i]);
        }
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (flag){
                    case 0:
                        Intent intent = new Intent();
                        intent.putExtra(Activity_SearchResultActivity.Tag_SearchQuery, item.getTitle());
                        intent.putExtra(Activity_SearchResultActivity.Tag_SearchMode, "SearchDesigner");
                        intent.setClass(getBaseContext(), Activity_SearchResultActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getTitle().toString()));
                        startActivity(browserIntent);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
        pm.show();
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
