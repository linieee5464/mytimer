package com.example.mytimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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


public class MainActivity extends AppCompatActivity {
    /*objects to represent buttons*/
    private ImageButton btn_start;
    private ImageButton btn_stop;
    private ImageButton btn_pause;
    private ImageButton btn_resume;
    private BottomNavigationView navView;
    /*Timer minute input edit text*/
    private EditText timerMin;
    /*Timer second input edit text*/
    private EditText timerSec;
    /*Rest minute input edit text*/
    private EditText restMin;
    /*Timer second input edit text*/
    private EditText restSec;
    /*Repeat input edit text*/
    private EditText repeat;
    /*String to hold current second/minute count of timer*/
    private String currentSec;
    private String currentMin;
    /*The value originally set for timer*/
    private String setSec;
    private String setMin;
    /*String to hold current second/minute count of rest timer*/
    private String currentRestSec;
    private String currentRestMin;
    /*The value originally set for rest timer*/
    private String setRestSec;
    private String setRestMin;
    /*String to hold the exercise type*/
    private String exerciseType;
    /*Calculator object*/
    private CaloCalculator caloWatch = new CaloCalculator();

    private long timeLeft;
    private long restTimeLeft;
    private int cycle;
    /*Input check flags*/
    private Boolean hasRestTimerInit = false;
    private Boolean goodInput = false;
    private Boolean isRestSet = false;

    private CountDownTimer mCountDownTimer;
    private CountDownTimer restTimer;
    /*Notification channel ID*/
    private static final String channel_ID = "timer_notification";
    private static final int noti_ID = 0;
    private NotificationManager notiManager;
    /*Media player objects*/
    private MediaPlayer mediaPlayer;;
    private SongPlayer songPlayer = new SongPlayer();
    /*Google signin and firebase related objects*/
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setFilter();
        setUpMenu();
        createNotificationChannel();
        createRequest();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            startActivity(new Intent(MainActivity.this, LandingPage.class));
            finish();
        }
        else
            super.onBackPressed();
    }

    /*Set up view and media, firebase instances*/
    public void initView(){
        Intent intent = getIntent();
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        btn_pause = findViewById(R.id.btn_pause);
        btn_resume = findViewById(R.id.btn_resume);
        timerMin = findViewById(R.id.edt_timerMin);
        timerSec = findViewById(R.id.edt_timerSec);
        restMin = findViewById(R.id.edt_restTimeMin);
        restSec = findViewById(R.id.edt_restTimeSec);
        repeat = findViewById(R.id.edt_repeat);
        timerMin.setText(intent.getStringExtra("min"));
        timerSec.setText(intent.getStringExtra("sec"));
        exerciseType = intent.getStringExtra("type");

        mAuth = FirebaseAuth.getInstance();
        cycle = 1;
    }
/*Apply min, max filter to the input*/
    public void setFilter(){
        timerMin.setFilters(new InputFilter[]{new InputFilterMinMax("00", "60")});
        timerSec.setFilters(new InputFilter[]{new InputFilterMinMax("00", "60")});
        restMin.setFilters(new InputFilter[]{new InputFilterMinMax("00", "05")});
        restSec.setFilters(new InputFilter[]{new InputFilterMinMax("00", "60")});
    }
    /*Get the set value, check input is valid and start timer*/
    /*Update notification and update calo to show later*/
    public void button_start(View view){
        btn_start.setVisibility(View.GONE);
        btn_stop.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.VISIBLE);
        getSetTimer();
        checkInput();
        if(goodInput){
            sendNotification("Timer start!!!");
            countDown();
            updateCalo();
        }
    }
    /*Stop current timer and reset view*/
    /*Update notification*/
    public void button_stop(View view) {
        cycle = 0;
        btn_start.setVisibility(View.VISIBLE);
        btn_resume.setVisibility(View.GONE);
        btn_stop.setVisibility(View.GONE);
        btn_pause.setVisibility(View.GONE);
        sendNotification("Timer stops!!!");
        mCountDownTimer.cancel();
        resetTimerView();
    }
    /*Pause current timer*/
    /*Update notification*/
    public void button_pause(View view) {
        btn_pause.setVisibility(View.GONE);
        btn_resume.setVisibility(View.VISIBLE);
        mCountDownTimer.cancel();
        sendNotification("Timer pauses!!!");
    }
    /*Resume counter*/
    /*Update notification*/
    public void button_resume(View view) {
        btn_resume.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        sendNotification("Timer resume!!!");
        getTime();
        countDown();
        }
/*Set event listener for the bottom navigation bar*/
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
/*Count the rest timer*/
    public void countDownRest(){
        hasRestTimerInit = true;
        boolean haveRest = getSetRest();
        restTimeLeft = Integer.parseInt(currentRestMin)*60*1000 + Integer.parseInt(currentRestSec)*1000;
        restTimer = new CountDownTimer(restTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                restTimeLeft = millisUntilFinished;
                updateTimer(restTimeLeft, restMin, restSec);
            }
            @Override
            public void onFinish() {
                restMin.setText(setRestMin);
                restSec.setText(setRestSec);
                getTime();
                countDown();
            }
        }.start();
    }
/*Count the main timer*/
    public void countDown(){
        timeLeft = Integer.parseInt(currentMin)*60*1000 + Integer.parseInt(currentSec)*1000;
        mCountDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer(timeLeft, timerMin, timerSec);
            }
            @Override
            public void onFinish() {
                cycle--;
                if(cycle == -1){
                    repeat.setText(String.valueOf("0"));
                    btn_start.setVisibility(View.VISIBLE);
                    btn_pause.setVisibility(View.GONE);
                    btn_stop.setVisibility(View.GONE);
                    //showCalo();
                }else{
                    repeat.setText(String.valueOf(cycle));
                }
                resetTimerView();
                if(cycle > -1){
                    resetTimerView();
                    timerMin.setText(setMin);
                    timerSec.setText(setSec);
                    if(isRestSet){
                        countDownRest();
                    }else{
                        getTime();
                        countDown();
                    }

                }
            }
        }.start();

    }
    /*Get newest value from view*/
    public void getTime(){
        currentMin = timerMin.getText().toString();
        currentSec = timerSec.getText().toString();
        try {
            cycle = Integer.parseInt(repeat.getText().toString());
        }
        catch(Exception e) {
            cycle = 0;
        }
    }
    /*Update timer value to view*/
    public void updateTimer(long time, EditText min, EditText sec){
        int a = (int) (time/1000)/60;
        int b = (int) (time/1000)%60;
       
        String remainingMinute = String.valueOf(a);
        String remainingSec = String.valueOf(b);
        if(a < 10){
            remainingMinute = "0" + remainingMinute;
        }
        if(b < 10){
            remainingSec = "0" + remainingSec;
        }
        min.setText(remainingMinute);
        sec.setText(remainingSec);
    }
    /*Validate input, pop toast if input is not filled*/
    public void checkInput(){
        boolean checkRestTime = getSetRest();
        if(Integer.parseInt(currentMin) == 0 && Integer.parseInt(currentSec) == 0){
            startInfoToast("Incorrect Input");
            goodInput = false;
            btn_start.setVisibility(View.VISIBLE);
            btn_stop.setVisibility(View.GONE);
            btn_pause.setVisibility(View.GONE);
        }else{
            goodInput = true;
        }
    }
    /*Get value from input*/
    public void getSetTimer(){
        setMin = timerMin.getText().toString();
        setSec = timerSec.getText().toString();
        currentMin = timerMin.getText().toString();
        currentSec = timerSec.getText().toString();
        try {
            cycle = Integer.parseInt(repeat.getText().toString());
        }
        catch(Exception e) {
            cycle = 0;
        }
    }
    /*Get value from input*/
    public boolean getSetRest(){
        if(restMin.getText().toString().matches("") || restSec.getText().toString().matches("")){
            isRestSet = false;
            return false;
        }
        isRestSet = true;
        setRestMin = restMin.getText().toString();
        setRestSec = restSec.getText().toString();
        currentRestMin = restMin.getText().toString();
        currentRestSec = restSec.getText().toString();
        return true;
    }
    /*Reset view to default value*/
    public void resetTimerView(){
        timerMin.setText("00");
        timerSec.setText("00");
    }
    /*show customised toast*/
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
    /*Update the calo value*/
    public void updateCalo(){
        double a = Double.parseDouble(setMin);
        double b = Double.parseDouble(setSec)/60;
        double c = (1+cycle)*(a+b);
        caloWatch.calculateCalo(exerciseType,c);
    }
    /*Show calo dialog*/
    public void showCalo(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_dialog);
        TextView amount = dialog.findViewById(R.id.amount);
        amount.setText(String.format("%.2f", caloWatch.getCalo()));
        dialog.show();
    }
    /*Show login dialog*/
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
    /*Jump to exercise list view
    public void showExercise(){
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        intent.putExtra("type", exerciseType.toLowerCase());
        startActivity(intent);
    }*/

    public void createNotificationChannel(){
        notiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notiChannel = new NotificationChannel(channel_ID, "Timer Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notiChannel.setDescription("Notification from My Timer");
            notiManager.createNotificationChannel(notiChannel);
        }
    }
    /*Helper method to return the notification class*/
    public NotificationCompat.Builder getNotificationBuilder(String message){
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, channel_ID)
                .setContentTitle("Timer status change!!!")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic__message);
        return notifyBuilder;
    }
    /*Send noti to device notification bar*/
    public void sendNotification(String message){
        NotificationCompat.Builder notiBuilder = getNotificationBuilder(message);
        notiManager.notify(noti_ID, notiBuilder.build());
    }
    /*Create a login request by calling google service API*/
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    /*Create signin intent*/
    /*Reference to FireBase example*/
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    /*Callback method when result activity is triggered*/
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
    /*Verify Google Sign in with OAuth*/
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Intent intent = new Intent(getApplicationContext()..class);
                            //intent.putExtra("type", exerciseType.toLowerCase());
                            //startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}