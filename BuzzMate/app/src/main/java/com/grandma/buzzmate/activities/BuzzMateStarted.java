package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.grandma.buzzmate.modules.Info;
import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Bus;
import com.grandma.buzzmate.modules.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class BuzzMateStarted extends Activity implements ArduinoListener {
    private Boolean log = false;
    private Arduino arduino;
    private TextView textView;
    private TextView fromTextView;
    private TextView toTextView;
    private TextView busTextView;
    private boolean busChecked = false;
    private int currentLocation;
    private boolean onBus;
    private int stopsTill;
    private boolean busReady = false;
    private boolean stopsReady = false;
    private List<Coordinates> koordinater;
    private List<Bus> busses;
    private FirebaseDatabase database;
    private DatabaseReference stops;
    private DatabaseReference bus;
    private int from;
    private int to;
    private double longitude;
    private double latitude;
    private LocationManager locationManager;
    private Timer timer = new Timer();
    private ConstraintLayout debugger;
    private int counter = 0;
    private Handler mHandler = new Handler();
    private boolean checkedStops;
    private EditText editText;
    private boolean first = true;
    private boolean audiReady = false;
    private double range;
    private boolean findBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("BuzzMate er sat igang");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buzz_mate_started);
        arduino = new Arduino(this);
        koordinater = new ArrayList<>();
        busses = new ArrayList<>();
        textView = findViewById(R.id.textView);
        debugger = findViewById(R.id.container1);
        currentLocation = 0;
        fromTextView = findViewById(R.id.textView23);
        toTextView = findViewById(R.id.textView24);
        busTextView = findViewById(R.id.textView25);
        editText = findViewById(R.id.editText3);

        fromTextView.setText("Fra: " + Info.getInstance().getStopsList().get(convertToNumb(Info.getInstance().getFromState())).getTitle());
        toTextView.setText("Til: " + Info.getInstance().getStopsList().get(convertToNumb(Info.getInstance().getToState())).getTitle());
        //GPS
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Log.i("test af","From char: "+ Info.getInstance().getFromState() + " From converted: " + convertToNumb(Info.getInstance().getFromState()));
        display("Tilkobl en Arduino, Husk at aktivere OTG i telefonens indstillinger");
        from = convertToNumb(Info.getInstance().getFromState());
        to = convertToNumb(Info.getInstance().getToState());
        stopsTill = from;
        onBus=false;

        database = FirebaseDatabase.getInstance();
        stops = database.getReference("stops");
        bus = database.getReference("busses");
        Log.i("runMate","Fra: " + from + " Til: " + to);


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
                countFourBackFrom();
                stopsReady = true;
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String input = editText.getText().toString();
                    input = input.toLowerCase();
                    input = input.trim();
                    if(!input.equals("")) {
                        if(input.length()==1){
                            arduino.send(input.getBytes());
                        }
                        if(input.equals("log")){
                            if(log){
                                display("Log turned off");
                                log=false;
                            }
                            else{
                                log=true;
                                display("log turned on");
                            }
                        }
                        if(input.equals("clear")){
                            editText.setText("");
                            display("Cleared");
                        }
                    }

                    editText.setText("");
                }
            return true;
            }
        });

        timer();

        Button bmybutton = (Button) findViewById(R.id.button19);

        bmybutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionevent) {
                int action = motionevent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    Log.i("repeatBtn", "MotionEvent.ACTION_DOWN");
                    mHandler.removeCallbacks(mUpdateTaskup);
                    mHandler.postAtTime(mUpdateTaskup,
                            SystemClock.uptimeMillis() + 50);
                } else if (action == MotionEvent.ACTION_UP) {
                    Log.i("repeatBtn", "MotionEvent.ACTION_UP");
                    mHandler.removeCallbacks(mUpdateTaskup);
                }//end else
                return false;
            } //end onTouch
        }); //end b my button

        arduino.send("4".getBytes());
    }

    public static double distance(Coordinates start,Coordinates slut) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(slut.getLat() - start.getLat());
        double lonDistance = Math.toRadians(slut.getLon() - start.getLon());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(start.getLat())) * Math.cos(Math.toRadians(slut.getLat()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public static boolean inRange(Coordinates start,Coordinates slut,Double range){
        Log.i("runMate","distance: " + distance(start, slut));
        if(distance(start, slut)<range){
            Log.i("runMate", "InRange");
            return true;
        }
        return false;
    }

    public void countFourBackFrom(){
        for(int i = 0;i<4;i++){
            if(stopsTill==0){
                stopsTill=koordinater.size();
                Log.i("stopsTill","reset to:" +koordinater.size());
            }
            stopsTill-=1;
        }
    }

    public void timer(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //display("latitude: " + latitude + " longitude: " + longitude);
                // task to run goes here
                audi();
                if(stopsReady && busReady && !first) {
                    currentBusLocation(busses.get(0));
                    onMate();
                    offMate();
                    if(findBus){
                        findBus();
                    }
                }
                //getLocation();
            }
        };
        long delay = 0;
        long intevalPeriod = 1 * 1000;

        // schedules the task to be run in an interval
        timer.scheduleAtFixedRate(task, delay,
                intevalPeriod);

    }

    public void onMate(){
            if (stopsReady && busReady && !onBus) {
                Log.i("made it","onMate" + Info.getInstance().getStopsList().get(rollOverNumbBack(stopsTill,from)).getTitle());
                if(log){display("OnMate: distance " + distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()), koordinater.get(rollOverNumbBack(stopsTill,from))) + " Stops left: " + stopsTill + " Next Stop: " + rollOverNumbBack(stopsTill,from));}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { busTextView.setText("Bus er på vej til: \n" + Info.getInstance().getStopsList().get(rollOverNumbBack(stopsTill,from)).getTitle());
                    }
                });
                Log.i("distance", "" + distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()), koordinater.get(rollOverNumbBack(stopsTill,from))) + " Stops left: " + stopsTill + " Next Stop: " + rollOverNumbBack(stopsTill,from));
                if (inRange(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()), koordinater.get(rollOverNumbBack(stopsTill,from)),(double)Info.getInstance().getInRange())) {
                    sendData();
                    if (stopsTill == 0) {
                        Log.i("runMate", "Bus Arrived");
                        onBus = true;
                    }
                    stopsTill--;
                    busReady=false;
                }
            }
    }



    public void offMate(){
        if (stopsReady && busReady && onBus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() { busTextView.setText("" + Info.getInstance().getStopsList().get(rollOverNumbBack(stopsTill,to)).getTitle());
                }
            });
            if(!checkedStops){
                stopsTill = rollOverNumbBack(from,to)-1;
                checkedStops = true;
                Log.i("StopsTill",stopsTill+"");
                arduino.send("9".getBytes());
            }
            Log.i("made it","offMate");
            if(log){display("OnMate: distance " + distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()), koordinater.get(rollOverNumbBack(stopsTill,from))) + " Stops left: " + stopsTill + " Next Stop: " + rollOverNumbBack(stopsTill,from));}
            Log.i("distance", "" + distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()), koordinater.get(rollOverNumbBack(stopsTill,to))) + " Stops left: " + stopsTill + " Next Stop: " + rollOverNumbBack(stopsTill,from));
            if (inRange(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()), koordinater.get(rollOverNumbBack(stopsTill,to)),(double)Info.getInstance().getInRange())) {
                sendData();
                if (stopsTill == 0) {
                    Log.i("made it","here3");
                    Log.i("runMate", "Bus Arrived");
                }
                stopsTill--;
                busReady=false;
            }
        }
    }



    public List<Coordinates> getKoordinater() {
        return koordinater;
    }

    public void sendData(){
        switch (stopsTill){
            case 3: Log.i("sendData","3 stops away");
                    display("3 Stops away");
                    arduino.send("3".getBytes());
                    break;
            case 2: Log.i("sendData","2 stops away");
                    arduino.send("2".getBytes());
                    display("2 stops away");
                    break;
            case 1: Log.i("sendData","1 stops away");
                    display("1 stops away");
                    arduino.send("1".getBytes());
                    break;
            case 0: Log.i("sendData","GET ON NOW BITCH");
                    display("Bus is here");
                    arduino.send("5".getBytes());
                    findBus=true;
                    if(onBus){onBus=false;}
                    else{onBus=true;}
                    break;
        }
    }

    public void findBus(){
        if(inRange(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from),(double) 10) && distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from))>8){
            arduino.send("z".getBytes());
            display("Send z");
        }
        if(inRange(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from),(double) 5) && distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from))>4){
            arduino.send("c".getBytes());
            display("Send c");
        }
        if(inRange(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from),(double) 3) && distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from))>2){
            arduino.send("b".getBytes());
            display("Send b");
        }
        if(inRange(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from),(double) 2) && distance(convertToCoor(busses.get(0).getLatitude(), busses.get(0).getLongitude()),koordinater.get(from))>0){
            arduino.send("n".getBytes());
            display("Send n");
            findBus=false;
        }
    }

    public int convertToNumb(String s){
        s = s.toLowerCase();
        String t = "";
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if (!t.isEmpty()) {
                t += " ";
            }
            int n = (int)ch - (int)'a';
            t += String.valueOf(n);
        }
        return Integer.parseInt(t);
    }

    public Coordinates convertToCoor(double lat, double lon){
        Coordinates cord = new Coordinates();
        cord.setLat(lat);
        cord.setLon(lon);
        return cord;
    }

    public void currentBusLocation(Bus bus) {
        if (!busChecked) {
            /**
            Coordinates cord = convertToCoor(bus.getLatitude(), bus.getLongitude());
            double shortes = 1000;
            int stopNumb = 0;
            for (int i = 0; i < koordinater.size(); i++) {
                if (distance(cord, koordinater.get(i)) < shortes) {
                    shortes = distance(cord, koordinater.get(i));
                    stopNumb = i;
                }
            }
            int prevStopNumb = stopNumb - 1;
            int nextStopNumb = stopNumb + 1;

            if (prevStopNumb == koordinater.size()) {
                prevStopNumb = 0;
            }
            if (prevStopNumb < 0) {
                prevStopNumb = koordinater.size() - 1;
            }

            if (nextStopNumb == koordinater.size()) {
                nextStopNumb = 0;
            }
            if (nextStopNumb < 0) {
                nextStopNumb = koordinater.size() - 1;
            }

            Log.i("made", "prev: " + prevStopNumb + " next: " + nextStopNumb + " arraysize: " + koordinater.size());
            double distPrev = distance(cord, koordinater.get(prevStopNumb));
            double distNext = distance(cord, koordinater.get(nextStopNumb));
            double total = distPrev + distNext;
            distPrev = (distPrev / total) * 100;
            distNext = (distNext / total) * 100;

            if (distPrev < distNext) {
                currentLocation = stopNumb;
            } else {
                currentLocation = stopNumb + 1;
            }
            /**
            int j = 0;
            int i = from;
            while (i != currentLocation) {
                Log.i("made", "ithere");
                j++;
                if (i == 0) {
                    i = koordinater.size();
                }
                i--;
            }
            if(j>4){return;}
            stopsTill = 3-j;
             */
            stopsTill = rollOverNumbBack(currentLocation,from);
            Log.i("currentBusLocation", "StopsTill: " + stopsTill + " CurrentLocation: " + currentLocation);
            sendData();
            busChecked = true;
        }
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
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        display("Arduino attached!");
        arduino.open(device);
        arduino.send("4".getBytes());
        display("4");
    }

    @Override
    public void onArduinoDetached() {
        display("Arduino detached");
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        display("Received: "+new String(bytes));
        if(new String(bytes).equals("9")){
            audiReady = true;
        }
    }

    @Override
    public void onArduinoOpened() {

    }

    public void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(message+"\n");
            }
        });
    }

    void getLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        else {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                display("Latitude: " + latitude);
                display("Longitude: " + longitude);
            } else {
                display("Unable to find correct location.");

            }
        }
    }

    public void reset(View view){
        arduino.send("9".getBytes());
    }

    public void goBack(View view){
        if(counter> Info.getInstance().getHoldDown()){
            debugger.setVisibility(View.VISIBLE);
            counter = 0;
        }
        else {
            timer.cancel();
            timer.purge();
            finish();
            super.onBackPressed();
        }
    }


    private Runnable mUpdateTaskup = new Runnable() {
        public void run() {
            counter++;
            if(counter> Info.getInstance().getHoldDown()){
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }else{
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }
            Log.i("repeatBtn", "repeat click" + counter);
            mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
        }//end run
    };// end runnable

    public void hide(View view){
        debugger.setVisibility(View.INVISIBLE);
    }

    private void audi(){
        if(!audiReady) {
            arduino.send("9".getBytes());
        }
        if(busReady && stopsReady && first && audiReady){
            display("made it in audi");
            arduino.send("4".getBytes());
            if(audiReady) {
                display("made it in audiReady");
                first = false;
            }
        }
    }
}

