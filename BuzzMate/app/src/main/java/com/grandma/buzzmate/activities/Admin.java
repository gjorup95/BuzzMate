package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.grandma.buzzmate.R;

public class Admin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
    }

    public void beBus(View view){
        Intent intent = new Intent(this, BeBus.class);
        startActivity(intent);
    }

    public void setStops(View view){
        Intent intent = new Intent(this, SetStop.class);
        startActivity(intent);
    }

    public void settings(View view){
        Intent intent = new Intent(this,Settings.class  );
        startActivity(intent);
    }

    public void calibrate(View view){
        Intent intent = new Intent(this,ControlCover.class  );
        startActivity(intent);
    }

    public void findBus(View view){
        Intent intent = new Intent(this,FindBus.class  );
        startActivity(intent);
    }
}
