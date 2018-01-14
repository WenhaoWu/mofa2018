package com.mofa.metropolia.architectmuseo.POIRecognition;

import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.mofa.metropolia.architectmuseo.POIDetail.Activity_POIActivity;
import com.mofa.metropolia.architectmuseo.R;
import com.mofa.metropolia.architectmuseo.WikitudeSDKConstants;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.ArchitectUrlListener;

public class CamActivity extends AbstractArchitectCamActivity {

	//private Receiver_DistanceResponseReceiver receiver;
	public static final String ARG_LOCATION = "Argument_location";
    private int mode;
    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mode = getIntent().getIntExtra("mode", mode);

        String imageFlag;
		if (savedInstanceState == null){
			Bundle extras = getIntent().getExtras();
			if (extras == null){
				imageFlag = null;
			}
			else {
				imageFlag = extras.getString("Image_Flag");
			}
		}
		else{
			imageFlag = (String)savedInstanceState.getSerializable("Image_Flag");
		}
		if (imageFlag == "True"){
			ImageView imageView = (ImageView)findViewById(R.id.ExampleImage);
			imageView.setImageResource(R.drawable.target_test);
		}
	}

	@Override
	public String getARchitectWorldPath() {
        switch (mode){
            case 1: return "Cloud_Recognition/index.html";
            case 2: return "Cloud_Recognition/3dmodel.html";
            case 3: return "Cloud_Recognition/video.html";
            default:return "Cloud_Recognition/3dmodel.html";
        }

	}

	@Override
	public String getActivityTitle() {
        return "Demo";
	}

	@Override
	public int getContentViewId() {
		return R.layout.sample_cam;
	}

	@Override
	public int getArchitectViewId() {
		return R.id.architectView;
	}
	
	@Override
	public String getWikitudeSDKLicenseKey() {
		return WikitudeSDKConstants.WIKITUDE_SDK_KEY;
	}

    @Override
    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new ArchitectView.SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && !CamActivity.this.isFinishing() && System.currentTimeMillis() - CamActivity.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                    Toast.makeText(CamActivity.this, R.string.compass_accuracy_low, Toast.LENGTH_LONG).show();
                    CamActivity.this.lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
                }
            }
        };
    }

    @Override
	public ArchitectUrlListener getUrlListener() {
		return new ArchitectUrlListener() {

			@Override
			public boolean urlWasInvoked(String uriString) {
            Uri invokedUri = Uri.parse(uriString);
            // pressed snapshot button
            if ("snapShotButton".equalsIgnoreCase(invokedUri.getHost())) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), Activity_POIActivity.class);
                intent.putExtra(Activity_POIActivity.ARG_ID, Integer.parseInt(invokedUri.getQueryParameter("name")));
                intent.putExtra(Activity_POIActivity.ARG_Des, "Test");
                Log.e("ResponseFromJS", invokedUri.getQueryParameter("name"));
                startActivity(intent);
            }
            return true;
			}
		};
	}
	
	@Override
	public float getInitialCullingDistanceMeters() {
		return ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS;
	}


    @Override
    protected void onStop() {
        super.onStop();
    }

}
