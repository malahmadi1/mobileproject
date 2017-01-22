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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.crittercism.app.Crittercism;
import java.lang.Object;
import java.util.ArrayList;
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    // DO *NOT* CHANGE THIS LINE! (CI-MATCH-POPULATE)
    private static final String CRITTERCISM_APP_ID = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CRITTERCISM_APP_ID != null) {
            Log.d(TAG, "Crittercism initialized.");
            Crittercism.initialize(getApplicationContext(), CRITTERCISM_APP_ID);
        }

        setContentView(R.layout.main);

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
}
