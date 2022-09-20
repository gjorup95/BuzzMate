package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.os.Vibrator;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grandma.buzzmate.modules.Info;
import com.grandma.buzzmate.R;

public class HomeActivity extends Activity {
    private FirebaseDatabase database;
    private DatabaseReference settings;
    private Handler mHandler = new Handler();

    private Runnable mUpdateTaskup = new Runnable() {
        public void run() {
            counter++;
            if(counter> Info.getInstance().getHoldDown()){
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }
            Log.i("repeatBtn", "repeat click" + counter);
            mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
        }//end run
    };// end runnable

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Hjemmeskærm");
        database = FirebaseDatabase.getInstance();
        settings = database.getReference("settings");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        Button bmybutton = (Button) findViewById(R.id.button3);

        bmybutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionevent) {
                int action = motionevent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    Log.i("repeatBtn", "MotionEvent.ACTION_DOWN");
                    mHandler.removeCallbacks(mUpdateTaskup);
                    mHandler.postAtTime(mUpdateTaskup,
                            SystemClock.uptimeMillis() + 50);
                } else if (action == MotionEvent.ACTION_UP) {
                    Log.i("repeatBtn", "MotionEvent.ACTION_UP");
                    mHandler.removeCallbacks(mUpdateTaskup);
                }//end else
                return false;
            } //end onTouch
        }); //end b my button
    }
    public void tidligereRejse(View view){
        if(counter>Info.getInstance().getHoldDown()){
            counter = 0;
            Intent intent = new Intent(this, Admin.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, PreviousFare.class);
            startActivity(intent);
        }
    }
    public void nyRejse(View view){
        Intent intent = new Intent(this, ToFromScreen.class);
        startActivity(intent);
        Info.getInstance().resetFare();
    }

}
