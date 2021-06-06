package android.example.walkee;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BroadcastService extends Service {
    private String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "com.example.backgoundtimercount";
    Intent intent = new Intent(COUNTDOWN_BR);
    CountDownTimer countDownTimer = null;

    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID=0;
    private static final String CHANNEL_ID="ch1";
    private static final String NOTIF_URL="http://google.co.id";
    private static final String ACTION_UPDATE_NOTIFICATION=
            BuildConfig.APPLICATION_ID+".ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION=
            BuildConfig.APPLICATION_ID+".ACTION_CANCEL_NOTIFICATION";
    private NotificationReceiver notificationReceiver=new NotificationReceiver();

    SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION);
        intentFilter.addAction(ACTION_CANCEL_NOTIFICATION);
        registerReceiver(notificationReceiver, intentFilter);

        Log.i(TAG,"Starting timer...");
        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        long millis = sharedPreferences.getLong("time",10000);
        if (millis / 1000 == 0) {
            millis = 10000;
        }
        countDownTimer = new CountDownTimer(millis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);
                intent.putExtra("countdown",millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                sendNotification();
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="Channel1";
            String desc="Description...";
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(desc);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(){
        Intent notifIntent=new Intent(this, MainActivity.class);
        PendingIntent notifPendingIntent=PendingIntent.getActivity(this,
                NOTIFICATION_ID, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancelIntent=new Intent(ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent=PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder=new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Hi There!")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("Look like it's time to take a walk!")
                .addAction(R.mipmap.ic_launcher,"Cancel",cancelPendingIntent)
                .addAction(R.mipmap.ic_launcher,"Go to App", notifPendingIntent)
                .setContentIntent(notifPendingIntent);
        Notification notification=notifyBuilder.build();
        notificationManager.notify(NOTIFICATION_ID,notification);
    }
    private void updateNotification(){
        Bitmap image= BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Intent cancelIntent=new Intent(ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent=PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder= new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Notif Title")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentText("Notif Content")
                .addAction(R.mipmap.ic_launcher,"Cancel", cancelPendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .setBigContentTitle("Notification Updated!"));
        Notification notification=notifyBuilder.build();
        notificationManager.notify(NOTIFICATION_ID,notification);
    }
    private void cancelNotification(){
        notificationManager.cancel(NOTIFICATION_ID);
    }
    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case ACTION_UPDATE_NOTIFICATION:
                    updateNotification();
                    break;
                case ACTION_CANCEL_NOTIFICATION:
                    cancelNotification();
                    break;
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

