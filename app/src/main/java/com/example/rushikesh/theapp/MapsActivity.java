package com.example.rushikesh.theapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.speech.RecognizerIntent;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Navigation.URLDirections;
import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,NavigationFinal {

    private GoogleMap mMap;
    protected static final int RESULT_SPEECH=1;
    Speech_Output s1;
    protected String start;
    protected String finish;
    LocationManager lm;
    Location l;
    URLDirections u;
    public static int cnt=1;
    Location destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        s1=new Speech_Output();

        s1.talk(getApplicationContext(),"Please Tell your destination");

        s1.t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {



            }

            @Override
            public void onDone(String utteranceId) {
                Intent r=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                r.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"en-US");
                try
                {

                    startActivityForResult(r,RESULT_SPEECH);
                }catch(Exception e){}

            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);









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
                    finish=text.get(0);
                    if(finish.equalsIgnoreCase("No"))
                    {
                        //Only short distance navigation
                    }
                    else
                    {
                        try {
                            //Geocoder g = new Geocoder(getApplicationContext());

                            //List<Address> l1 = g.getFromLocationName(user, 1);
                            //Address d = l1.get(0);
                            //dest = new LatLng(d.getLatitude(), d.getLongitude());
                            l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            Toast.makeText(getApplicationContext(),finish,Toast.LENGTH_LONG).show();
                            //src=new LatLng(l.getLatitude(),l.getLongitude());

                            //List<Address>l1=g.getFromLocation(l.getLatitude(),l.getLongitude(),1);
                            //start=l1.get(0).getAddressLine(0);
                            //start=dest.toString();
                            //l1 = g.getFromLocationName(curr, 1);
                            //d = l1.get(0);
                            //src = new LatLng(d.getLatitude(), d.getLongitude());
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



    @Override
    public void onParseComplete() {


        mMap.addMarker(new MarkerOptions().position(u.route.startLocation).title("Start"));
        mMap.addMarker(new MarkerOptions().position(u.route.endLocation).title("Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(u.route.startLocation,18));

        PolylineOptions polylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.BLUE).
                width(10);

        for(int i=0;i<u.route.polyline.size();i++)
        {
            polylineOptions.add(u.route.polyline.get(i));
        }
        mMap.addPolyline(polylineOptions);
        Toast.makeText(getApplicationContext(),u.route.steps.get(0).instruction,Toast.LENGTH_LONG).show();
        s1.talk(getApplicationContext(),u.route.steps.get(0).instruction);
        cnt++;
        destination=new Location("");
        destination.setLatitude(u.route.endLocation.latitude);
        destination.setLongitude(u.route.endLocation.longitude);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        }catch (SecurityException e){e.printStackTrace();}


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        try {
            mMap.setMyLocationEnabled(true);
        }catch(SecurityException e){e.printStackTrace();}



        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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





}
