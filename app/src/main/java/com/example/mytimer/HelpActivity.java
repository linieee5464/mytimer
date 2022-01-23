package com.example.mytimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;


public class HelpActivity extends AppCompatActivity {
    private ListView lv_exercises;
    private ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    private CustomArrayAdapter adapter;
    private ExerciseInfo chosenExercise;
    private TextView title_view;
    private TextView length_view;
    private TextView suggestion_view;
    private BottomNavigationView navView;
    private SongPlayer songPlayer = new SongPlayer();
    private MediaPlayer mediaPlayer;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private String exerciseType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        createRequest();
        initView();
        loadViewInBackGround();
        setUpMenu();
    }
    /*Use Async Task to fetch data from FireBase to populate listview*/
    class SimpleAsyncTask2 extends AsyncTask<Void, Exercise, Void> {
        CustomArrayAdapter adapt_Ref;
        DatabaseReference dbRef;

        @Override
        protected void onPreExecute() {
            adapt_Ref = (CustomArrayAdapter) lv_exercises.getAdapter();
            dbRef = FirebaseDatabase.getInstance().getReference(chosenExercise.getName());
        }
        /*Main code, called when detect any child node*/
        @Override
        protected Void doInBackground(Void... voids) {
            dbRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Exercise tmpExercise = new Exercise();
                    tmpExercise.setName(snapshot.child("name").getValue().toString());
                    tmpExercise.setDescription(snapshot.child("description").getValue().toString());
                    tmpExercise.setImage(BitmapFactory.decodeResource(getResources(), chosenExercise.getIDImage().get(snapshot.child("id").getValue().toString())));
                    publishProgress(tmpExercise);
                }

                @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

                @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override public void onCancelled(@NonNull DatabaseError error) { }
            });
            return null;
        }
        /*Update list item*/
        @Override
        protected void onProgressUpdate(Exercise... values){
            adapt_Ref.add(values[0]);
        }
    }
    /*Init the view*/
    public void initView(){
        title_view = findViewById(R.id.ex_title);
        length_view = findViewById(R.id.ex_length);
        suggestion_view = findViewById(R.id.ex_suggestion);
        lv_exercises = findViewById(R.id.lv_exercises);
        Intent intent = getIntent();
        exerciseType = intent.getStringExtra("type");
        chosenExercise = new ExerciseInfo(exerciseType);
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        title_view.setText(chosenExercise.getName().toUpperCase());
        length_view.setText(chosenExercise.getLength());
        suggestion_view.setText(chosenExercise.getSuggestion());
        adapter = new CustomArrayAdapter(this, R.layout.listview_item, exercises);
        lv_exercises.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
    }
    /*Run the AsyncTask*/
    public void loadViewInBackGround(){
        new SimpleAsyncTask2().execute();
    }
    /*Setup bottom navigation bar*/
    public void setUpMenu(){
        navView = findViewById(R.id.menu_bar);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_stat:{
                        if(!songPlayer.getPlayerStatus()){
                            songPlayer.playSong(mediaPlayer);
                            startInfoToast("Music ON");
                        }else{
                            songPlayer.pauseSong();
                            startInfoToast("Music OFF");
                        }
                        return true;
                    }

                    case R.id.menu_note:{
                        showLogin();
                        return true;
                    }
                    case R.id.menu_home:{
                        finish();
                        return true;
                    }
                }
                return false;
            }
        });
    }
    /*The code below is implemented the same to MainActivity*/
    public void startInfoToast(String message){
        Toast toast = new Toast(getApplicationContext());
        View view = LayoutInflater.from(this).inflate(R.layout.custom_toast, null);
        TextView toastTextView = view.findViewById(R.id.toast_cus);
        toastTextView.setText(message);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 170);
        toast.show();
    }

    public void showLogin(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.login_dialog);
        Button btn_login = dialog.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        dialog.show();
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
                firebaseAuthWithGoogle(account);
            } catch (Throwable e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(getApplicationContext(),profile.class);
                            intent.putExtra("type", exerciseType);
                            startActivity(intent);
                        } else {
                            Toast.makeText(HelpActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        songPlayer.stopPlayer();
    }
}