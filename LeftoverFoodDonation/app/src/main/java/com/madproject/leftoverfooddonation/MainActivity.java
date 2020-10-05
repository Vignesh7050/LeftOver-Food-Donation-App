package com.madproject.leftoverfooddonation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer timer;
   static {
       FirebaseDatabase.getInstance().setPersistenceEnabled(false);
     }
    @Override  // This indicates that the following method is overriding the method of its superclass
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//R is a class and layout is a inner class

        timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i= new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        },2000);
    }
}
