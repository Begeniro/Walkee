package android.example.walkee;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.PrivateKey;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Riwayat extends AppCompatActivity {
    //background animation
    private ConstraintLayout constraintLayout;
    private AnimationDrawable animationDrawable;

    //bottom navigation
    private BottomNavigationView mMainNav;

    private Button month;
    private Button datePicker;
    private int stringLimit = 3;
    private Button lastMonth;
    private Button thisMonth;

    //listview history
    ArrayList<HistoryItems> history = new ArrayList<>();
    ListView listView;
    DatabaseReference database;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        month = findViewById(R.id.buttonMont);
        lastMonth = findViewById(R.id.lastMonthButton);
        thisMonth = findViewById(R.id.thisMonthButton);

        //listview history
        listView = findViewById(R.id.list_view);
        database = FirebaseDatabase.getInstance().getReference("users");

        //date
        Date dateD =Calendar.getInstance().getTime();
        //Month
        DateFormat Monthformatter = new SimpleDateFormat("MMMM");
        String currentMonth = Monthformatter.format(dateD).substring(0,stringLimit);
        //Day
        DateFormat Dayformatter = new SimpleDateFormat("dd");
        String currentDay = Dayformatter.format(dateD);
        //Year
        DateFormat Yearformatter = new SimpleDateFormat("yyyy");
        String currentYear = Yearformatter.format(dateD);

        //firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentuser = mAuth.getInstance().getCurrentUser().getUid();
            database.child(currentuser).child(currentYear).child(currentMonth).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    history.clear();
                    for (DataSnapshot historys : snapshot.getChildren()){
                        HistoryItems historyclass = historys.getValue(HistoryItems.class);
                        Log.d("zxy", "cek " + historyclass.getDay());
                        history.add(historyclass);
                    }
                    HistoryAdapter historyAdapter = new HistoryAdapter(Riwayat.this, history) ;
                    listView.setAdapter(historyAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    history.clear();
                    for (DataSnapshot historys : snapshot.getChildren()) {
                        HistoryItems historyclass = historys.getValue(HistoryItems.class);
                        Log.d("zxy", "cek " + historyclass.getDay());
                        history.add(historyclass);
                    }
                    HistoryAdapter historyAdapter = new HistoryAdapter(Riwayat.this, history);
                    listView.setAdapter(historyAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

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
        mMainNav.setSelectedItemId(R.id.nav_history);

        //BottomBar
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_tracker :
                        startActivity(new Intent(getApplicationContext()
                                ,MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_history :
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



        datePicker =findViewById(R.id.buttonMont);
        //MaterialDatePicker
        MaterialDatePicker.Builder dateMbuilder= MaterialDatePicker.Builder.datePicker();
        dateMbuilder.setTitleText("Select a Date");
        dateMbuilder.setTheme(R.style.ThemeOverlay_App_DatePicker);
        MaterialDatePicker<Long> materialDatePicker = dateMbuilder.build();

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick(Long selection) {
                String mDateSelected = materialDatePicker.getHeaderText();

                TimeZone timeZoneUTC = TimeZone.getDefault();
                // It will be negative, so that's the -1
                int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;

                // Create a date format, then a date object with our offset
                SimpleDateFormat simpleFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = new Date(selection + offsetFromUTC);

                String mDateS = simpleFormat.format(date);
                String montLimit = mDateS.substring(0,stringLimit);

                int midCharsStart = ((mDateS.length() + 2) / 2) - 3;
                int midCharsEnd = midCharsStart + 2;
                String SpacedayLimit = mDateS.substring(midCharsStart, midCharsEnd);
                String dayLimit = SpacedayLimit.substring(SpacedayLimit.indexOf(' ') + 1);

                if (month != null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String currentuser = mAuth.getInstance().getCurrentUser().getUid();
                        database.child(currentuser).child(currentYear).child(montLimit).orderByKey().equalTo(dayLimit).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                history.clear();
                                for (DataSnapshot historys : snapshot.getChildren()){
                                    HistoryItems historyclass = historys.getValue(HistoryItems.class);
                                    Log.d("zxy", "cek " + historyclass.getDay());
                                    history.add(historyclass);
                                }
                                HistoryAdapter historyAdapter = new HistoryAdapter(Riwayat.this, history) ;
                                listView.setAdapter(historyAdapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else{
                        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                history.clear();
                                for (DataSnapshot historys : snapshot.getChildren()) {
                                    HistoryItems historyclass = historys.getValue(HistoryItems.class);
                                    Log.d("zxy", "cek " + historyclass.getDay());
                                    history.add(historyclass);
                                }
                                HistoryAdapter historyAdapter = new HistoryAdapter(Riwayat.this, history);
                                listView.setAdapter(historyAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                }
            }else {
                }
            }
        });



        lastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                Date minusMonth = cal.getTime();
                String lastMonth = Monthformatter.format(minusMonth).substring(0,stringLimit);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String currentuser = mAuth.getInstance().getCurrentUser().getUid();
                    database.child(currentuser).child(currentYear).child(lastMonth).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            history.clear();
                            for (DataSnapshot historys : snapshot.getChildren()){
                                HistoryItems historyclass = historys.getValue(HistoryItems.class);
                                Log.d("zxy", "cek " + historyclass.getDay());
                                history.add(historyclass);
                            }
                            HistoryAdapter historyAdapter = new HistoryAdapter(Riwayat.this, history) ;
                            listView.setAdapter(historyAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        thisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String currentuser = mAuth.getInstance().getCurrentUser().getUid();
                    database.child(currentuser).child(currentYear).child(currentMonth).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            history.clear();
                            for (DataSnapshot historys : snapshot.getChildren()){
                                HistoryItems historyclass = historys.getValue(HistoryItems.class);
                                Log.d("zxy", "cek " + historyclass.getDay());
                                history.add(historyclass);
                            }
                            HistoryAdapter historyAdapter = new HistoryAdapter(Riwayat.this, history) ;
                            listView.setAdapter(historyAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }
    }


}