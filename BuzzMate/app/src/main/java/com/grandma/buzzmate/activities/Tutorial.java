package com.grandma.buzzmate.activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.grandma.buzzmate.R;
import com.grandma.buzzmate.modules.Info;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class Tutorial extends Activity implements ArduinoListener {
    private int stage = 0;
    private TextView mainTextView;
    private Button prevButton;
    private Button nextButton;
    private Button playAgain;
    private TextView slideTextView;
    private Arduino arduino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        mainTextView = findViewById(R.id.textView29);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        playAgain = findViewById(R.id.play_again_button);
        slideTextView = findViewById(R.id.textView30);
        arduino = new Arduino(this);
        slideTextView.setText("Side: " + (stage+1) + " af 5" );
    }

    public void nextStage(View view){
        stage++;
        chooseState();
    }

    public void prevStage(View view){
        stage--;
        chooseState();
    }

    private void chooseState(){
        switch (stage){
            case 0:
                mainTextView.setText(R.string.stage_null_text);
                prevButton.setVisibility(View.GONE);
                playAgain.setVisibility(View.GONE);
                break;
            case 1:
                mainTextView.setText(R.string.stage_one_text);
                sendData("3");
                prevButton.setVisibility(View.VISIBLE);
                playAgain.setVisibility(View.VISIBLE);
                break;
            case 2:
                mainTextView.setText(R.string.stage_two_text);
                sendData("2");
                break;
            case 3:
                mainTextView.setText(R.string.stage_three_text);
                nextButton.setText("Næste stadie");
                sendData("1");
                break;
            case 4:
                mainTextView.setText(R.string.stage_four_text);
                nextButton.setText("Afslut guide");
                sendData("5");
                break;
            case 5:
                sendData("9");
                Info.getInstance().setTutorial(1);
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        slideTextView.setText("Side: " + (stage+1) + " af 5" );
    }

    public void sendData(String data){
        arduino.send("9".getBytes());
        arduino.send(data.getBytes());
    }

    public void playAgain(View view){
        arduino.send("9".getBytes());
        switch (stage){
            case 1:
                sendData("3");
                break;
            case 2:
                sendData("2");
                break;
            case 3:
                sendData("1");
                break;
            case 4:
                sendData("5");
                break;
        }
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
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
    }

    @Override
    public void onArduinoOpened() {

    }
}
