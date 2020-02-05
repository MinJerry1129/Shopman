package com.emp.auction;

public class Productmain {
    private String mId;
    private String mShopId;
    private String mName;
    private String mPrice;
    private String mImage;
    private String mDescription;

    public Productmain(String id,String shopId,String name,String price,String image,String description){
        mId=id;
        mShopId = shopId;
        mName=name;
        mPrice = price;
        String currentString = image;
        String[] separated = currentString.split("_split_");
        mImage=separated[0];
        mDescription=description;
    }

    public String getmId() {
        return mId;
    }

    public String getmShopId() { return mShopId;}

    public String getmPrice() { return mPrice;}

    public String getmName() {
        return mName;
    }

    public String getmImage() {
        return Common.getInstance().getBaseimageURL()+mImage;
    }

    public String getmDescription() {
        return mDescription;
    }
}
