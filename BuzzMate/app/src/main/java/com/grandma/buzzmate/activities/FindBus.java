package com.grandma.buzzmate.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.grandma.buzzmate.modules.Bus;
import com.grandma.buzzmate.modules.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class FindBus extends Activity implements ArduinoListener {
    private Arduino arduino;
    private boolean busReady = false;
    private boolean stopsReady = false;
    private Timer timer = new Timer();
    private List<Coordinates> koordinater;
    private List<Bus> busses;
    private FirebaseDatabase database;
    private DatabaseReference stops;
    private DatabaseReference bus;
    private BuzzMateStarted buzz = new BuzzMateStarted();
    private TextView displayTextView;
    private TextView distanceTextView;
    private Spinner dropdown;
    private Button button;
    private Coordinates coord=new Coordinates();
    private double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bus);
        arduino = new Arduino(this);
        displayTextView=findViewById(R.id.textView31);
        displayTextView=findViewById(R.id.textView33);
        dropdown=findViewById(R.id.spinner2);
        button =findViewById(R.id.button25);


        koordinater = new ArrayList<>();
        busses = new ArrayList<>();


        database = FirebaseDatabase.getInstance();
        stops = database.getReference("stops");
        bus = database.getReference("busses");

        bus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                busses.clear();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    double latitude = (double) messageSnapshot.child("Latitude").getValue();
                    double longitude = (double) messageSnapshot.child("Longitude").getValue();
                    Bus bus = new Bus();
                    bus.setLatitude(latitude);
                    bus.setLongitude(longitude);
                    busses.add(bus);
                }
                Log.i("skrt",""+busses.toString());
                busReady = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        stops.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Coordinates>> genericTypeIndicator = new GenericTypeIndicator<List<Coordinates>>(){};
                List<Coordinates> CoorDesList=dataSnapshot.getValue(genericTypeIndicator);
                koordinater.clear();
                for(int i = 0;i<CoorDesList.size();i++){
                    koordinater.add(CoorDesList.get(i));
                }
                Log.i("Arraylist", koordinater.toString());
                stopsReady = true;
                createList();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void timer(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //display("latitude: " + latitude + " longitude: " + longitude);
                // task to run goes here
                findBus();
                if(coord!=null) {
                     displayDistance("" + distance);
                }
            }
        };
        long delay = 0;
        long intevalPeriod = 1 * 1000;

        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay,
                intevalPeriod);

    }

    private void findBus(){
        distance = buzz.distance(coord,koordinater.get(Integer.parseInt(dropdown.getSelectedItem().toString())));
        if(BuzzMateStarted.inRange(coord, koordinater.get(Integer.parseInt(dropdown.getSelectedItem().toString())) ,(double) 10) && distance > 8){
            arduino.send("z".getBytes());
            display("Send z");
        }
        if(BuzzMateStarted.inRange(coord, koordinater.get(Integer.parseInt(dropdown.getSelectedItem().toString())) ,(double) 7) && distance > 5){
            arduino.send("c".getBytes());
            display("Send c");
        }
        if(BuzzMateStarted.inRange(coord, koordinater.get(Integer.parseInt(dropdown.getSelectedItem().toString())) ,(double) 5) && distance > 3){
            arduino.send("v".getBytes());
            display("Send v");
        }
        if(BuzzMateStarted.inRange(coord, koordinater.get(Integer.parseInt(dropdown.getSelectedItem().toString())) ,(double) 3) && distance > 1){
            arduino.send("b".getBytes());
            display("Send b");
        }
        if(BuzzMateStarted.inRange(coord, koordinater.get(Integer.parseInt(dropdown.getSelectedItem().toString())) ,(double) 1) && distance > 0){
            arduino.send("n".getBytes());
            display("Send n");
        }
    }

    public void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayTextView.append(message+"\n");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        Toast.makeText(FindBus.this, "Arduino attached!", Toast.LENGTH_LONG).show();
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() { Toast.makeText(FindBus.this, "Arduino attached!", Toast.LENGTH_LONG).show(); }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        Toast.makeText(FindBus.this, "Received: "+new String(bytes), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onArduinoOpened() {

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

                    //displayCurrentStop("Latitude : " + stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())).getLat()+" Longitude : " + stop.get(Integer.parseInt(dropdown.getSelectedItem().toString())).getLon());
                    coord.setLat(location1.getLatitude());
                    coord.setLon(location1.getLongitude());
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

    private void createList(){
//create a list of items for the spinner.
        String[] items = new String[koordinater.size()];
        for(int i = 0;i<koordinater.size();i++){
            items[i]=""+i;
        }
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
    }

    public void start(View view){
        if(active){
            timer.cancel();
            timer.purge();
            active = false;
            button.setText("OFF");
        }
        else{
            requestLocationUpdates();
            timer();
            active = true;
            button.setText("ON");
        }
    }
    private boolean active;


    public void displayDistance(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayTextView.setText(message);
            }
        });
    }
}