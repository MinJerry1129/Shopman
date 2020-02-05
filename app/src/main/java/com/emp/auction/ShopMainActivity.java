package com.emp.auction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ShopMainActivity extends AppCompatActivity {
    ArrayList<Shopmain> mShop = new ArrayList<>();
    private GridView gridView;
    TextView _resname;
    TextView _resmobile;
    ImageView _resImage;
    Button _edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_main);
        _resname = (TextView) findViewById(R.id.res_name);
        _resmobile = (TextView) findViewById(R.id.res_mobile);
        _edit = (Button) findViewById(R.id.btn_edit);
        _resImage = (ImageView) findViewById(R.id.shopimage);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        gridView = findViewById(R.id.list_product);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String product_id=Common.getInstance().getmShops().get(position).getmId();
            Common.getInstance().setProductid(product_id);
            Intent intent = new Intent(getApplicationContext(), ProductEditActivity.class);
            startActivity(intent);
            }
        });
        _edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShopEditActivity.class);
                startActivity(intent);
            }
        });
    }
    protected void onStart() {
        super.onStart();
        getProductData();
    }
    @Override
    public void onBackPressed() {
        // Disable going back to the UserMainActivity
//        moveTaskToBack(true);
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
        json.addProperty("useremail",Common.getInstance().getUseremail());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/getsellermain")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    String status_product = result.get("status_product").getAsString();
                                    String shop_id = result.get("shopid").getAsString();
                                    String shop_name = result.get("shopname").getAsString();
                                    String shop_mobile = result.get("shopmobile").getAsString();
                                    String shop_image = result.get("shopimage").getAsString();
                                    _resname.setText("Name:  "+shop_name);
                                    _resmobile.setText("Mobile:  "+shop_mobile);
                                    Glide.with(getBaseContext())
                                            .load(Common.getInstance().getBaseimageURL() + shop_image)
                                            .into(_resImage);
                                    Log.d("Shop_id:", shop_id);
                                    Common.getInstance().setShopid(shop_id);
                                    if(status_product.equals("ok")){
                                        ArrayList<Shopmain> shops=new ArrayList<>();
                                        JsonArray shopsJson = result.get("allproduct").getAsJsonArray();
                                        for (JsonElement shopElement : shopsJson) {
                                            JsonObject theShop = shopElement.getAsJsonObject();
                                            String id = theShop.get("id").getAsString();
                                            String name = theShop.get("productname").getAsString();
                                            String image = theShop.get("productimg").getAsString();
                                            String location = "0,0";
                                            shops.add(new Shopmain(id,name,image,location));
                                        }
                                        Common.getInstance().setmShops(shops);
                                        Common.getInstance().setShopid(shop_id);
                                        mShop = shops;
                                        initview_shop();
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
                case R.id.navigation_addproduct:
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
        Intent intent=new Intent(this, ShopMainActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToFavorite(){
        Intent intent=new Intent(this, ShopFavoriteActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToBasket(){
        Intent intent=new Intent(this, PostActivity.class);
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
