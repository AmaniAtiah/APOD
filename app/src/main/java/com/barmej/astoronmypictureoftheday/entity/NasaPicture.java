package com.barmej.astoronmypictureoftheday.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class NasaPicture implements Parcelable{
    private String date;
    private String explanation;
    private String hdurl;
    private String mediaType;
    private String title;
    private String url;

    protected NasaPicture(Parcel in) {
        date = in.readString();
        explanation = in.readString();
        hdurl = in.readString();
        mediaType = in.readString();
        title = in.readString();
        url = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest,int flags) {
        dest.writeString(date);
        dest.writeString(explanation);
        dest.writeString(hdurl);
        dest.writeString(mediaType);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NasaPicture> CREATOR = new Creator<NasaPicture>() {
        @Override
        public NasaPicture createFromParcel(Parcel in) {
            return new NasaPicture(in);
        }

        @Override
        public NasaPicture[] newArray(int size) {
            return new NasaPicture[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type = "Nasa Picture";




    public NasaPicture() {

    }

    public NasaPicture(String type) {
        this.type = type;
    }




    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getHdurl() {
        return hdurl;
    }

    public void setHdurl(String hdurl) {
        this.hdurl = hdurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String media_Type){
        this.mediaType = media_Type;
    }
}
