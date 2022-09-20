package com.grandma.buzzmate.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandma.buzzmate.R;
import com.grandma.buzzmate.activities.BuzzMateStarted;
import com.grandma.buzzmate.modules.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class SetStop extends Activity {
    private double longitude;
    private double latitude;
    private LocationManager locationManager;
    private TextView textView;
    private TextView busLocation;
    private FirebaseDatabase database;
    private DatabaseReference stops;
    private List<Coordinates> stop;
    private Spinner dropdown;
    private TextView distance;
    private Button setCurrent;
    private Button delete;
    private Button createNew;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_stop);
        stop = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        stops = database.getReference("stops");
        textView = findViewById(R.id.textView6);
        distance = findViewById(R.id.textView7);
        busLocation = findViewById(R.id.textView9);
        dropdown = findViewById(R.id.spinner1);
        setCurrent = findViewById(R.id.button7);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        stops.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Coordinates>> genericTypeIndicator = new GenericTypeIndicator<List<Coordinates>>(){};
                List<Coordinates> CoorDesList=dataSnapshot.getValue(genericTypeIndicator);
                stop.clear();
                for(int i = 0;i<CoorDesList.size();i++){
                    stop.add(CoorDesList.get(i));
                }
                createList();
                timer();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        setCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStop();
            }
        });

        requestLocationUpdates();

    }

    private void createList(){
//create a list of items for the spinner.
        String[] items = new String[stop.size()];
        for(int i = 0;i<stop.size();i++){
            items[i]=""+i;
        }
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
    }


    public void timer(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //display("latitude: " + latitude + " longitude: " + longitude);
                // task to run goes here
                //getLocation();
                displayCurrentStop("Latitude : " + stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())).getLat()+" Longitude : " + stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())).getLon());
                Coordinates cord = new Coordinates();
                cord.setLat(latitude);
                cord.setLon(longitude);
                displayDistance("Distance: " + BuzzMateStarted.distance(stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())),cord));
            }
        };

        Timer timer = new Timer();
        long delay = 0;
        long intevalPeriod = 1 * 1000;

        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay,
                intevalPeriod);

    }

    public void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
            }
        });
    }

    public void displayDistance(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                distance.setText(message);
            }
        });
    }
    public void displayCurrentStop(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                busLocation.setText(message);
            }
        });
    }

    public void setStop(){
        stops.child(dropdown.getSelectedItem().toString()).child("lat").setValue(latitude);
        stops.child(dropdown.getSelectedItem().toString()).child("lon").setValue(longitude);
    }

    public void createNew(){

    }

    public void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(1000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                    // Location location = locationResult.getLastLocation();

                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Location location1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    longitude = location1.getLongitude();
                    latitude = location1.getLatitude();
                    //displayCurrentStop("Latitude : " + stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())).getLat()+" Longitude : " + stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())).getLon());
                    Coordinates cord = new Coordinates();
                    cord.setLat(latitude);
                    cord.setLon(longitude);
                    display("Latitude: " + latitude + " Longitude: " + longitude + " counter:");
                    //displayDistance("Distance: " + BuzzMateStarted.distance(stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())),cord));

                    /* if (location != null) {

//Save the location data to the database//

                        ref.setValue(location);
                    }
                    */
                }
            }, null);
        }
    }


}
