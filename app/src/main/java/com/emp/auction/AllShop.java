package com.emp.auction;

public class AllShop {
    private String mId;
    private String mName;
    private String mImage;
    private String mFav;
    private String mDes;

    public AllShop(String id,String name,String image,String fav,String des){
        mId=id;
        mName=name;
        mImage=image;
        mFav=fav;
        mDes=des;
    }

    public String getmFav() { return mFav;}

    public String getmId() { return mId;}

    public String getmName() {
        return mName;
    }

    public String getmImage() {
        return Common.getInstance().getBaseimageURL()+mImage;
    }

    public String getmDes() { return mDes;}
    public void setmDes(String mDes) { this.mDes = mDes; }
}
