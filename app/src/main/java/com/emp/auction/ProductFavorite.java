package com.emp.auction;

public class ProductFavorite {
    private String mId;
    private String mProductid;
    private String mShopID;
    private String mName;
    private String mPrice;
    private String mImage;

    public ProductFavorite(String id,String productid,String shopid,String name,String price,String image){
        mId=id;
        mProductid = productid;
        mShopID = shopid;
        mName=name;
        mPrice = price;
        String currentString = image;
        String[] separated = currentString.split("_split_");
        mImage=separated[0];
    }

    public String getmId() {
        return mId;
    }

    public String getmProductid() {return mProductid;}

    public String getmShopID() { return mShopID; }

    public String getmName() {
        return mName;
    }

    public String getmPrice() { return mPrice; }

    public String getmImage() {
        return Common.getInstance().getBaseimageURL()+mImage;
    }

}
