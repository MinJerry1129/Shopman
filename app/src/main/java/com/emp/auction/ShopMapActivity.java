package com.emp.auction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.getDefault;

public class ShopMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap GMap;
    Geocoder geocoder;
    List<Address> addresses;
    TextView _shopaddress;
    private ArrayList<Shopmain> mShops;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(this, getDefault());
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_shop_map);
        _shopaddress = (TextView) findViewById(R.id.shop_address);

        mShops = Common.getInstance().getmShops();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
//
        Marker mMaker;
        GMap = googleMap;
        for(Shopmain shop : mShops){
            String pos_location = shop.getmLocation();
            String[] separated = pos_location.split(",");
            String pos_latitude= separated[0];
            String pos_longtitude= separated[1];
            LatLng position = new LatLng(Double.parseDouble(pos_latitude),Double.parseDouble(pos_longtitude));
            mMaker = GMap.addMarker(new MarkerOptions().position(position).title(shop.getmName()).snippet(shop.getmId()));
            mMaker.setTag(0);
        }
        GMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String shop_id=marker.getSnippet();
        Common.getInstance().setShopid(shop_id);
        Intent intent = new Intent(getApplicationContext(), OneShopActivity.class);
        startActivity(intent);
        return false;
    }
}