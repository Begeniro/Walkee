package android.example.walkee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

//    public AlarmReceiver2(SharedPreferences sharedPreferences) {
//        this.sharedPreferences = sharedPreferences;
//    }
//
//    SharedPreferences sharedPreferences;

    public void onReceive (Context context, Intent intent) {
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//        }
        Log.i("ALARM RECEIVER","ONRECEIVED");


    }

}