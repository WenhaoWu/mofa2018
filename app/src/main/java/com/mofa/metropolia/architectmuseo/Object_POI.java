package com.mofa.metropolia.architectmuseo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Object_POI implements Parcelable{

    private double latitude;
    private double longitude;
    private String name;
    private String imgBase64;
    private int ID;
    private String descrip;
    private double disTo;
    private double rate_score;
    private int rate_count;
    private String designer;
    private int timeTo;
    private String reasonForSug;
    private String website;
    private String competition;

    public Object_POI(double latitude, double longitude, String name, int ID,
                      String imgBase64, String descrip, double disTo, double rate_score,
                      int rate_count, String designer, int timeTo, String reason,
                      String website, String competition) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.ID = ID;
        this.imgBase64 = imgBase64;
        this.descrip = descrip;
        this.disTo = disTo;
        this.rate_score = rate_score;
        this.rate_count = rate_count;
        this.designer = designer;
        this.timeTo = timeTo;
        this.reasonForSug = reason;
        this.website = website;
        this.competition = competition;
    }

    public Object_POI() {
    }

    public static final Creator<Object_POI> CREATOR = new Creator<Object_POI>() {
        @Override
        public Object_POI createFromParcel(Parcel in) {
            return new Object_POI(in);
        }

        @Override
        public Object_POI[] newArray(int size) {
            return new Object_POI[size];
        }
    };

    public int getTimeTo() {
        return timeTo;
    }

    public double getRate_score() {
        return rate_score;
    }

    public int getRate_count() {
        return rate_count;
    }

    public double getDisTo() {
        return disTo;
    }

    public int getID() {
        return ID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return  name;
    }

    public void printPOI (){
        Log.e("POI", "Name: "+name +" Lat: " + this.latitude + " Long: " + this.longitude);
    }

    public String getImgBase64() {
        return imgBase64;
    }

    @Override
    public String toString() {
        return "Name: "+name;
    }

    public String getDescrip() {
        return descrip;
    }

    public String getDesigner(){
        return designer;
    }

    public String getReasonForSug() {
        return reasonForSug;
    }

    //The following three methods make this object parcelable

    private Object_POI(Parcel in){

        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        imgBase64 = in.readString();
        ID = in.readInt();
        descrip = in.readString();
        disTo = in.readInt();
        rate_score = in.readDouble();
        rate_count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeString(imgBase64);
        dest.writeInt(ID);
        dest.writeString(descrip);
        dest.writeDouble(disTo);
        dest.writeDouble(rate_score);
        dest.writeInt(rate_count);
    }




}
