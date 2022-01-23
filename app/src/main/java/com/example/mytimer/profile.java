package com.example.mytimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile extends AppCompatActivity {
    private ImageButton btn_logout;
    private GoogleSignInAccount signInAccount;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private GoogleSignInClient mGoogleSignInClient;
    private String userName;
    private TextView noteView;
    private BottomNavigationView navView;
    private SongPlayer songPlayer = new SongPlayer();
    private MediaPlayer mediaPlayer;
    private String exerciseType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupView();
        extractUserName();
        connectDataBase();
        showSavedNote();
        setUpMenu();
    }
    /*Setup view*/
    public void setupView(){
        btn_logout = findViewById(R.id.btn_logout);
        noteView = findViewById(R.id.tv_mynote);
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        Intent intent = getIntent();
        exerciseType = intent.getStringExtra("type");
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut();
                finish();
            }
        });
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
                            return true;
                        }else{
                            songPlayer.pauseSong();
                            startInfoToast("Music OFF");
                            return true;
                        }
                    }
                    case R.id.menu_preset:{
                        showExercise();
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
    /*Activate exercise activity*/
    public void showExercise(){
        Intent intent = new Intent(profile.this, HelpActivity.class);
        intent.putExtra("type", exerciseType.toLowerCase());
        startActivity(intent);
    }

    public void connectDataBase(){
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    /*Extract username from the full user email*/
    /*Firebase does not except symbol '@' in JSON*/
    public void extractUserName(){
        String email = signInAccount.getEmail();
        String[] name = email.split("@");
        userName = name[0];
    }
    /*Show the saved note for the given user available in the database*/
    public void showSavedNote(){
        try{
            reference = reference.child(userName);
            Log.d("profile", reference.getKey());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("note")){
                        String note = snapshot.child("note").getValue().toString();
                        noteView.setText(note);
                    }else{
                        noteView.setText("");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "No note in database", Toast.LENGTH_SHORT).show();
        }
    }
    /*Move to activity add note*/
    public void addNote(View view) {
        Intent intent = new Intent(profile.this, AddNote.class);
        intent.putExtra("user", userName);
        intent.putExtra("type", exerciseType);
        startActivity(intent);
    }

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

    @Override
    protected void onStop() {
        super.onStop();
        songPlayer.stopPlayer();
    }
}