package com.grandma.buzzmate.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Fare;
import com.grandma.buzzmate.modules.Info;

import java.util.ArrayList;
import java.util.List;

public class PreviousFare extends Activity {
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private static final int[] idArray ={R.id.button,R.id.button2,R.id.button3,R.id.button4};
    private Button[] buttons = new Button[idArray.length];
    private FirebaseDatabase database;
    private DatabaseReference fares;
    private int rejseCount;
    private BuzzMateStarted buzz;
    private boolean finish = false;

    private ArrayList<String> from;
    private ArrayList<String> to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Tidligere rejse");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        to = new ArrayList<>();
        from = new ArrayList<>();
        super.onCreate(savedInstanceState);
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        buzz = new BuzzMateStarted();


        //Db init
        database = FirebaseDatabase.getInstance();
        fares = database.getReference("fares");
        //EventListerner
        fares.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(!finish) {
                    rejseCount = (int) dataSnapshot.getChildrenCount();
                    Log.i("skr", "db updated");
                    updateButtons();
                    Log.i("dataInput", dataSnapshot.getValue().toString());
                    GenericTypeIndicator<List<Fare>> genericTypeIndicator = new GenericTypeIndicator<List<Fare>>() {
                    };
                    List<Fare> fareDesList = dataSnapshot.getValue(genericTypeIndicator);
                    int j = 0;
                    for (int i = fareDesList.size() - 4; i < fareDesList.size(); i++) {
                        buttons[j].setText("Fra: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(fareDesList.get(i).getFrom())).getTitle() + " Til: " + Info.getInstance().getStopsList().get(buzz.convertToNumb(fareDesList.get(i).getTo())).getTitle());
                        from.add(fareDesList.get(i).getFrom());
                        to.add(fareDesList.get(i).getTo());
                        j++;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        Log.i("skr", "anatal rejser: " + Integer.toString(rejseCount));
        setContentView(R.layout.activity_prev_fare);
    }

    private void updateButtons(){
        for(int i = 0; i<idArray.length;i++){
            buttons[i] = (Button)findViewById(idArray[i]);
            buttons[i].setVisibility(View.GONE);
        }
        for(int i = 0; i<idArray.length;i++){
            Log.i("skrt","i=" + i + " rejsecount:"+rejseCount);
            buttons[i] = (Button)findViewById(idArray[i]);
            buttons[i].setText("Button Number: " + i + " RejseDBCount:" +rejseCount);
            buttons[i].setVisibility(View.VISIBLE);
            if(i+2>rejseCount){
                Log.i("skrt","runnung return");
                return;
            }
        }
    }

    public void button1(View v) {
        Intent intent = new Intent(PreviousFare.this, ToFromScreen.class);
        startActivity(intent);
        Info.getInstance().setToState(to.get(0));
        Info.getInstance().setFromState(from.get(0));
        finish = true;
        finish();
    }
    public void button2(View v) {
        Intent intent = new Intent(PreviousFare.this, ToFromScreen.class);
        startActivity(intent);
        Info.getInstance().setToState(to.get(1));
        Info.getInstance().setFromState(from.get(1));
        finish = true;
        finish();
    }
    public void button3(View v) {
        Intent intent = new Intent(PreviousFare.this, ToFromScreen.class);
        startActivity(intent);
        Info.getInstance().setToState(to.get(2));
        Info.getInstance().setFromState(from.get(2));
        finish = true;
        finish();
    }
    public void button4(View v) {
        Intent intent = new Intent(PreviousFare.this, ToFromScreen.class);
        startActivity(intent);
        Info.getInstance().setToState(to.get(3));
        Info.getInstance().setFromState(from.get(3));
        finish = true;
        finish();
    }

    public void goBack(View v){
        super.onBackPressed();
        finish();
    }
}
