package com.mofa.metropolia.architectmuseo.POIRecognition;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mofa.metropolia.architectmuseo.LandingPage.Activity_MainActivity;
import com.mofa.metropolia.architectmuseo.POIListView.Activity_POIMainListView;
import com.mofa.metropolia.architectmuseo.POINotification.Receiver_AlarmReceiver;
import com.mofa.metropolia.architectmuseo.R;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectStartupConfiguration;

import java.io.IOException;

import io.proximi.proximiiolibrary.Proximiio;
import io.proximi.proximiiolibrary.ProximiioFactory;
import io.proximi.proximiiolibrary.ProximiioListener;

public abstract class AbstractArchitectCamActivity extends AppCompatActivity implements ArchitectViewHolderInterface {
    private Proximiio proximiio;
    private ProximiioListener listener;

	protected ArchitectView architectView;
	protected ArchitectUrlListener urlListener;
    protected ArchitectView.SensorAccuracyChangeListener sensorAccuracyListener;

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		/* pressing volume up/down should cause music volume changes */
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.setContentView(this.getContentViewId());
		this.setTitle(this.getActivityTitle());
		/* set AR-view for life-cycle notifications etc. */
		this.architectView = (ArchitectView)this.findViewById( this.getArchitectViewId()  );
		final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration( );

		config.setLicenseKey("anWYNS8+REWsLfaBV7G4gO7N8TfOAe8RzoWoSVN6W23C1Aa4Pq6tv2ZdrXMgDavCWCFTOw+iXlOStYgqU4GGBy9BYIUlpSC8qxKWHcbeUQ9YLm5I/wVOD/LDeXM+PNCv/5muBmyhv/7kUeDVEw/L2TEHBOaN01yn89udaRg484VTYWx0ZWRfX4L5Iz0V5H3jWEZhmBWmV2nb0Fg3o0LncpgSKD6l0G9lj9zjM0FUciO6OcLngLFNwVfTZliOpzSYcZAkENglBezeMGakG+gk/2Rs/KMsgxK76C4Gv6qe5vA3C8JAfmu065e4KsbdPX9HLsuQMT492RsV+2YIcJMRJi34AN8N6znDIEjtGeVkYK2UOLK/22nCfCypPVgq9NVa9V2XwiamTlpi5Os0/3iDIVFZxrCi4BqYCx+mg/FyYr7xpwrBvZHw8s4GwA+BkyoaxzVJcprT7Nkai/ERMws+OaMPFIycGD4ChgxCrBqnDJuL1WhkvSplroV7uESiGHWM3Rtq/p8GLfnSgOZQ20JJN0NeKvNqavgYFuz1zr6pXvHtfwXyY7dv9jxFEDI7qLqCYj0WcPfR+nnrv7gwbwvaEtaFJ5BgcnKi4sEkoyhZglu33FcKASgJqQdPJKfzJJdRdUJzWkC5ioimc6N1XgkG/ye6B+DLyNDqrw7CMloZetI=");
		try {
			/* first mandatory life-cycle notification */
			this.architectView.onCreate( config );
		} catch (RuntimeException rex) {
			this.architectView = null;
			Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT)
					.show();
			Log.e(this.getClass().getName(), "Exception in ArchitectView.onCreate()", rex);
		}
        this.urlListener = this.getUrlListener();
		if (this.urlListener != null && this.architectView != null) {
			this.architectView.registerUrlListener( this.getUrlListener() );
		}


        this.sensorAccuracyListener = this.getSensorAccuracyListener();
	}

    @Override
    protected void onStart() {
        super.onStart();
        proximiio = ProximiioFactory.getProximiio(this, this);
        listener = new ProximiioListener() {
            @Override
            public void position(double lat, double lon, double accuracy) {
                if ( AbstractArchitectCamActivity.this.architectView != null ) {
                    AbstractArchitectCamActivity.this.architectView
							.setLocation( lat, lon, 0, (float)accuracy );
                }
            }
        };
        proximiio.addListener(listener);
		Activity_MainActivity.foreground++;
    }

	@Override
	protected void onPostCreate( final Bundle savedInstanceState ) {
		super.onPostCreate(savedInstanceState);
		if ( this.architectView != null ) {
			this.architectView.onPostCreate();

			try {
				this.architectView.load(this.getARchitectWorldPath());
                if(this.getARchitectWorldPath().equals("Cloud_Recognition/3dmodel.html")){
                    String title = getIntent().getStringExtra("title").replaceAll("\\s", "");
                    Double   lat = getIntent().getDoubleExtra("lat", 0);
                    Double lng = getIntent().getDoubleExtra("lng", 0);
                    architectView
							.callJavascript("Recognition.createModel('" + title + "'," + lat + "," + lng + ")");
                }

				if (this.getInitialCullingDistanceMeters() != 1) {
					//ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS
					this.architectView.setCullingDistance( this.getInitialCullingDistanceMeters() );
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        if ( this.architectView != null ) {
            this.architectView.onResume();
            if (this.sensorAccuracyListener!=null) {
                this.architectView.registerSensorAccuracyChangeListener( this.sensorAccuracyListener );
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( this.architectView != null ) {
            this.architectView.onPause();
            if ( this.sensorAccuracyListener != null ) {
                this.architectView.unregisterSensorAccuracyChangeListener( this.sensorAccuracyListener );
            }
        }
    }

	@Override
	protected void onStop() {
		super.onStop();
        proximiio.removeListener(listener);
		Activity_MainActivity.foreground--;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if ( this.architectView != null ) {
			this.architectView.onDestroy();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if ( this.architectView != null ) {
			this.architectView.onLowMemory();
		}
	}

	public abstract String getActivityTitle();

	@Override
	public abstract String getARchitectWorldPath();

	@Override
	public abstract ArchitectUrlListener getUrlListener();

	@Override
	public abstract int getContentViewId();

	@Override
	public abstract String getWikitudeSDKLicenseKey();

	@Override
	public abstract int getArchitectViewId();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.location_start) {
			scheduleAlarm();
			return true;
		}
		else if(id == R.id.location_stop){
			cancelAlarm();
			return true;
		}
		else if(id == R.id.show_list){
			Intent intent = new Intent();
			intent.setClass(this, Activity_POIMainListView.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void scheduleAlarm(){
		Log.e("MySer", "startAlarm");
		Intent intent = new Intent(getApplicationContext(), Receiver_AlarmReceiver.class);
		//create a pendingIntent to be triggered when the alarm goes off
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, Receiver_AlarmReceiver.REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		//alarm is set from now
		long firstMillis = System.currentTimeMillis();
		//setup periodic alarm every 5 seconds
		AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 5*1000, pIntent);

	}

	public void cancelAlarm() {
		Intent intent = new Intent(getApplicationContext(), Receiver_AlarmReceiver.class);
		final PendingIntent pIntent = PendingIntent.getBroadcast(this, Receiver_AlarmReceiver.REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pIntent);
	}

}