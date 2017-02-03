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
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;

import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener {

    private static final String TAG = MainActivity.class.getName();

    // DO *NOT* CHANGE THIS LINE! (CI-MATCH-POPULATE)
    private static final String CRITTERCISM_APP_ID = null;

    ProgressBar mprogressBar;
    TextView tv_steps;
    SensorManager sensorManager;
    boolean running = false;
    //    lowPass(float[] input, float[] output);
    static final float ALPHA = 0.25f;
    int stepsensor;
    public float corr;
    int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv_steps = (TextView)findViewById(R.id.tv_steps);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        tv_steps.setText("0");
        //corr = 114;
        Button Reset = (Button)findViewById(R.id.button);
        Reset.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        TextView text1 = (TextView)findViewById(R.id.button);
                        tv_steps.setText("0");
                        i = 0;
                        //  corr = Float.valueOf((String) tv_steps.getText());
                    }
                }
        );
        ObjectAnimator anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, 100);
        anim.setDuration(15000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

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
    public void onSensorChanged(SensorEvent event) {
        i++;
        if( i == 1)
            corr = event.values[0];
        //    corr = Float.valueOf((String) tv_steps.getText());
        if(running){
            // tv_steps.setText("0");
/*            stepsensor = lowPass(event.values[0],stepsensor);
            tv_steps.setText(String.valueOf(stepsensor));*/

            tv_steps.setText(String.valueOf(event.values[0] - corr));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
