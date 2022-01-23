package com.example.mytimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNote extends AppCompatActivity {
    private EditText note_title;
    private EditText note_body;
    private GoogleSignInAccount signInAccount;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private String userName;
    private BottomNavigationView navView;
    private SongPlayer songPlayer = new SongPlayer();
    private MediaPlayer mediaPlayer;
    private String exerciseType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        setupView();
        connectDatabase();
        setUpMenu();
    }
    /*Prepare the view and extract data from intent*/
    public void setupView(){
        note_title = findViewById(R.id.note_title);
        note_body = findViewById(R.id.note_body);
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        Intent intent = getIntent();
        userName = intent.getStringExtra("user");
        exerciseType = intent.getStringExtra("type");
    }
    /* Connect to database*/
    public void connectDatabase(){
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
    }
    /*Save note content to FireBase Realtime Database*/
    public void saveNote(View view) {
        String title = note_title.getText().toString();
        String body = note_body.getText().toString();
        UserNote tmpNote = new UserNote(title, body);
        reference.child(userName).setValue(tmpNote);
        finish();
    }
    /*Setup bottom navigator*/
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

    public void showExercise(){
        Intent intent = new Intent(AddNote.this, HelpActivity.class);
        intent.putExtra("type", exerciseType.toLowerCase());
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        songPlayer.stopPlayer();
    }
}