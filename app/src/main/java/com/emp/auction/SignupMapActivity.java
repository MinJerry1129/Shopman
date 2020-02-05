package com.emp.auction;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static java.util.Locale.getDefault;

public class SignupMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap GMap;
    Geocoder geocoder;
    List<Address> addresses;
    TextView _shopaddress;
    TextView _shoplocation;
    String sel_location = "";
    Button set_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(this, getDefault());
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_signup_map);
        _shopaddress = (TextView) findViewById(R.id.shop_address);
        _shoplocation = (TextView) findViewById(R.id.shop_location);
        set_location = (Button) findViewById(R.id.set_location);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_location();
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
//
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String shop_address = "";
                String shop_locaton = "";
                String shop_location = "";
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    shop_address = address;
                    shop_locaton = latLng.latitude + "," + latLng.longitude;
                    shop_location = "Latitude:"+  latLng.latitude + "\n Longtitude:" + latLng.longitude;
                    sel_location = "yes";

                    Common.getInstance().setShopaddress(shop_address);
                    Common.getInstance().setShoplocation(shop_locaton);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                _shoplocation.setText(shop_location);
                _shopaddress.setText(shop_address);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(shop_address);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.addMarker(markerOptions);

            }
        });

    }
    public void set_location() {
        if(sel_location == "yes"){
            finish();
        }else{
            Toast toast = Toast.makeText(this, getResources().getString(R.string.toast_checklocationset), Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}