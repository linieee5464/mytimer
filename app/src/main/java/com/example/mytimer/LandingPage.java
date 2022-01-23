package com.example.mytimer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LandingPage extends AppCompatActivity {
    private String exerciseChoice = "Cardio";
    private AlertDialog suggestionDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
    }
    /*On click for the button*/
    public void btn_ok(View view) {
        String defaultMin = "00";
        String defaultSec = "00";
        Intent intent = new Intent(LandingPage.this, MainActivity.class);
        intent.putExtra("min", defaultMin);
        intent.putExtra("sec", defaultSec);
        intent.putExtra("type", exerciseChoice);
        startActivityForResult(intent, 5464);
        //finish();
    }
}