package android.example.walkee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Riwayat extends AppCompatActivity {
    //background animation
    private ConstraintLayout constraintLayout;
    private AnimationDrawable animationDrawable;

    //bottom navigation
    private BottomNavigationView mMainNav;

    private TextView month;

    //listview history
    ArrayList<HistoryItems> history = new ArrayList<>();
    ListView listView;
    DatabaseReference database;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        TextView month = findViewById(R.id.month_A);

        //listview history
        listView = findViewById(R.id.list_view);
        database = FirebaseDatabase.getInstance().getReference("users");

        //date
        Date dateD =Calendar.getInstance().getTime();
        //Month
        DateFormat Monthformatter = new SimpleDateFormat("MMMM");
        String currentMonth = Monthformatter.format(dateD);
        //Day
        DateFormat Dayformatter = new SimpleDateFormat("dd");
        String currentDay = Dayformatter.format(dateD);

        //firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentuser = mAuth.getInstance().getCurrentUser().getUid();
            database.child(currentuser).child(currentMonth).addListenerForSingleValueEvent(new ValueEventListener() {
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

        month.setText(currentMonth);

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