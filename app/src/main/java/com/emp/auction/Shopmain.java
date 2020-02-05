package com.emp.auction;

public class Shopmain {
    private String mId;
    private String mName;
    private String mImage;
    private String mLocation;

    public Shopmain(String id,String name,String image,String location){
        mId=id;
        mName=name;
        String currentString = image;
        String[] separated = currentString.split("_split_");
        mImage=separated[0];
        mLocation = location;
    }

    public String getmLocation() {return mLocation;}

    public String getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmImage() {
        return Common.getInstance().getBaseimageURL()+mImage;
    }

}
