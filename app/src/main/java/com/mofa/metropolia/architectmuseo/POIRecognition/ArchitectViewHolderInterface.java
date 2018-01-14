package com.mofa.metropolia.architectmuseo.POIRecognition;

import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;

public interface ArchitectViewHolderInterface {
	
	/**
	 * 50km = architectView's default cullingDistance, return this value in "getInitialCullingDistanceMeters()" to not change cullingDistance.
	 */
	int CULLING_DISTANCE_DEFAULT_METERS = 10000;
	
	/**
	 * path to the architect-file (AR-Experience HTML) to launch
	 * @return
	 */
	String getARchitectWorldPath();
	
	/**
	 * url listener fired once e.g. 'document.location = "architectsdk://foo?bar=123"' is called in JS
	 * @return
	 */
	ArchitectUrlListener getUrlListener();
	
	/**
	 * @return layout id of your layout.xml that holds an ARchitect View, e.g. R.layout.camview
	 */
	int getContentViewId();
	
	/**
	 * @return Wikitude SDK license key, checkout www.wikitude.com for details
	 */
	String getWikitudeSDKLicenseKey();
	
	/**
	 * @return layout-id of architectView, e.g. R.id.architectView
	 */
	int getArchitectViewId();
	
	/**
	 * @return Implementation of Sensor-Accuracy-Listener. That way you can e.g. show prompt to calibrate compass
	 */
	SensorAccuracyChangeListener getSensorAccuracyListener();
	
	/**
	 * sets maximum distance to render places. In case your places are more than 50km away from the user you must adjust this value (compare 'AR.context.scene.cullingDistance').
	 * Return ArchitectViewHolder.CULLING_DISTANCE_DEFAULT_METERS to not change default behavior (50km range) or any positive float to set cullingDistance on architectView start.
	 * @return
	 */
	float getInitialCullingDistanceMeters();

}
