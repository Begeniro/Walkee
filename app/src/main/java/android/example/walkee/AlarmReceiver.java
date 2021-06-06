package android.example.walkee;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    MainActivity mainActivity;

    public void onReceive (Context context, Intent intent) {
//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//        }
        Toast toast = Toast.makeText(context, "test", Toast.LENGTH_LONG);
        toast.show();
        SharedPreferences settings = context.getSharedPreferences("stepCount", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        mainActivity.stepCount = 0;


    }

}
