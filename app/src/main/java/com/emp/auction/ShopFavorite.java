package com.emp.auction;

public class ShopFavorite {
    private String mId;
    private String mShopId;
    private String mName;
    private String mImage;
    private String mMobile;
    private String mEmail;
    private String mAddress;

    public ShopFavorite(String id,String shopid,String name,String email,String mobile,String address,String image){
        mId=id;
        mShopId = shopid;
        mName=name;
        mEmail=email;
        mMobile = mobile;
        mAddress = address;
        mImage=image;
    }

    public String getmId() {
        return mId;
    }

    public String getmShopId() {return mShopId;}

    public String getmName() {
        return mName;
    }

    public String getmMobile() { return  mMobile;}

    public String getmAddress() {return mAddress;}

    public String getmEmail() {return mEmail;}

    public String getmImage() {
        return Common.getInstance().getBaseimageURL()+mImage;
    }

}
