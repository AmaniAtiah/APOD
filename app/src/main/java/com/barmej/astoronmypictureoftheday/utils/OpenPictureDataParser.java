package com.barmej.astoronmypictureoftheday.utils;

import com.barmej.astoronmypictureoftheday.entity.NasaPicture;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenPictureDataParser {

    private static final String DATE = "date";
    private static final String TITLE = "title";
    private static final String EXPLANATION = "explanation";
    private static final String URL = "url";
    private static final String HDURL = "hdurl";
    private static final String MEDIA_TYPE = "media_type";



    public static NasaPicture getPictureInfoObjectFromJson(JSONObject apodJson) throws JSONException {


        NasaPicture nasaPicture = new NasaPicture();
        nasaPicture.setDate(apodJson.getString(DATE));
        nasaPicture.setTitle(apodJson.getString(TITLE));
        nasaPicture.setExplanation(apodJson.getString(EXPLANATION));
        nasaPicture.setUrl(apodJson.getString(URL));
        if(apodJson.has(HDURL)) {
            nasaPicture.setHdurl(apodJson.getString(HDURL));
        }
        nasaPicture.setMediaType(apodJson.getString(MEDIA_TYPE));

        return nasaPicture;
    }
}
