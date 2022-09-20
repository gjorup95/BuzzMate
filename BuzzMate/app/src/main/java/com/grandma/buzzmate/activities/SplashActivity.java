package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Info;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Info.getInstance().setup();
        timer();
        if(!Info.getInstance().isReady()) {
            Toast.makeText(SplashActivity.this, "Indlæser", Toast.LENGTH_LONG).show();
        }

    }

    public void timer(){
         final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(Info.getInstance().isReady()) {
                    if(Info.getInstance().getTutorial()==1) {
                        loadHome();
                        finish();
                        timer.cancel();
                        timer.purge();
                    }
                    else{
                        loadTutorial();
                        finish();
                        timer.cancel();
                        timer.purge();
                    }
                }
            }
        };


        long delay = 0;
        long intevalPeriod = 1 * 1000;


        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay,
                intevalPeriod);
    }

    private void loadHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadTutorial(){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
        finish();
    }
}
