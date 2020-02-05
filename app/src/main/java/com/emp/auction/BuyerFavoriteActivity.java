package com.emp.auction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class BuyerFavoriteActivity extends AppCompatActivity {
    ArrayList<ProductFavorite> mProduct = new ArrayList<>();
    ArrayList<ShopFavorite> mShop = new ArrayList<>();
    private GridView gridView;
    Button _fav_product;
    Button _fav_shop;
    String sel_type="product";
    ProductFavoriteAdapter mAdapter_product;
    ShopFavoriteAdapter mAdapter_shop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_favorite);
        _fav_product = (Button) findViewById(R.id.btn_product_sel);
        _fav_shop = (Button) findViewById(R.id.btn_shop_sel);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_favorite);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        gridView = findViewById(R.id.list_allFavorite);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(sel_type == "shop") {
                    String shop_id = Common.getInstance().getmShopFav().get(position).getmShopId();
                    Common.getInstance().setShopid(shop_id);
                    Intent intent = new Intent(getApplicationContext(), OneShopActivity.class);
                    startActivity(intent);
                }else{
                    String shop_id = Common.getInstance().getmProductFav().get(position).getmShopID();
                    String product_id = Common.getInstance().getmProductFav().get(position).getmProductid();
                    Common.getInstance().setShopid(shop_id);
                    Common.getInstance().setProductid(product_id);
                    Intent intent = new Intent(getApplicationContext(), OneProductActivity.class);
                    startActivity(intent);
                }

            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setMessage("Really delete this product?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int which) {
                                arg0.dismiss();
                                if(sel_type == "product"){
                                    String favid=mProduct.get(position).getmId();
                                    JsonObject json = new JsonObject();
                                    json.addProperty("favid",favid);

                                    //final File fileToUpload = new File(imageUrls.get(0));   //temp 0
                                    try {
                                        Ion.with(getBaseContext())
                                                .load(Common.getInstance().getBaseURL()+"api/buyerdelfavproduct")
                                                .setJsonObjectBody(json)
                                                .asJsonObject()
                                                .setCallback(new FutureCallback<JsonObject>() {
                                                    @Override
                                                    public void onCompleted(Exception e, JsonObject result) {
                                                        if (result != null) {
                                                            String status = result.get("status").getAsString();
                                                            if (status.equals("ok")) {
                                                                mProduct.remove(position);
                                                                mAdapter_product.notifyDataSetChanged();
                                                                Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_success), Toast.LENGTH_LONG).show();
                                                            } else {
                                                                Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_empty), Toast.LENGTH_LONG).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_empty), Toast.LENGTH_LONG).show();
                                                            Log.d("err",e.toString());
                                                        }
                                                    }
                                                });
                                    }catch(Exception e){
                                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }else if(sel_type == "shop"){
                                    String favid=mShop.get(position).getmId();
                                    JsonObject json = new JsonObject();
                                    json.addProperty("favid",favid);
                                    try {
                                        Ion.with(getBaseContext())
                                                .load(Common.getInstance().getBaseURL()+"api/buyerdelfavuser")
                                                .setJsonObjectBody(json)
                                                .asJsonObject()
                                                .setCallback(new FutureCallback<JsonObject>() {
                                                    @Override
                                                    public void onCompleted(Exception e, JsonObject result) {
                                                        if (result != null) {
                                                            String status = result.get("status").getAsString();
                                                            if (status.equals("ok")) {
                                                                mShop.remove(position);
                                                                mAdapter_shop.notifyDataSetChanged();
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
                        });

                alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
        _fav_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product_sel();
            }
        });

        _fav_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shop_sel();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        getProductData();
    }
    private void delete_product(String favid){

    }
    private void initview(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                mAdapter_product = new ProductFavoriteAdapter(getBaseContext(), mProduct);
                gridView.setAdapter(mAdapter_product);
            }
        });

    }

    private void initview_shop(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                mAdapter_shop = new ShopFavoriteAdapter(getBaseContext(), mShop);
                gridView.setAdapter(mAdapter_shop);
            }
        });

    }

    public void product_sel() {
        sel_type = "product";
        _fav_product.setTextColor(0xffe8732d);
        _fav_product.setBackgroundColor(0xffffffff);
        _fav_shop.setTextColor(0xffffffff);
        _fav_shop.setBackgroundColor(0xffe8732d);
        initview();
    }

    public void shop_sel() {
        sel_type = "shop";
        _fav_product.setTextColor(0xffffffff);
        _fav_product.setBackgroundColor(0xffe8732d);
        _fav_shop.setTextColor(0xffe8732d);
        _fav_shop.setBackgroundColor(0xffffffff);
        initview_shop();
    }


    private void getProductData(){
        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getUserId());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/buyerfavpage")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {

                                    String status_shop = result.get("status_shop").getAsString();
                                    String status_product = result.get("status_product").getAsString();
                                    if(status_shop.equals("ok")){
                                        ArrayList<ShopFavorite> shops=new ArrayList<>();
                                        JsonArray shopsJson = result.get("allshop").getAsJsonArray();
                                        for (JsonElement shopElement : shopsJson) {
                                            JsonObject theShop = shopElement.getAsJsonObject();
                                            String id = theShop.get("favid").getAsString();
                                            String shopid = theShop.get("shopid").getAsString();
                                            String name = theShop.get("shopname").getAsString();
                                            String email = theShop.get("shopemail").getAsString();
                                            String mobile = theShop.get("shopmobile").getAsString();
                                            String address = theShop.get("shopaddress").getAsString();
                                            String image = theShop.get("shopimg").getAsString();
                                            shops.add(new ShopFavorite(id,shopid,name,email,mobile,address,image));
                                        }
                                            Common.getInstance().setmShopFav(shops);
                                        mShop = shops;
//                                            initview_shop();
                                    }
                                    if(status_product.equals("ok")){
                                        ArrayList<ProductFavorite> products=new ArrayList<>();
                                        JsonArray productsJson = result.get("allproduct").getAsJsonArray();
                                        for (JsonElement productElement : productsJson) {
                                            JsonObject theProduct = productElement.getAsJsonObject();
                                            String id = theProduct.get("favid").getAsString();
                                            String productid = theProduct.get("productid").getAsString();
                                            String shopId = theProduct.get("shopid").getAsString();
                                            String name = theProduct.get("productname").getAsString();
                                            String price = theProduct.get("productvalue").getAsString();
                                            String image = theProduct.get("productimg").getAsString();
                                            String description = theProduct.get("productdes").getAsString();
                                            products.add(new ProductFavorite(id,productid,shopId,name,price,image));
                                        }
                                        Common.getInstance().setmProductFav(products);
                                        mProduct= products;
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
