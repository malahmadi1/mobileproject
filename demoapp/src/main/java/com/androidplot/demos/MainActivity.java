/*
 * Copyright 2015 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.androidplot.demos;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener, LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ArrayList<String> data = new ArrayList<String>();
    private static final String TAG = MainActivity.class.getName();
    public GoogleApiClient mApiClient;
    PendingIntent pendingIntent;
    Button start, stop;
    TextView ave_speed, cur_speed;
    TextView cur_status;
    LocationManager locationManager;
    Location last_location = new Location("");
    Location current_location = new Location("");
    double cul_dis = 0;
    long last_time = 0;
    long cur_time = 0;
    long cul_time = 0;
    boolean first_start = false;
    double action_duration = 0.0;
    double action_distance = 0.0;
    String ini_status = "Unknown";
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            set_status(message);
        }
    };

    // DO *NOT* CHANGE THIS LINE! (CI-MATCH-POPULATE)
    private static final String CRITTERCISM_APP_ID = null;

    ProgressBar mprogressBar;
    TextView tv_steps;
    SensorManager sensorManager;
    boolean running = false;
    ObjectAnimator anim;
    CountDownTimer mCountDownTimer;
    //    lowPass(float[] input, float[] output);
    static final float ALPHA = 0.25f;
    int stepsensor;
    public float corr;
    int i = 0;int j=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv_steps = (TextView)findViewById(R.id.tv_steps);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        mprogressBar.setProgress(0);
        tv_steps.setText("0");
        //corr = 114;
        Button Reset = (Button)findViewById(R.id.button);
        start = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);
        ave_speed = (TextView)findViewById(R.id.ave_speed);
        cur_speed = (TextView)findViewById(R.id.cur_speed);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        cur_status = (TextView)findViewById(R.id.status);
        Reset.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        TextView text1 = (TextView)findViewById(R.id.button);
                        tv_steps.setText("0");
                        mprogressBar.setMax(500);
                        mprogressBar.setProgress(0);
                        j = 0;
                        i = 0;
                        //  corr = Float.valueOf((String) tv_steps.getText());
                    }
                }
        );
        mprogressBar.setMax(500);

        ObjectAnimator anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, 100);
        anim.setDuration(15000);
    /*    anim.setInterpolator(new DecelerateInterpolator());
        anim.start();*/

        if (CRITTERCISM_APP_ID != null) {
            Log.d(TAG, "Crittercism initialized.");
            Crittercism.initialize(getApplicationContext(), CRITTERCISM_APP_ID);
        }

        Number [] numbers =  {1, 4, 2, 8, 4, 16, 8, 32, 16, 64,20};
        Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14,15};
        Intent intent = new Intent(MainActivity.this, AnimatedXYPlotActivity.class);
       // intent.putExtra("numbers",numbers);
        //intent.putExtra("domailLabel",numbers);
        //intent.putExtra("mylist", numbers);
        Button startAnimatedXYPlotExButton = (Button) findViewById(R.id.animatedXYPlotExButton);
        startAnimatedXYPlotExButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Number [] numbers =  {1, 4, 2, 8, 4, 16, 8, 32, 16, 64,20};
                //Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14,15};
                ArrayList<Integer> seriesOfNumbers = new ArrayList<Integer>();
                seriesOfNumbers.add(0,1);
                seriesOfNumbers.add(1,4);
                seriesOfNumbers.add(2,8);
                seriesOfNumbers.add(3,4);
                seriesOfNumbers.add(4,4);
                seriesOfNumbers.add(5,16);
                seriesOfNumbers.add(6,8);
                seriesOfNumbers.add(7,32);
                seriesOfNumbers.add(8,16);
                seriesOfNumbers.add(9,64);
                seriesOfNumbers.add(10,20);


                Intent intent = new Intent(MainActivity.this, AnimatedXYPlotActivity.class);
                intent.putIntegerArrayListExtra("numbers",seriesOfNumbers);

                //intent.putExtra("domailLabel",numbers);

                //startActivity(intnet);
                startActivity(intent);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first_start = true;
                last_time = System.currentTimeMillis()/1000;
                Intent intent = new Intent( MainActivity.this, ActivityRecognizedService.class );
                pendingIntent = PendingIntent.getService( MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 3000, pendingIntent );
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000,0, MainActivity.this);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cul_time = 0;
                cul_dis = 0;
                cur_speed.setText("current speed: 0 m/s");
                ave_speed.setText("average speed: 0m/s");
                cur_status.setText("Unknown");
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient,pendingIntent);
                locationManager.removeUpdates(MainActivity.this);
            }
        });
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        running  = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this,countSensor, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        }else{
            Toast.makeText(this,"Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running =  false;
    }

    protected void onClose(SensorEvent event){
        //   corr = Float.valueOf((String) tv_steps.getText());
    }
    @Override
    public void onSensorChanged ( final SensorEvent event){
        i++;
        if (i == 1)
            corr = event.values[0];
        //    corr = Float.valueOf((String) tv_steps.getText());
        if (running) {


            // tv_steps.setText("0");
/*            stepsensor = lowPass(event.values[0],stepsensor);
            tv_steps.setText(String.valueOf(stepsensor));*/
            //anim.start();
            tv_steps.setText(String.valueOf(event.values[0] - corr));
            if(!(tv_steps.getText().toString().equals("500.0"))) {
                mCountDownTimer = new CountDownTimer(5000000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                        j = j + 1;
                        //mprogressBar.setProgress(j);
                        mprogressBar.setProgress((int) (event.values[0] - corr));

                    }

                    @Override
                    public void onFinish() {
                        //Do what you want
                        j = j + 1;
                        mprogressBar.setProgress((int) (event.values[0] - corr));
                        tv_steps.setText("Task accomplished");
                        mprogressBar.setProgress(0);
                    }
                };
                mCountDownTimer.start();
            }
            else
            {
                tv_steps.setText("0");
            }
/*                mCountDownTimer=new CountDownTimer(5000,100) {
                @Override
                public void onTick ( long millisUntilFinished){
                    // Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                    //i++;
                    // mprogressBar.setProgress(i);
                    mprogressBar.setProgress((int) (event.values[0] - corr));

                }

                @Override
                public void onFinish () {
                    //Do what you want
                    //i++;
                    mprogressBar.setProgress((int) (event.values[0] - corr));
                }
                };*/
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onLocationChanged(Location location) {
        double distance = 0.0;
        Date currentDate= new Date(System.currentTimeMillis());
        cur_time = System.currentTimeMillis()/1000;
        double duration = cur_time - last_time;
        last_time = cur_time;
        cul_time += duration;
        if (cul_time == 0)
            cul_time = 1;
        if(first_start){
            first_start = false;
            last_location.setLatitude(location.getLatitude());
            last_location.setLongitude(location.getLongitude());
        }
        else{
            current_location.setLatitude(location.getLatitude());
            current_location.setLongitude(location.getLongitude());
            distance =current_location.distanceTo(last_location);
            cul_dis += distance;
            last_location.setLatitude(location.getLatitude());
            last_location.setLongitude(location.getLongitude());
        }
        cur_speed.setText("current speed" + location.getSpeed() + " m/s");
        ave_speed.setText("average speed:" + cul_dis/cul_time);
        if( !cur_status.getText().toString().equals(ini_status)){
            data.add(currentDate+";"+action_distance/action_duration+";"+ cur_status.getText()+";"+ action_duration);
            action_duration = 0;
            action_distance = 0;
            ini_status = cur_status.getText().toString();
            Log.e("datarecord",""+data);
        }
        else{
            action_distance += distance;
            action_duration += duration;
        }
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("MainActivity", "onStatusChanged(:)");
    }

    public void onProviderEnabled(String provider) {
        Log.i("MainActivity", "onProviderEnabled(:)");
    }

    public void onProviderDisabled(String provider) {
        Log.i("MainActivity", "onProviderDisabled(:)");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void set_status(String status){
        if (!status.equals(cur_status.getText())){
            cur_status.setText(status);
        }
    }
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
