package com.emp.auction;

public class Basket {
    private String mId;
    private String mProductID;
    private String mShopID;
    private String mName;
    private String mPrice;
    private String mImage;
    private String mCount;

    public Basket(String id,String productid,String shopid,String name,String price,String image,String count){
        mId=id;
        mProductID = productid;
        mShopID = shopid;
        mName=name;
        mPrice = price;
        String currentString = image;
        String[] separated = currentString.split("_split_");
        mImage=separated[0];
        mCount=count;
    }


    public String getmId() {
        return mId;
    }

    public String getmProductID() {return mProductID; }

    public String getmShopID() { return mShopID; }

    public String getmName() {
        return mName;
    }

    public String getmPrice() { return mPrice; }

    public String getmImage() {
        return Common.getInstance().getBaseimageURL()+mImage;
    }

    public String getmCount() { return mCount;}
}
