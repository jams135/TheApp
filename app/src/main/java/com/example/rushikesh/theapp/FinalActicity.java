package com.example.rushikesh.theapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import Navigation.URLDirections;

public class FinalActicity extends AppCompatActivity implements NavigationFinal,LocationListener {

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static final String TAG="Bluetooth";
    Handler h,h2;
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your server's MAC address
    private static String address = "00:15:83:35:8A:C9";
    //private static String address = "B4:E1:C4:B7:ED:2F";
    private InputStream inStream;
    protected static final int RESULT_SPEECH=1;
    Speech_Output s1;
    protected String start;
    protected String finish;
    LocationManager lm;
    Location l;
    URLDirections u;
    public static int cnt=1;
    Location destination;
    private TextView myt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_acticity);
        s1=new Speech_Output();

        btAdapter=BluetoothAdapter.getDefaultAdapter();
        checkBTState();


        lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);






    }

    public void navClicked(View v)
    {

        Intent i=new Intent(this,MapsActivity.class);
        startActivity(i);

        /*

        s1.talk(getApplicationContext(),"Please Tell your destination");



        s1.t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {



            }

            @Override
            public void onDone(String utteranceId) {
                Intent r=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                r.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-UK");
                try
                {

                    startActivityForResult(r,RESULT_SPEECH);


                }catch(Exception e){}

            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    */

    }


    public void offClicked(View v)
    {
        this.finishAffinity();
    }


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
                    finish=text.get(0);
                    if(finish.equalsIgnoreCase("No"))
                    {

                        //shortNav();
                        //Only short distance navigation
                    }
                    else
                    {
                        try {


                            if(!isNetworkAvailable())
                            {
                                s1.talk(getApplicationContext(),"You are not connected to the internet");
                                return;
                            }
                            else if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                            {
                                s1.talk(getApplicationContext(),"Location not enabled,please enable location");
                                return;
                            }

                            l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Toast.makeText(getApplicationContext(),finish,Toast.LENGTH_LONG).show();

                            u=new URLDirections(this);
                            u.startServices(l, finish);
                            //u.startServices(src,dest);


                        }catch (SecurityException e){e.printStackTrace();}
                        //catch(IOException e){e.printStackTrace();}
                    }
                }
                break;
            }
        }
    }


    public void onParseComplete() {



        destination=new Location("");
        destination.setLatitude(u.route.endLocation.latitude);
        destination.setLongitude(u.route.endLocation.longitude);
        Toast.makeText(getApplicationContext(),"Route found",Toast.LENGTH_LONG).show();
        //shortNav();

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }catch (SecurityException e){e.printStackTrace();}


    }


    @Override
    public void onLocationChanged(Location location) {

        Location temp_loc=new Location("");
        temp_loc.setLatitude(u.route.steps.get(cnt).startLoc.latitude);
        temp_loc.setLongitude(u.route.steps.get(cnt).startLoc.longitude);
        if(location.distanceTo(temp_loc)<=5)
        {
            s1.talk(getApplicationContext(),u.route.steps.get(cnt).instruction);
            Toast.makeText(getApplicationContext(),u.route.steps.get(cnt).instruction,Toast.LENGTH_LONG).show();
            cnt++;

        }
        else if(location.distanceTo(destination)<=5)
        {
            s1.talk(getApplicationContext(),"Destination Reached");
        }

        return;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*
    @Override
    protected void onResume() {
        super.onResume();

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        btAdapter.startDiscovery();
        Toast.makeText(getApplicationContext(),"Device found: "+device.getName(),Toast.LENGTH_LONG).show();
        int pin=1234;
        byte[] pinbytes;



        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            pinbytes=(""+pin).getBytes("UTF-8");
            //device.setPin(pinbytes);
            //device.setPairingConfirmation(true);
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
            Toast.makeText(getApplicationContext(),"Connected to :"+device.getName(),Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.d("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        try {
            outStream = btSocket.getOutputStream();
            inStream=btSocket.getInputStream();
        } catch (IOException e) {
            //errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

    }
    */
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    public void shortNav()
    {
        Runnable r=new Runnable() {
            @Override
            public void run() {
                byte buffer[]= new byte[1024];
                int bytes;

                // while(true) {
                try {
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bytes = inStream.read(buffer);            //read bytes from input buffer
                    String yo = new String(buffer, 0, bytes);

                    bundle.putString("Message", yo);
                    msg.setData(bundle);
                    // send message to the handler
                    handler.sendMessage(msg);
                    handler.postDelayed(this,1000);
                } catch (Exception e) {

                }
                // }//while
            }//run
        };

        Thread jamThread=new Thread(r);
        jamThread.start();
    }
*/
    public void redirect(View v)
    {
        Intent i=new Intent(this,ShortNavigation.class);
        startActivity(i);
    }

    /*
    @Override
    public void onPause() {     //USE this for closing bluetooth on pause, it calls errorExit which calls finish()
        super.onPause();

        //Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }
    private void errorExit(String title, String message){
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
    }
    */

    public String distProcess(String message)
    {
        int steps;
        String instruction=" ";
        message=message.replaceAll("[^\\d.]","");
        if(message.length()!=0) {
            double dist = Double.parseDouble(message);
            if (!Double.isNaN(dist)) {
                steps = (int) dist / 30;
                if(dist==0)
                {
                    return " ";
                }
                else {

                    if (steps != 0) {
                        instruction = "Obstacle Detected " + steps + " Steps away";
                    } else {
                        instruction = "Obstacle just ahead";
                    }
                }
            }
        }
        return instruction;

    }

}
