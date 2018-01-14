package com.mofa.metropolia.architectmuseo.POIDetail;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mofa.metropolia.architectmuseo.LandingPage.Activity_MainActivity;
import com.mofa.metropolia.architectmuseo.R;

import java.util.ArrayList;
import java.util.List;

public class Activity_ImageFullView extends AppCompatActivity {

    public static final String PicListTag = "PictureListTag";

    private List<String> picList= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity__image_full_view);

        //getting the picture list from sharedPreferences
        SharedPreferences sp = getSharedPreferences("my_prefs", MODE_PRIVATE);
        int picCount = sp.getInt("Picture_size",0);
        for (int i=0; i<picCount; i++){
            picList.add(sp.getString("Picture_"+i, null));
        }

        //hiding the status bar
        View decorView = getWindow().getDecorView();
        int uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOption);

        ViewPager viewPager = (ViewPager)findViewById(R.id.Pager_ImageFullScreen);
        Adapter_ImageFullScreenAdapter adapter = new Adapter_ImageFullScreenAdapter(this, picList);
        viewPager.setAdapter(adapter);
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
