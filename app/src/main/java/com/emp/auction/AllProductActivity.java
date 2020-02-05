package com.emp.auction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class AllProductActivity extends AppCompatActivity {
    ArrayList<AllProduct> mProduct = new ArrayList<>();
    private ArrayList<AllProduct> mTempProductList;
    private GridView gridView;
    EditText _editSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);
        gridView = findViewById(R.id.list_allProduct);
        _editSearch = findViewById(R.id.editSearch);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shop_id=mTempProductList.get(position).getmShopID();
                String product_id=mTempProductList.get(position).getmId();
                Common.getInstance().setShopid(shop_id);
                Common.getInstance().setProductid(product_id);
                Intent intent = new Intent(getApplicationContext(), OneProductActivity.class);
                startActivity(intent);
                Log.d("shop_id: ", shop_id);
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        _editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pattern=_editSearch.getText().toString().trim();
                searchRestaurant(pattern);
            }
        });
        TextView emptyView = (TextView)findViewById(R.id.emptyview);
        gridView.setEmptyView(emptyView);

        findViewById(R.id.imgSearch_Close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _editSearch.setText("");
                mTempProductList=new ArrayList<AllProduct>(mProduct);
                findViewById(R.id.imgSearch_Close).setVisibility(View.INVISIBLE);
                initview();
            }
        });
    }
    protected void onStart() {
        super.onStart();
        getProductData();
    }
    private void initview(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                AllProductAdapter mAdapter = new AllProductAdapter(getBaseContext(), mProduct);
                gridView.setAdapter(mAdapter);
            }
        });

    }

    private void showProductList(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                AllProductAdapter mAdapter = new AllProductAdapter(getBaseContext(), mTempProductList);
                gridView.setAdapter(mAdapter);
            }
        });

    }

    private void searchRestaurant(String pattern){

        Log.d("aasdfadf:", mProduct.toString());
        mTempProductList.clear();
        Log.d("aasdfadf:", mProduct.toString());
        if(pattern.isEmpty()){
            mTempProductList=new ArrayList<AllProduct>(mProduct);
            findViewById(R.id.imgSearch_Close).setVisibility(View.INVISIBLE);
        }
        else{
            for(AllProduct theProduct:mProduct){
                if(theProduct.getmName().toLowerCase().contains(pattern.toLowerCase())){
                    mTempProductList.add(theProduct);
                }
            }
            findViewById(R.id.imgSearch_Close).setVisibility(View.VISIBLE);
        }
        showProductList();
    }

    private void getProductData(){
        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getUserId());

        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/buyerallproduct")
                    .setJsonObjectBody(json)
                    .asJsonObject()

                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    String status_product = result.get("status_product").getAsString();
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
                                        mTempProductList=new ArrayList<AllProduct>(mProduct);
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
                            if (result != null) {
                                Common.getInstance().setUsername("");
                                Common.getInstance().setPassword("");
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
