package com.emp.auction;

public class AllProduct {
    private String mId;
    private String mShopID;
    private String mName;
    private String mPrice;
    private String mImage;
    private String mFav;

    public AllProduct(String id,String shopid,String name,String price,String image,String fav){
        mId=id;
        mShopID = shopid;
        mName=name;
        mPrice = price;
        String currentString = image;
        String[] separated = currentString.split("_split_");
        mImage=separated[0];
        mFav=fav;
    }

    public String getmFav() { return mFav;}

    public String getmId() {
        return mId;
    }

    public String getmShopID() { return mShopID; }

    public String getmName() {
        return mName;
    }

    public String getmPrice() { return mPrice; }

    public String getmImage() {
        return Common.getInstance().getBaseimageURL()+mImage;
    }

}
