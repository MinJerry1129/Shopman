package com.emp.auction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class ShopFavoriteActivity extends AppCompatActivity {
    ArrayList<UserFavorite> mUser = new ArrayList<>();
    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_favorite);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_favorite);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        gridView = findViewById(R.id.list_allFavorite);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String shop_id=Common.getInstance().getmAllProducsts().get(position).getmShopID();
//                String product_id=Common.getInstance().getmAllProducsts().get(position).getmId();
//                Common.getInstance().setShopid(shop_id);
//                Common.getInstance().setProductid(product_id);
////                Intent intent = new Intent(getApplicationContext(), OneProductActivity.class);
////                startActivity(intent);
//                Log.d("shop_id: ", shop_id);
//            }
//        });
    }
    protected void onStart() {
        super.onStart();
        getProductData();
    }

    private void initview_shop(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                UserFavoriteAdapter mAdapter = new UserFavoriteAdapter(getBaseContext(), mUser);
                gridView.setAdapter(mAdapter);
            }
        });

    }


    private void getProductData(){
        JsonObject json = new JsonObject();
        Log.d("shop_id:", Common.getInstance().getShopid());
        json.addProperty("userid",Common.getInstance().getShopid());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/sellerfavpage")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    if (status.equals("ok")) {
                                        String status_shop = result.get("status_user").getAsString();
                                        if(status_shop.equals("ok")){
                                            ArrayList<UserFavorite> users=new ArrayList<>();
                                            JsonArray shopsJson = result.get("allbuyer").getAsJsonArray();
                                            for (JsonElement shopElement : shopsJson) {
                                                JsonObject theShop = shopElement.getAsJsonObject();
                                                String id = theShop.get("buyerid").getAsString();
                                                String name = theShop.get("buyername").getAsString();
                                                String email = theShop.get("buyeremail").getAsString();
                                                String mobile = theShop.get("buyermobile").getAsString();
                                                users.add(new UserFavorite(id,name,email,mobile));
                                            }
//                                            Common.getInstance().setmShops(shops);
                                            mUser  = users;
                                            initview_shop();
                                        }

                                    } else if (status.equals("fail")) {
                                        Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_empty),Toast.LENGTH_LONG).show();
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

    @Override
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
