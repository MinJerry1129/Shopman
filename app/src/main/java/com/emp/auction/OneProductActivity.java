package com.emp.auction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class OneProductActivity extends AppCompatActivity{
    private static final String TAG = "paymentExample";
    /**
     *      * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *      *
     *      * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     *      * from https://developer.paypal.com
     *      *
     *      * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     *      * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AeGp29MHu5J1QzC2VCSizUpsrFtHWRLDsZVlvCX8CiJYfCP5UqW9CygpCD7NLj2Auf1u0eqHfonWIcgT";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(true)
            .rememberUser(false);

    SliderLayout mDemoSlider;
    ImageView _basketimg;
    ImageView _shopimage;
    ImageView _favsetimg;
    ImageView _phone;
    ImageView _position;
    TextView _productname;
    TextView _shopname;
    TextView _shopphone;
    TextView _productprice;
    EditText _buycount;
    TextView _productdes;

    Button _buy_product;

    String product_name ;
    String product_price;
    String product_des;
    String product_fav ;
    String product_image ;
    String shop_name;
    String shop_mobile;
    String shop_location;
    String shop_image;
    LocationManager locationmanager;
    LatLng my_location;
    String my_location_string;
    private static  final int REQUEST_LOCATION=1;
    private Location mCurrentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_product);
        mDemoSlider = (SliderLayout) findViewById(R.id.imageSlider);
        _basketimg = (ImageView) findViewById(R.id.basketimg);
        _shopimage = (ImageView) findViewById(R.id.shopimage);
        _favsetimg = (ImageView) findViewById(R.id.favsetimg);
        _phone = (ImageView) findViewById(R.id.imgCall);
        _position = (ImageView) findViewById(R.id.imglocation);
        _productname = (TextView) findViewById(R.id.productname);
        _shopname = (TextView) findViewById(R.id.shopname);
        _shopphone = (TextView) findViewById(R.id.shopphone);
        _productprice = (TextView) findViewById(R.id.productprice);
        _productdes = (TextView) findViewById(R.id.productdescription);
        _buycount = (EditText) findViewById(R.id.buycount);
        _buy_product = (Button) findViewById(R.id.btnbuy);

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000); //set scroll delay in seconds :
        _position.setVisibility(ImageView.GONE);
        _favsetimg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setfavorite();

            }
        });
        _position.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showmap();

            }
        });
        _phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhone();
            }
        });

        _basketimg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setbasket();
            }
        });
        _buy_product.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buyproduct();
            }
        });
        setready();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
            _position.setVisibility(ImageView.VISIBLE);
            my_location_string = my_location.latitude + "," + my_location.longitude;
        }

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

    }
    public void setSliderViews() {
        String[] imageUrls=product_image.split("_split_");
        for (int i=0;i<imageUrls.length;i++) {
            final String theImageUrl=Common.getInstance().getBaseimageURL()+imageUrls[i];
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.image(theImageUrl).setScaleType(BaseSliderView.ScaleType.CenterCrop);
            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Common.getInstance().setImageurl(theImageUrl);
                    Intent intent=new Intent(OneProductActivity.this, FullScreenActivity.class);//LoginActivity.class);
                    startActivity(intent);
                }
            });
            mDemoSlider.addSlider(textSliderView);
        }
    }

    public void showmap(){
//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.google.com/maps/@"+shop_location+",19z"));
//        startActivity(intent);
//        Intent intent=new Intent(this, OneShopMapActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/?saddr="+ my_location_string +"&daddr="+ shop_location));
        startActivity(intent);
    }
    public void callPhone(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+shop_mobile));
        startActivity(intent);
    }
    public void setfavorite(){
        if(product_fav.equals("no")){
            JsonObject json = new JsonObject();
            json.addProperty("userid",Common.getInstance().getUserId());
            json.addProperty("productid",Common.getInstance().getProductid());
            try {
                Ion.with(getBaseContext())
                        .load(Common.getInstance().getBaseURL()+"api/buyersetfavproduct")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null) {
                                    String status = result.get("status").getAsString();
                                    if (status.equals("ok")) {
                                        product_fav = "yes";
                                        _favsetimg.setImageResource(R.drawable.fav_red);
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
            json.addProperty("productid",Common.getInstance().getProductid());
            try {
                Ion.with(getBaseContext())
                        .load(Common.getInstance().getBaseURL()+"api/buyerdelfavpro")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null) {
                                    String status = result.get("status").getAsString();
                                    if (status.equals("ok")) {
                                        product_fav = "no";
                                        _favsetimg.setImageResource(R.drawable.fav_black);
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

    public void buyproduct(){
//        String price = _productprice.getText().toString(); // example String
        double dprice = Double.parseDouble(product_price);
        String quality = _buycount.getText().toString(); // example String
        double dquality = Double.parseDouble(quality);
        double total = dprice * dquality;

        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(String.valueOf(total)), "EUR", "Contact information fee",PayPalPayment.PAYMENT_INTENT_SALE);
        thingToBuy.payeeEmail("testshop1@test.com");
        Intent intent = new Intent(this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        //add to code to send payment info
                        setResult(102);
//                        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
//                        progressDialog.setIndeterminate(true);
//                        progressDialog.setMessage("Loading...");
//                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        progressDialog.setCancelable(false);
//                        progressDialog.show();

                    } catch (JSONException e) {
                        //Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                        Toast.makeText(getBaseContext(), "an extremely unlikely failure occurred:"+e, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Log.i(TAG, "The user canceled.");
                Toast.makeText(getBaseContext(), "The user canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                //Log.i(TAG,"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(getBaseContext(), "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onStop() {
        Log.d("test","stoped");
        stopService(new Intent(this, PayPalService.class));
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
        //Log.d("destroy","now");
    }

    public void setbasket(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, Add  this product to basket?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        arg0.dismiss();
                        String count = _buycount.getText().toString();
                        JsonObject json = new JsonObject();
                        json.addProperty("userid",Common.getInstance().getUserId());
                        json.addProperty("productid",Common.getInstance().getProductid());
                        json.addProperty("count",count);
                        try {
                            Ion.with(getBaseContext())
                                    .load(Common.getInstance().getBaseURL()+"api/buyeraddbasket")
                                    .setJsonObjectBody(json)
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            if (result != null) {
                                                String status = result.get("status").getAsString();
                                                if (status.equals("ok")) {
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
                });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void initview(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                _productname.setText(product_name);
                _shopname.setText(shop_name);
                _shopphone.setText(shop_mobile);
                _productdes.setText(product_des);
                _productprice.setText(product_price+"€");
                if(product_fav.equals("yes"))
                {
                    _favsetimg.setImageResource(R.drawable.fav_red);
                }else{
                    _favsetimg.setImageResource(R.drawable.fav_black);
                }
                Ion.with(getBaseContext()).load(Common.getInstance().getBaseimageURL() + shop_image).intoImageView(_shopimage);
            }
        });


//        Picasso.get().load(Common.getInstance().getBaseURL() + shop_image).into(_shopimage);

    }

    public void setready(){
        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getUserId());
        json.addProperty("productid",Common.getInstance().getProductid());
        json.addProperty("shopid",Common.getInstance().getShopid());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/buyeroneproduct")
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
                                        product_name = result.get("productname").getAsString();
                                        product_price = result.get("productvalue").getAsString();
                                        product_des = result.get("productdes").getAsString();
                                        product_fav = result.get("fav").getAsString();
                                        product_image = result.get("productimg").getAsString();
                                        shop_name = result.get("shopname").getAsString();
                                        shop_mobile = result.get("shopmobile").getAsString();
                                        shop_location = result.get("shoplocation").getAsString();
                                        Common.getInstance().setShoplocation(shop_location);
                                        shop_image = result.get("shopimg").getAsString();
                                        initview();
                                        setSliderViews();
                                    }
                                } else if (status.equals("fail")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_checkinternet),Toast.LENGTH_LONG).show();
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
