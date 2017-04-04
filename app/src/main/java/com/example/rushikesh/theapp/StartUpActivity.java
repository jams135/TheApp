package com.example.rushikesh.theapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.RecognizerIntent;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.rushikesh.theapp.Speech_Output;

import java.util.ArrayList;

public class StartUpActivity extends AppCompatActivity {

    Speech_Output s1;
    Intent r;
    public String user;
    //private BluetoothAdapter btAdapter = null;
    //private BluetoothSocket btSocket = null;
    private static final String TAG="Bluetooth";
    protected static final int RESULT_SPEECH=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        r=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        r.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-UK");
        s1=new Speech_Output();
        //btAdapter=BluetoothAdapter.getDefaultAdapter();
        //checkBTState();

        s1.talk(getApplicationContext(),"Would you like to start the navigation app?");


        s1.t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {



            }

            @Override
            public void onDone(String utteranceId) {

                try
                {

                    startActivityForResult(r,RESULT_SPEECH);
                }catch(Exception e){}

            }

            @Override
            public void onError(String utteranceId) {

            }
        });


        //Bluetooth pairing code
        IntentFilter i=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(new BluetoothPair(),i);



    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case RESULT_SPEECH:{
                if(resultCode==RESULT_OK && null!=data)
                {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(),text.get(0),Toast.LENGTH_LONG).show();
                    user=text.get(0);
                    if(user.equalsIgnoreCase("Yes"))
                    {
                        Intent i1=new Intent(this,FinalActicity.class);

                        startActivity(i1);
                    }
                    else if(user.equalsIgnoreCase("No"))
                    {
                        this.finishAffinity();
                    }
                    else
                    {
                        s1.talk(getApplicationContext(),"Can you repeat?");
                        startActivityForResult(r,RESULT_SPEECH);
                    }
                }
                break;
            }
        }
    }
/*
    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            Log.d("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                //Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                btAdapter.enable();
                //btAdapter.disable();
            }
        }
    }
*/
    /*
    public void onPause(){
        if(s1.t1 !=null){
            s1.t1.stop();
            s1.t1.shutdown();
        }
        super.onPause();
    }*/
}
