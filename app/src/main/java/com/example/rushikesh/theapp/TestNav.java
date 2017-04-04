package com.example.rushikesh.theapp;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import java.util.List;

import Navigation.URLDirections;

public class TestNav extends FragmentActivity implements OnMapReadyCallback,LocationListener,NavigationFinal {

    private GoogleMap mMap;
    protected String user,curr;
    Speech_Output s1;
    LatLng dest,src;
    LocationManager lm;
    Location l,destination;
    URLDirections u;

    public static int cnt=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_nav);
        s1=new Speech_Output();
        lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        try {
            Geocoder g = new Geocoder(getApplicationContext());
            user="Pune Station";
            //List<Address> l1 = g.getFromLocationName(user, 1);
            //Address d = l1.get(0);
            //dest = new LatLng(d.getLatitude(), d.getLongitude());
            l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            curr="Hadapsar,Pune";
            //dest=new LatLng(l.getLatitude(),l.getLongitude());
            //List<Address>l1=g.getFromLocation(l.getLatitude(),l.getLongitude(),1);
            //curr=dest.toString();
             //l1 = g.getFromLocationName(curr, 1);
             //d = l1.get(0);
            //src = new LatLng(d.getLatitude(), d.getLongitude());
            u=new URLDirections(this);
            u.startServices(l, user);
        }catch (SecurityException e){e.printStackTrace();}

        /*

        */
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(),"location changed",Toast.LENGTH_LONG).show();
        s1.talk(getApplicationContext(),"hello");
        Toast.makeText(getApplicationContext(),"Current location"+location.toString(),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),"location to match: "+u.route.steps.get(cnt).startLoc.toString(),Toast.LENGTH_LONG).show();
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
        //Toast.makeText(getApplicationContext(),u.route.steps.get(0).instruction,Toast.LENGTH_LONG).show();
        //s1.talk(getApplicationContext(),u.route.steps.get(0).instruction);
        //cnt++;
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
       // LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
