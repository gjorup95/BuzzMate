package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Info;

public class ToFromScreen extends Activity {

    private Button to_button;
    private Button fra_button;
    private Button send;
    private Button goBack;
    private int count;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("fares");
    private BuzzMateStarted buzz = new BuzzMateStarted();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Info.getInstance().getToState()=="" && Info.getInstance().getFromState()=="") {
            setTitle("Vælg start og slutpunkt");
        }
        if(Info.getInstance().getToState()!="") {
            setTitle("Fra: " + " Til: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(Info.getInstance().getToState())).getTitle());
        }
        if(Info.getInstance().getFromState()!="") {
            setTitle("Fra: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(Info.getInstance().getFromState())).getTitle() + " Til:");
        }
        if(Info.getInstance().getToState()!="" && Info.getInstance().getFromState()!=""){
            setTitle("Fra: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(Info.getInstance().getFromState())).getTitle() + " Til: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(Info.getInstance().getToState())).getTitle());
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_from_screen);
        FirebaseApp.initializeApp(this);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        fra_button = (Button) findViewById(R.id.fra);
        to_button = (Button) findViewById(R.id.til);
        send = (Button) findViewById(R.id.send);
        goBack = findViewById(R.id.button17);
        if(Info.getInstance().getToState()!="" && Info.getInstance().getFromState()!=""){
            send.setBackgroundColor(Color.parseColor("#f1d2c4"));
        }
        if(Info.getInstance().getFromState()!="") {
            fra_button.setText("Fra: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(Info.getInstance().getFromState())).getTitle());
        }
        if(Info.getInstance().getToState()!=""){
            to_button.setText("Til: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(Info.getInstance().getToState())).getTitle());
        }
        fra_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info.getInstance().setReady(false);
                Intent intent = new Intent(ToFromScreen.this, ChooseDestination.class);
                startActivity(intent);
                //finish();
            }
        });
        to_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Info.getInstance().setReady(true);
                Intent intent = new Intent(ToFromScreen.this, ChooseDestination.class);
                startActivity(intent);
                //finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Info.getInstance().getToState()!="" && Info.getInstance().getFromState()!="") {
                    Intent intent = new Intent(ToFromScreen.this, BuzzMateStarted.class);
                    startActivity(intent);
                    Info.getInstance().getFromState();
                    myRef.child(Integer.toString(count)).child("to").setValue(Info.getInstance().getToState());
                    myRef.child(Integer.toString(count)).child("from").setValue(Info.getInstance().getFromState());
                }
                else {
                    Toast.makeText(ToFromScreen.this, "Vælgt til og fra før rejsen kan begynde", Toast.LENGTH_LONG).show();
                }
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToFromScreen.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}