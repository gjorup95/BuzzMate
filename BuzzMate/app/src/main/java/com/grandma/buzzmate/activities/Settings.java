package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Info;

public class Settings extends Activity {
    private EditText inRange;
    private EditText holdDown;
    private Button setInRange;
    private Button setholdDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        inRange = findViewById(R.id.editText);
        holdDown = findViewById(R.id.editText2);
        setInRange = findViewById(R.id.button14);
        setholdDown = findViewById(R.id.button15);

        inRange.setText(Info.getInstance().getInRange()+"");
        holdDown.setText(Info.getInstance().getHoldDown()+"");


    }

    public void setInRange(View view){
        Info.getInstance().setInRange(Integer.parseInt(inRange.getText().toString()));
        Toast.makeText(Settings.this, "Range set to: " + inRange.getText().toString()+ " m", Toast.LENGTH_LONG).show();
    }

    public void setHoldDown(View view) {
        Info.getInstance().setHoldDown(Integer.parseInt(holdDown.getText().toString()));
        Toast.makeText(Settings.this, "Admin Timer set to: " + (Integer.parseInt(holdDown.getText().toString()) / 10) + " seconds", Toast.LENGTH_LONG).show();
    }
}
