package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.grandma.buzzmate.R;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class ControlCover extends Activity implements ArduinoListener {
    private Arduino arduino;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
        arduino = new Arduino(this);
    }

    public void sendOneStop(View view){ arduino.send("3".getBytes()); }

    public void sendTwoStop(View view){
        arduino.send("2".getBytes());
    }

    public void sendThreeStop(View view){
        arduino.send("1".getBytes());
    }

    public void resset(View view){
        arduino.send("9".getBytes());
    }

    public void busArrived(View view){
        arduino.send("5".getBytes());
    }

    public void audi(View view){
        arduino.send("4".getBytes());
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
        Toast.makeText(ControlCover.this, "Arduino attached!", Toast.LENGTH_LONG).show();
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() { Toast.makeText(ControlCover.this, "Arduino attached!", Toast.LENGTH_LONG).show(); }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        Toast.makeText(ControlCover.this, "Received: "+new String(bytes), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onArduinoOpened() {

    }
}
