package android.example.walkee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private ConstraintLayout constraintLayout;
    private AnimationDrawable animationDrawable;

    private TrackerFragment trackerFragment;
    private Account accountFragment;

    TextView header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        header=findViewById(R.id.header);

        mMainFrame=findViewById(R.id.mainFrame);
        mMainNav=findViewById(R.id.bottNav);

        trackerFragment = new TrackerFragment();
        accountFragment = new Account();
        setFragment(trackerFragment);
        header.setText("Step Tracker");

        //BottomBar
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_tracker :
                        setFragment(trackerFragment);
                        header.setText("Step Tracker");
                        return true;
                    case R.id.nav_history :
                        setFragment(trackerFragment);
                        header.setText("History");
                        return true;
                    case R.id.nav_account :
                        setFragment(accountFragment);
                        header.setText("Account");
                        return true;
                    default :
                        return false;
                }
            }
        });

        getSupportActionBar().hide();

        // init constraintLayout
        constraintLayout = (ConstraintLayout) findViewById(R.id.gradientbg);

        // initializing animation drawable by getting background from constraint layout
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();

        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(2000);

        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(1500);

        mMainFrame=findViewById(R.id.mainFrame);
        mMainNav=findViewById(R.id.bottNav);

        trackerFragment = new TrackerFragment();
        accountFragment = new Account();

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

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

    }

}