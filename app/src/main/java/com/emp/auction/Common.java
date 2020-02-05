package com.emp.auction;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Common {
    private String user;
    private String username;
    private String password;
    private String userRole;
    private String userId;
    private String useremail;
    private String shopaddress;
    private String shoplocation;
    private String shopid;
    private String productid;
    private String imageurl;
    private LatLng mylocation;
    private String mydeviceid;
    private String lang;

//    private String baseURL="http://10.0.2.2/shopman/";//"http://The-work-kw.com/shopman/shopman/backend/";//
//    private String baseimageURL="http://10.0.2.2/shopman/backend/";//"http://The-work-kw.com/shopman/shopman/backend/";//
    private String baseimageURL="http://myshopman.cityonwheels.net/backend/";//"http://The-work-kw.com/shopman/shopman/backend/";//
    private String baseURL="http://myshopman.cityonwheels.net/";//"http://10.0.2.2/shopbackend/";//
    private ArrayList<Productmain> mProducts;
    private ArrayList<Shopmain> mShops;
    private ArrayList<AllProduct> mAllProducsts;
    private ArrayList<AllShop> mAllShop;
    private ArrayList<Basket> mBaskets;
    private ArrayList<ProductFavorite> mProductFav;
    private ArrayList<ShopFavorite> mShopFav;

    private static Common instance = new Common();

    public void Comon(){
        //this.baseURL="http://localhost/jsontest/";
    }

    public LatLng getMylocation() { return mylocation;}

    public void setMylocation(LatLng mylocation) {this.mylocation = mylocation;}

    public String getImageurl() {return imageurl;}

    public void setImageurl(String imageurl) {this.imageurl = imageurl;}

    public String getUseremail() {return useremail;}

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getProductid() { return productid;}

    public void setProductid(String productid) { this.productid = productid;  }

    public ArrayList<Shopmain> getmShops() { return mShops;}

    public void setmShops(ArrayList<Shopmain> mShops) { this.mShops = mShops;}

    public String getShopid() {return shopid;}

    public void setShopid(String shopid) {this.shopid = shopid;}

    public ArrayList<Productmain> getmProducts() {return mProducts;}

    public void setmProducts(ArrayList<Productmain> mProducts) {this.mProducts = mProducts;}

    public ArrayList<AllProduct> getmAllProducsts() { return mAllProducsts;}

    public void setmAllProducsts(ArrayList<AllProduct> mAllProducsts) { this.mAllProducsts = mAllProducsts;}

    public ArrayList<AllShop> getmAllShop() { return mAllShop;}

    public void setmAllShop(ArrayList<AllShop> mAllShop) {this.mAllShop = mAllShop;}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShopaddress() {
        return shopaddress;
    }

    public String getShoplocation() {
        return shoplocation;
    }

    public void setShopaddress(String shopaddress) {
        this.shopaddress = shopaddress;
    }

    public void setShoplocation(String shoplocation) {
        this.shoplocation = shoplocation;
    }

    public static Common getInstance()
    {
        return instance;
    }

    public String getMydeviceid() { return mydeviceid;}

    public void setMydeviceid(String mydeviceid) { this.mydeviceid = mydeviceid; }

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getLang() { return lang;}

    public void setLang(String lang) { this.lang = lang; }

    public String getBaseURL() {
        return baseURL;
    }

    public String getBaseimageURL() { return baseimageURL; }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public void setmBaskets(ArrayList<Basket> mBaskets) {this.mBaskets = mBaskets;}
    public ArrayList<Basket> getmBaskets() { return mBaskets;}

    public ArrayList<ProductFavorite> getmProductFav() { return mProductFav;}
    public void setmProductFav(ArrayList<ProductFavorite> mProductFav) { this.mProductFav = mProductFav;}

    public ArrayList<ShopFavorite> getmShopFav() {return mShopFav;}
    public void setmShopFav(ArrayList<ShopFavorite> mShopFav) { this.mShopFav = mShopFav;}
}
