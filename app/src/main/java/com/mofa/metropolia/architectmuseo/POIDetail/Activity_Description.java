package com.mofa.metropolia.architectmuseo.POIDetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mofa.metropolia.architectmuseo.LandingPage.Activity_MainActivity;
import com.mofa.metropolia.architectmuseo.R;

public class Activity_Description extends AppCompatActivity {

    public static final String ARG_DES = "Argument_description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_description);

        TextView textView = (TextView)findViewById(R.id.poi_detail_description);
        textView.setText(getIntent().getStringExtra(ARG_DES));

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
