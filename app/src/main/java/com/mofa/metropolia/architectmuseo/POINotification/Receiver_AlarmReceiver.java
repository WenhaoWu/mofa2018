package com.mofa.metropolia.architectmuseo.POINotification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class Receiver_AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 123;
    public static final String ACTION = "com.museum.intentservicetest.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, Service_LocationTrackingService.class);
        i.putExtra("foo", "bar");

        context.startService(i);
    }
}
