package com.emp.auction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class OneShopActivity extends AppCompatActivity {
    ArrayList<AllProduct> mProduct = new ArrayList<>();
    private GridView gridView;
    ImageView _shopimage;
    ImageView _shopsetfav;
    ImageView _shoplocation;
    TextView _shopname;
    TextView _shopmobile;
    TextView _shopemail;
    TextView _shopaddress;
    String shop_name;
    String shop_mobile;
    String shop_email;
    String shop_address;
    String shop_image;
    String shop_fav;
    String shop_location;
    LocationManager locationmanager;
    LatLng my_location;
    String my_location_string;
    private static  final int REQUEST_LOCATION=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_shop);
        _shopimage = (ImageView) findViewById(R.id.shopimage);
        _shopsetfav = (ImageView) findViewById(R.id.shopsetfav);
        _shoplocation = (ImageView) findViewById(R.id.shoplocation);
        _shopname = (TextView) findViewById(R.id.shopname);
        _shopmobile = (TextView) findViewById(R.id.shoptel);
        _shopemail = (TextView) findViewById(R.id.shopemail);
        _shopaddress = (TextView) findViewById(R.id.shopaddress);
        gridView = findViewById(R.id.list_product);
        _shoplocation.setVisibility(ImageView.GONE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shop_id=Common.getInstance().getmAllProducsts().get(position).getmShopID();
                String product_id=Common.getInstance().getmAllProducsts().get(position).getmId();
                Common.getInstance().setShopid(shop_id);
                Common.getInstance().setProductid(product_id);
                Intent intent = new Intent(getApplicationContext(), OneProductActivity.class);
                startActivity(intent);
                Log.d("shop_id: ", shop_id);
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        _shopsetfav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setfavorite();

            }
        });
        _shoplocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showMap();

            }
        });

        locationmanager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {

            Location LocationGps = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            my_location = new LatLng(LocationGps.getLatitude(), LocationGps.getLongitude());
            Common.getInstance().setMylocation(my_location);
            _shoplocation.setVisibility(ImageView.VISIBLE);
            my_location_string = my_location.latitude + "," + my_location.longitude;

        }
    }
    protected void onStart() {
        super.onStart();
        getProductData();
    }
    private void initview(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                Ion.with(getBaseContext()).load(Common.getInstance().getBaseimageURL() + shop_image).intoImageView(_shopimage);
                Log.d("favoriteshop:", shop_fav);
                _shopname.setText(shop_name);
                _shopmobile.setText(shop_mobile);
                _shopemail.setText(shop_email);
                _shopaddress.setText(shop_address);

                if(shop_fav.equals("yes"))
                {
                    _shopsetfav.setImageResource(R.drawable.fav_red);
                }else{
                    _shopsetfav.setImageResource(R.drawable.fav_black);
                }
                AllProductAdapter mAdapter = new AllProductAdapter(getBaseContext(), mProduct);
                gridView.setAdapter(mAdapter);
            }
        });

    }
    public void showMap(){
//        Intent intent=new Intent(this, OneShopMapActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/?saddr="+ my_location_string +"&daddr="+ shop_location));
        startActivity(intent);
    }
    public void setfavorite(){
        if(shop_fav.equals("no")){
            JsonObject json = new JsonObject();
            json.addProperty("userid",Common.getInstance().getUserId());
            json.addProperty("shopid",Common.getInstance().getShopid());
            try {
                Ion.with(getBaseContext())
                        .load(Common.getInstance().getBaseURL()+"api/buyersetfavuser")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null) {
                                    String status = result.get("status").getAsString();
                                    if (status.equals("ok")) {
                                        shop_fav = "yes";
                                        _shopsetfav.setImageResource(R.drawable.fav_red);
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_success),Toast.LENGTH_LONG).show();
                                    } else if (status.equals("fail")) {
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_fail),Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(),getResources().getString(R.string.toast_checkinternet),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }catch(Exception e){
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else{
            JsonObject json = new JsonObject();
            json.addProperty("userid",Common.getInstance().getUserId());
            json.addProperty("shopid",Common.getInstance().getShopid());
            try {
                Ion.with(getBaseContext())
                        .load(Common.getInstance().getBaseURL()+"api/buyerdelfavshop")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null) {
                                    String status = result.get("status").getAsString();
                                    if (status.equals("ok")) {
                                        shop_fav = "no";
                                        _shopsetfav.setImageResource(R.drawable.fav_black);
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_success),Toast.LENGTH_LONG).show();
                                    } else if (status.equals("fail")) {
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_fail),Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(),getResources().getString(R.string.toast_checkinternet),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }catch(Exception e){
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    private void getProductData(){
        JsonObject json = new JsonObject();
        Log.d("result:userid:shopid", Common.getInstance().getUserId()+Common.getInstance().getShopid());
        json.addProperty("userid",Common.getInstance().getUserId());
        json.addProperty("shopid",Common.getInstance().getShopid());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/buyershopproduct")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                Log.d("result:", result.toString());
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    String status_product = result.get("status_product").getAsString();
                                    shop_name = result.get("shopname").getAsString();
                                    shop_mobile = result.get("shopmobile").getAsString();
                                    shop_email = result.get("shopemail").getAsString();
                                    shop_address = result.get("shopaddress").getAsString();
                                    shop_image = result.get("shopimg").getAsString();
                                    shop_fav = result.get("shopfav").getAsString();
                                    shop_location = result.get("shoplocation").getAsString();
                                    Common.getInstance().setShoplocation(shop_location);
                                    if(status_product.equals("ok")){
                                        ArrayList<AllProduct> products=new ArrayList<>();
                                        JsonArray shopsJson = result.get("allproduct").getAsJsonArray();
                                        for (JsonElement shopElement : shopsJson) {
                                            JsonObject theShop = shopElement.getAsJsonObject();
                                            String id = theShop.get("id").getAsString();
                                            String shopid = theShop.get("shopid").getAsString();
                                            String name = theShop.get("productname").getAsString();
                                            String price = theShop.get("productvalue").getAsString();
                                            String image = theShop.get("productimg").getAsString();
                                            String fav = theShop.get("productfav").getAsString();

                                            products.add(new AllProduct(id,shopid,name,price,image,fav));
                                        }
                                        Common.getInstance().setmAllProducsts(products);
                                        mProduct = products;
                                    }
                                    initview();
                                } else if (status.equals("fail")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_empty),Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(),getResources().getString(R.string.toast_checkinternet),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    moveToHome();
                    return true;
                case R.id.navigation_favorite:
                    moveToFavorite();
                    return true;
                case R.id.navigation_basket:
                    moveToBasket();
                    return true;
                case R.id.navigation_logout:
                    moveToLogout();
                    return true;
            }
            return false;
        }
    };
    private void moveToHome(){
        Intent intent=new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToFavorite(){
        Intent intent=new Intent(this, BuyerFavoriteActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToBasket(){
        Intent intent=new Intent(this, BuyerBasketActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToLogout(){
        JsonObject json = new JsonObject();
        json.addProperty("username", Common.getInstance().getUsername());
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/logout")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.d("test:::", "NOOOOOOOOOOOOOO");
                            if (result != null) {
                                Common.getInstance().setUsername("");
                                Common.getInstance().setPassword("");
                                Intent intent=new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Log.d("test:::", "NOOOOOOOOOOOOOO");
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
