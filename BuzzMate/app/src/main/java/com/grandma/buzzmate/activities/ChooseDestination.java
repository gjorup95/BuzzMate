package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Info;

public class ChooseDestination extends Activity {
    private Button fromA_button;
    private Button fromB_button;
    private Button fromC_button;
    private Button fromD_button;
    private Button from_button;
    private Button to_button;
    private int count;
    private String to;
    private String from;
    private Boolean ready = false;
    private DatabaseReference ref;
    private DatabaseReference ref1;

    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Info.getInstance().ready!=true){setTitle("Vælg afrejsested");}
        else{setTitle("Vælg Destination");}
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_destination);
        fromA_button = (Button) findViewById(R.id.button1);
        fromB_button = (Button) findViewById(R.id.button2);
        fromC_button = (Button) findViewById(R.id.button3);
        fromD_button = (Button) findViewById(R.id.button4);
        fromA_button.setText(Info.getInstance().getStopsList().get(0).getTitle());
        fromB_button.setText(Info.getInstance().getStopsList().get(1).getTitle());
        fromC_button.setText(Info.getInstance().getStopsList().get(2).getTitle());
        fromD_button.setText(Info.getInstance().getStopsList().get(3).getTitle());

        FirebaseApp.initializeApp(this);
        ref = database.getInstance().getReference();

        fromA_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(Info.getInstance().ready==true && !Info.getInstance().getFromState().equals("A")) {
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setToState("A");
                    finish();
                }
                if(Info.getInstance().ready==false){
                    Info.getInstance().setReady(true);
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setFromState("A");
                    finish();
                }
            }
        });
        fromB_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(Info.getInstance().ready==true && !Info.getInstance().getFromState().equals("B")) {
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setToState("B");
                    finish();
                }
                if(Info.getInstance().ready==false){
                    Info.getInstance().setReady(true);
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setFromState("B");
                    finish();
                }
            }
        });
        fromC_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(Info.getInstance().ready==true && !Info.getInstance().getFromState().equals("C")) {
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setToState("C");
                    finish();
                }
                if(Info.getInstance().ready==false){
                    Info.getInstance().setReady(true);
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setFromState("C");
                    finish();
                }
            }
        });
        fromD_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(Info.getInstance().ready==true && !Info.getInstance().getFromState().equals("D")) {
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setToState("D");
                    finish();
                }
                if(Info.getInstance().ready==false){
                    Info.getInstance().setReady(true);
                    Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
                    startActivity(intent);
                    Info.getInstance().setFromState("D");
                    finish();
                }
            }
        });
    }

    public void goBack(View view){
        Intent intent = new Intent(ChooseDestination.this, ToFromScreen.class);
        startActivity(intent);
        finish();
    }
}
