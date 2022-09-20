package com.grandma.buzzmate.modules;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Info {
    private static Info mInstance = null;
    private FirebaseDatabase database;
    private DatabaseReference settings;
    public Boolean ready = false;
    private String fromState = "";
    private String toState = "";
    private int inRange = 0;
    private int holdDown = 0;
    private int tutorial = 0;
    private DatabaseReference busses;
    private DatabaseReference stops;
    private DatabaseReference fares;
    private List<Stops> stopsList = new ArrayList<>();
    private boolean faresReady = false;
    private boolean settingsReady = false;
    private boolean stopsReady = false;

    protected Info (){}

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String buttonState) {
        this.toState = buttonState;
    }

    public static synchronized Info getInstance() {
        if(null == mInstance){
            mInstance = new Info();
        }
        return mInstance;
    }

    public void setup(){
        database = FirebaseDatabase.getInstance();
        settings = database.getReference("settings");
        busses = database.getReference("busses");
        stops = database.getReference("stops");
        fares = database.getReference("fares");
        settings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Info.getInstance().setHoldDown(dataSnapshot.child("holdDown").getValue(Integer.class));
                Info.getInstance().setInRange(dataSnapshot.child("inRange").getValue(Integer.class));
                Info.getInstance().setTutorial(dataSnapshot.child("tutorial").getValue(Integer.class));
                settingsReady=true;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        stops.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Stops>> genericTypeIndicator = new GenericTypeIndicator<List<Stops>>(){};
                List<Stops> CoorDesList=dataSnapshot.getValue(genericTypeIndicator);
                stopsList.clear();
                for(int i = 0;i<CoorDesList.size();i++){
                    stopsList.add(CoorDesList.get(i));
                }
                stopsReady=true;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        fares.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                faresReady=true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setReady(Boolean ready){
        this.ready = ready;
    }

    public int getInRange() {
        return inRange;
    }

    public void setInRange(int inRange) {
        this.inRange = inRange;
        settings.child("inRange").setValue(inRange);
    }

    public int getHoldDown() {
        return holdDown;
    }

    public void setHoldDown(int holdDown) {
        this.holdDown = holdDown;
        settings.child("holdDown").setValue(holdDown);

    }

    public void setBusStop(String busID,int stopNumb){
        busses.child(busID).child("nextStop").setValue(stopNumb);
    }

    public List<Stops> getStopsList() {
        return stopsList;
    }

    public boolean isReady(){
        if(settingsReady && stopsReady && faresReady){
            Log.i("Ready","aker");
            return true;
        }
        return false;
    }

    public void resetFare(){
        fromState = "";
        toState = "";
    }

    public int getTutorial() {
        return tutorial;
    }

    public void setTutorial(int tutorial) {
        this.tutorial = tutorial;
        settings.child("tutorial").setValue(tutorial);
    }


}