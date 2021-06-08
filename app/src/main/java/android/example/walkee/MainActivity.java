package android.example.walkee;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //background animation
    private ConstraintLayout constraintLayout;
    private AnimationDrawable animationDrawable;

    //bottom navigation
    private BottomNavigationView mMainNav;

    //step counter
    private TextView countView, kmView, calView;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    private double kmCount = 0;
    private double calCount = 0;

    //notification
    String TAG = "Main";
    TextView txt;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //no action bar
        getSupportActionBar().hide();

        // init constraintLayout
        constraintLayout = (ConstraintLayout) findViewById(R.id.gradientbg);

        // initializing animation drawable by getting background from constraint layout
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();

        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(2000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(1500);

        //init bottom navigation
        mMainNav=findViewById(R.id.bottNav);

        //set activity selected
        mMainNav.setSelectedItemId(R.id.nav_tracker);

        //BottomBar
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_tracker :
                        return true;

                    case R.id.nav_history :
                        startActivity(new Intent(getApplicationContext()
                        ,Riwayat.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_account :
                        startActivity(new Intent(getApplicationContext()
                                ,Akun.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        //step counter
        countView = findViewById(R.id.textViewCounter);
        kmView = findViewById(R.id.textViewKm);
        calView = findViewById(R.id.textViewCal);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent!= null){
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 6){
                        stepCount++;
                        kmCount = stepCount * 0.0008;
                        calCount = stepCount * 0.0035;

                    }
                    countView.setText(stepCount.toString());
                    kmView.setText(String.format("%.2f", kmCount));
                    calView.setText(String.format("%.2f", calCount));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        txt = findViewById(R.id.txt);
        calendar = Calendar.getInstance();
        dailyReset(MainActivity.this);

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());
        Log.i("DATE",date);

    }

    public void dailyReset(Context context) {
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 11);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, pi);
    }

    //notification
    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown",30000);
            Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);

            txt.setText( Long.toString(millisUntilFinished / 1000));
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);

            sharedPreferences.edit().putLong("time",millisUntilFinished).apply();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Update GUI
            updateGUI(intent);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //animation
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();
        }

        //step counter
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        if(!sharedPreferences.getString("date","").equals(date)) {
            SharedPreferences editor = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
            editor.edit().clear().commit();
            stepCount = 0;
        }else {
            stepCount = sharedPreferences.getInt("stepCount", 0);
        }

        //notification
        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i(TAG,"Registered broadcast receiver");
        stopService(new Intent(this,BroadcastService.class));
        Log.i(TAG,"Stopped service");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //animation
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }

        //step counter
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.putString("date",date);
        editor.apply();

        //notification
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG,"Unregistered broadcast receiver");
    }

    protected void onStop() {
        //notification
        try {
            Intent intent = new Intent(this,BroadcastService.class);
            startService(intent);
            Log.i(TAG,"Started Service");
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // Receiver was probably already
        }
        super.onStop();

        //step counter
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.putString("date",date);
        editor.apply();


    }

    @Override
    protected void onDestroy() {
        //notification

        super.onDestroy();
    }

}