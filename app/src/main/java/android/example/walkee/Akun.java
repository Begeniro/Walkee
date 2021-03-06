package android.example.walkee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;


public class Akun extends AppCompatActivity {
    //Login
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="Akun";
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private ImageView foto;


//    private CardView cv;
    private TextView nama;
    private TextView email;
    private int RC_SIGN_IN = 1;

    //background animation
    private ConstraintLayout constraintLayout;
    private AnimationDrawable animationDrawable;


    //bottom navigation
    private BottomNavigationView mMainNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

// ...
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

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
        mMainNav.setSelectedItemId(R.id.nav_account);

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
                        startActivity(new Intent(getApplicationContext()
                                ,Riwayat.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_account :
                        return true;
                }
                return false;
            }
        });

        //Login
        signInButton = findViewById(R.id.signin_btn);
        mAuth = FirebaseAuth.getInstance();
        btnSignOut = findViewById(R.id.signout_btn);
        foto = findViewById(R.id.foto);
        nama= findViewById(R.id.nama);
        email = findViewById(R.id.email);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mGoogleSignInClient.signOut();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Akun.this, "Logged Out",Toast.LENGTH_SHORT).show();
                btnSignOut.setVisibility(View.INVISIBLE);
                signInButton.setVisibility(View.VISIBLE);
                nama.setText(" ");
                email.setText("Silakan masuk kembali dengan google");
                Picasso.get().load(R.drawable.blank_profile_picture).into(foto);

//                Glide.with(getApplicationContext()).load(R.drawable.blank_profile_picture)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(foto);

//                cv.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser fUser){

//        cv.setVisibility(View.VISIBLE);

        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account!=null){
            btnSignOut.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
//            Log.d(personPhoto);

            nama.setText(personName);
            email.setText(personEmail);
            Picasso.get().load(personPhoto).into(foto);
            Picasso.get()
                    .load(personPhoto)
                    .resize(500, 500)
                    .centerCrop()
                    .error(R.drawable.blank_profile_picture)
                    .into(foto);
//            Glide.with(getApplicationContext()).load(personPhoto)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(foto);

        }
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