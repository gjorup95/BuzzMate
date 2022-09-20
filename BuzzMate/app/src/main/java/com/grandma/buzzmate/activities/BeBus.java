package com.grandma.buzzmate.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandma.buzzmate.modules.Coordinates;
import com.grandma.buzzmate.modules.Info;
import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Bus;
import com.grandma.buzzmate.modules.Stops;

import java.util.ArrayList;
import java.util.List;

public class BeBus extends Activity {
    private int currentLocation1;
    private TextView currentLocation;
    private TextView prevStop;
    private TextView nextStop;
    private TextView distance;
    private FirebaseDatabase database;
    private DatabaseReference busses;
    private DatabaseReference stops;
    private Spinner dropdown;
    private Spinner dropdown1;
    private Button createNewBus;
    private Button beBus;
    private LocationManager locationManager;
    private ArrayList<Bus> bus;
    private double latitude;
    private double longitude;
    private boolean enabeld = false;
    private BuzzMateStarted buzz;
    private ArrayList<Stops> koordinater = new ArrayList<>();
    private boolean busChecked = false;
    private Button set;
    private Button next;
    private Button prev;
    private int currentSetPosition;
    private LocationRequest request = new LocationRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_be_bus);
        buzz = new BuzzMateStarted();
        bus = new ArrayList<>();
        currentLocation = findViewById(R.id.textView10);
        beBus = findViewById(R.id.button11);
        prevStop = findViewById(R.id.textView16);
        nextStop = findViewById(R.id.textView17);
        distance = findViewById(R.id.textView18);
        next = findViewById(R.id.button21);
        prev = findViewById(R.id.button22);
        database = FirebaseDatabase.getInstance();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        busses = database.getReference("busses");
        stops = database.getReference("stops");
        set = findViewById(R.id.button20);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        dropdown = findViewById(R.id.spinner1);
        dropdown1 = findViewById(R.id.spinner2);

        prevStop.setText(enabeld+"");

        busses.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Bus data = dataSnapshot.getValue(Bus.class);
                data.setBusID(dataSnapshot.getKey());
                Log.i("test",data.toString());
                bus.add(data);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        busses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("testString",dataSnapshot.getValue().toString());
                    createList();
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
                koordinater.clear();
                for(int i = 0;i<CoorDesList.size();i++){
                    koordinater.add(CoorDesList.get(i));
                }
                Log.i("Arraylist", koordinater.toString());
                createStopList();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        beBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnabeld();
                if(enabeld){
                    beBus.setText("Stop BeBus");
                }
                else{
                    beBus.setText("BeBus");
                }
            }
        });

        requestLocationUpdates();
    }

    private void createList(){
        String[] items = new String[bus.size()];
        for(int i = 0;i<bus.size();i++){
            items[i]=bus.get(i).getBusID();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        nextStop.setText("virekde");
        beBus.setEnabled(true);
    }

    private void createStopList(){
        String[] items = new String[koordinater.size()];
        for(int i = 0;i<koordinater.size();i++){
            items[i]=koordinater.get(i).getTitle();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown1.setAdapter(adapter);
        set.setEnabled(true);
    }

    public void requestLocationUpdates() {
//Specify how often your app should request the device’s location//

        request.setInterval(1000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//
                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Location location1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    final double longitude = location1.getLongitude();
                    final double latitude = location1.getLatitude();
                    Coordinates cord = new Coordinates();
                    cord.setLat(latitude);
                    cord.setLon(longitude);
                    if(enabeld){
                        if (buzz.inRange(convertToCoor(latitude, longitude), convertToCoor(koordinater.get(currentLocation1).getLat(),koordinater.get(currentLocation1).getLon()),(double) Info.getInstance().getInRange())){
                            currentLocation1++;
                        }
                        busses.child(dropdown.getSelectedItem().toString()).child("Latitude").setValue(latitude);
                        busses.child(dropdown.getSelectedItem().toString()).child("Longitude").setValue(longitude);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() { currentLocation.setText(latitude + ","+ longitude);
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() { nextStop.setText(currentLocation1+"");
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() { distance.setText(buzz.distance(convertToCoor(latitude, longitude), convertToCoor(koordinater.get(currentLocation1).getLat(),koordinater.get(currentLocation1).getLon()))+"");
                            }
                        });
                    }
                }
            }, null);
        }
    }

    public void setEnabeld(){
        if(enabeld){enabeld=false;}
        else{enabeld=true;}
    }

    public Coordinates convertToCoor(double lat, double lon){
        Coordinates cord = new Coordinates();
        cord.setLat(lat);
        cord.setLon(lon);
        return cord;
    }

    public int rollOverNumbBack(int from, int to){
        int numb;
        numb = to - from;
        if(numb<0) {
            numb += koordinater.size();
        }
        return numb;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if(!enabeld) {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void buttonClick(View view){
        busses.child(dropdown.getSelectedItem().toString()).child("Latitude").setValue(koordinater.get(dropdown1.getSelectedItemPosition()).getLat());
        busses.child(dropdown.getSelectedItem().toString()).child("Longitude").setValue(koordinater.get(dropdown1.getSelectedItemPosition()).getLon());
        prev.setEnabled(true);
        next.setEnabled(true);
        currentSetPosition = dropdown1.getSelectedItemPosition();
    }

    public void nextStop(View view){
        currentSetPosition++;
        if(currentSetPosition==koordinater.size()) {
            currentSetPosition = 0;
        }
        busses.child(dropdown.getSelectedItem().toString()).child("Latitude").setValue(koordinater.get(currentSetPosition).getLat());
        busses.child(dropdown.getSelectedItem().toString()).child("Longitude").setValue(koordinater.get(currentSetPosition).getLon());
        dropdown1.setSelection(currentSetPosition);
    }

    public void prevStop(View view){
        currentSetPosition--;
        if(currentSetPosition==-1) {
            currentSetPosition = koordinater.size()-1;
        }
        busses.child(dropdown.getSelectedItem().toString()).child("Latitude").setValue(koordinater.get(currentSetPosition).getLat());
        busses.child(dropdown.getSelectedItem().toString()).child("Longitude").setValue(koordinater.get(currentSetPosition).getLon());
        dropdown1.setSelection(currentSetPosition);
    }

}
