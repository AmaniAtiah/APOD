package com.barmej.astoronmypictureoftheday.entity;

public class NasaPicture {
    private String date;
    private String explanation;
    private String hdurl;
    private String mediaType;
    private String title;
    private String url;


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

    public void setMediaType(String media_Type) {
        this.mediaType = media_Type;
    }
}
