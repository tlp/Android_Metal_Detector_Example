package com.jonathanblood.main;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Simple metal detector class.
 * @author Jonathan
 *
 */
public class MetalDetectorActivity extends Activity implements SensorEventListener {

	private TextView xTV;
	private TextView yTV;
	private TextView zTV;
	
	private ProgressBar mProgress;

	private SensorManager sensorManager = null;
	private float[] geomag = new float[3];
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up a SensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		setContentView(R.layout.main);

		// Text view setup
		xTV = (TextView) findViewById(R.id.xTV);
		yTV = (TextView) findViewById(R.id.yTV);
		zTV = (TextView) findViewById(R.id.zTV);

		// Initialise values.
		xTV.setText("X: 0.00");
		yTV.setText("Y: 0.00");
		zTV.setText("Z: 0.00");
		
		// Setup progress bar value.
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);
		mProgress.setMax(100);
	}

	public void onSensorChanged(SensorEvent sensorEvent) {
		synchronized (this) {
			geomag = sensorEvent.values.clone();

			if (geomag != null) {

				xTV.setText("X: "+Float.toString(geomag[0]));
				yTV.setText("Y: "+Float.toString(geomag[1]));
				zTV.setText("Z: "+Float.toString(geomag[2]));    
				
				mProgress.setProgress(getProgressBarValue(geomag[0]));
				
				//Metal detected.
				if( Math.abs(geomag[0]) > 50){
					playAlarm();
					Toast.makeText(this, "Metal detected", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Register this class as a listener for the sensors.
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		// Unregister the listener
		sensorManager.unregisterListener(this);
	}
	
	/**
	 * Method to play an alarm sound to signal half time or full time.
	 */
	private void playAlarm() {
		MediaPlayer mp = MediaPlayer.create(MetalDetectorActivity.this, R.raw.buzzer);
		
		if(!mp.isPlaying()){ //If mediaplayer is not already playing.
			
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}

			});
		}

	}
	
	/**
	 * Takes in a sensor value and returns a value to set the
	 * progress bar to depending on the sensor value.
	 * @param sensorValue
	 * @return
	 */
	private int getProgressBarValue(float sensorValue){
		sensorValue = Math.abs(sensorValue);
		
		if(sensorValue >= 10 &&  sensorValue < 30)
			return 15;
		else if(sensorValue >= 30 &&  sensorValue < 50)
			return 30;
		else if(sensorValue >= 30 &&  sensorValue < 50)
			return 30;
		else if(sensorValue >= 50 &&  sensorValue < 70)
			return 50;
		else if(sensorValue >= 70 &&  sensorValue < 100)
			return 75;
		else if(sensorValue >= 100)
			return 99;
		else 
			return 0;
	}

}