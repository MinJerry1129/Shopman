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
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class BuyerBasketActivity extends AppCompatActivity {
    ArrayList<Basket> mProduct = new ArrayList<>();
    private GridView gridView;
    BasketAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_basket);

        gridView = findViewById(R.id.list_basket);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shop_id=Common.getInstance().getmBaskets().get(position).getmShopID();
                String product_id=Common.getInstance().getmBaskets().get(position).getmId();
                Common.getInstance().setShopid(shop_id);
                Common.getInstance().setProductid(product_id);
                Intent intent = new Intent(getApplicationContext(), OneProductActivity.class);
                startActivity(intent);

                Log.d("shop_id: ", shop_id);
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
                                String basketid=mProduct.get(position).getmId();
                                JsonObject json = new JsonObject();
                                json.addProperty("basketid",basketid);
                                //final File fileToUpload = new File(imageUrls.get(0));   //temp 0
                                try {
                                    Ion.with(getBaseContext())
                                            .load(Common.getInstance().getBaseURL()+"api/buyerdelbasket")
                                            .setJsonObjectBody(json)
                                            .asJsonObject()
                                            .setCallback(new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject result) {
                                                    if (result != null) {
                                                        String status = result.get("status").getAsString();
                                                        if (status.equals("ok")) {
                                                            mProduct.remove(position);
                                                            mAdapter.notifyDataSetChanged();
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_basket);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
    protected void onStart() {
        super.onStart();
        getProductData();
    }
    private void initview(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                mAdapter = new BasketAdapter(getBaseContext(), mProduct);
                gridView.setAdapter(mAdapter);
            }
        });

    }
    private void getProductData(){
        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getUserId());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/buyerbasket")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    ArrayList<Basket> products=new ArrayList<>();
                                    JsonArray shopsJson = result.get("allproduct").getAsJsonArray();
                                    for (JsonElement shopElement : shopsJson) {
                                        JsonObject theShop = shopElement.getAsJsonObject();
                                        String id = theShop.get("basketid").getAsString();
                                        String count = theShop.get("basketcount").getAsString();
                                        String productid = theShop.get("basketid").getAsString();
                                        String shopid = theShop.get("shopid").getAsString();
                                        String name = theShop.get("productname").getAsString();
                                        String price = theShop.get("productvalue").getAsString();
                                        String image = theShop.get("productimg").getAsString();
                                        products.add(new Basket(id,productid,shopid,name,price,image,count));
                                    }
                                    Common.getInstance().setmBaskets(products);
                                    mProduct = products;
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
