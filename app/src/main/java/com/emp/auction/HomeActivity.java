package com.emp.auction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    ArrayList<Productmain> mProduct=new ArrayList<>();
    ArrayList<Shopmain> mShop = new ArrayList<>();
    private GridView gridView;
    private TextView _showall_shop;
    private TextView _showall_product;
    private ImageView _shop_position;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        _showall_shop = (TextView) findViewById(R.id.showallshop);
        _showall_product = (TextView) findViewById(R.id.showallproduct);
        _shop_position = (ImageView) findViewById(R.id.shop_position);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        gridView = findViewById(R.id.list_shop);
        _showall_shop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), AllShopActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        _showall_product.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), AllProductActivity.class);
                startActivity(intent);
                //finish();

            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shop_id=Common.getInstance().getmShops().get(position).getmId();
                Common.getInstance().setShopid(shop_id);
                Intent intent = new Intent(getApplicationContext(), OneShopActivity.class);
                startActivity(intent);
            }
        });
        _shop_position.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), ShopMapActivity.class);
                startActivity(intent);
                //finish();

            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    protected void onStart() {
        super.onStart();
        count = 0;
        getProductData();
    }
    private void initview(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
                RecyclerView recyclerView = findViewById(R.id.list_product);
                recyclerView.setLayoutManager(layoutManager);
                ProductmainAdapter adapter = new ProductmainAdapter(getBaseContext(), mProduct);
                recyclerView.setAdapter(adapter);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = this.findViewById(R.id.list_product);
        recyclerView.setLayoutManager(layoutManager);
        ProductmainAdapter adapter = new ProductmainAdapter(this, mProduct);
        recyclerView.setAdapter(adapter);
    }
    private void initview_shop(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                ShopmainAdapter mAdapter = new ShopmainAdapter(getBaseContext(), mShop);
                gridView.setAdapter(mAdapter);
            }
        });

    }

    private void getProductData(){
        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getUserId());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/getbuyermain")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                Log.d("result:", result.toString());
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    String status_shop = result.get("status_shop").getAsString();
                                    String status_product = result.get("status_product").getAsString();
                                    if(status_shop.equals("ok")){
                                        ArrayList<Shopmain> shops=new ArrayList<>();
                                        JsonArray shopsJson = result.get("allshop").getAsJsonArray();
                                        for (JsonElement shopElement : shopsJson) {
                                            JsonObject theShop = shopElement.getAsJsonObject();
                                            String id = theShop.get("id").getAsString();
                                            String name = theShop.get("shopname").getAsString();
                                            String image = theShop.get("shopimg").getAsString();
                                            String location = theShop.get("shoplocation").getAsString();
                                            if(count<10){
                                                shops.add(new Shopmain(id,name,image,location));
                                            }
                                            count = count + 1;
                                        }
                                        Common.getInstance().setmShops(shops);
                                        mShop = shops;
                                        initview_shop();
                                    }
                                    if(status_product.equals("ok")){
                                        ArrayList<Productmain> products=new ArrayList<>();
                                        JsonArray productsJson = result.get("allproduct").getAsJsonArray();

                                        for (JsonElement productElement : productsJson) {

                                            JsonObject theProduct = productElement.getAsJsonObject();
                                            String id = theProduct.get("id").getAsString();
                                            String shopId = theProduct.get("shopid").getAsString();
                                            String name = theProduct.get("productname").getAsString();
                                            String price = theProduct.get("productvalue").getAsString();
                                            String image = theProduct.get("productimg").getAsString();
                                            String description = theProduct.get("productdes").getAsString();

                                            products.add(new Productmain(id,shopId,name,price,image,description));

                                        }
                                        Common.getInstance().setmProducts(products);
                                        mProduct= Common.getInstance().getmProducts();
                                        initview();
                                    }

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
    public void onBackPressed() {
        // Disable going back to the UserMainActivity
//        moveTaskToBack(true);
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
