package android.example.walkee;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

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
        Toast toast = Toast.makeText(context, "test", Toast.LENGTH_LONG);
        toast.show();
        SharedPreferences editor = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        editor.edit().clear().commit();




    }

}