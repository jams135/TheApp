package com.example.rushikesh.theapp;

import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import Navigation.URLDirections;
import android.widget.TextView;
public class TestAct extends AppCompatActivity implements NavigationFinal{

    protected String user,curr;
    Speech_Output s1;
    LatLng dest,src;
    LocationManager lm;
    Location l;
    URLDirections u;
    public TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        try {
            //Geocoder g = new Geocoder(getApplicationContext());
            user="Pune Station";
            //List<Address> l1 = g.getFromLocationName(user, 1);
            //Address d = l1.get(0);
            //dest = new LatLng(d.getLatitude(), d.getLongitude());
            curr="Hadapsar,Pune";
            //l1 = g.getFromLocationName(curr, 1);
            //d = l1.get(0);
            //src = new LatLng(d.getLatitude(), d.getLongitude());
            u=new URLDirections(this);
            u.startServices(l, user);

        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onParseComplete() {

        Toast.makeText(getApplicationContext(),u.route.steps.get(0).instruction,Toast.LENGTH_LONG).show();
        t=(TextView)findViewById(R.id.textView);
        t.setText(u.route.steps.get(0).instruction);

    }
}


