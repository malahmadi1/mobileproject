package com.androidplot.demos;


import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognizedService extends IntentService {

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            int activityType = mostProbableActivity.getType();
            if (activityType == DetectedActivity.ON_FOOT) {
                Log.e("actiondetection", "on_foot");
                DetectedActivity betterActivity = walkingOrRunning(result.getProbableActivities());
                if (null != betterActivity)
                    mostProbableActivity = betterActivity;
            }
            Intent intent2 = new Intent("custom-event-name");
            if (mostProbableActivity.getType() == DetectedActivity.RUNNING){
                intent2.putExtra("message", "Running");
            }
            else if(mostProbableActivity.getType() == DetectedActivity.WALKING){
                Log.e("actionfound","walking" + mostProbableActivity.getConfidence());
                intent2.putExtra("message", "Walking");
            }
            else {
                intent2.putExtra("message", "Unknown");
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent2);
        }
    }
    private DetectedActivity walkingOrRunning(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        for (DetectedActivity activity : probableActivities) {
            if (activity.getType() == DetectedActivity.RUNNING || activity.getType() == DetectedActivity.WALKING) {
                if(myActivity == null)
                    myActivity = activity;
                else
                {
                    if (myActivity.getConfidence() < activity.getConfidence())
                        myActivity = activity;
                }
            }
        }
        return myActivity;
    }
}