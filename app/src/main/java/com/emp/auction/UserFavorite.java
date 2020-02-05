package com.emp.auction;

public class UserFavorite {
    private String mId;
    private String mName;
    private String mEmail;
    private String mMobile;
//    private String mImage;

    public UserFavorite(String id,String name,String email, String mobile ){
        mId=id;
        mName=name;
        mEmail=email;
        mMobile=mobile;
//        mImage=image;
    }

    public String getmEmail() { return mEmail;}

    public String getmMobile() {return mMobile;}

    public String getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

//    public String getmImage() {
//        return Common.getInstance().getBaseURL()+mImage;
//    }

}
