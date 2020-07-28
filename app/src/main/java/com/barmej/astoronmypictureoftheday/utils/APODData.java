package com.barmej.astoronmypictureoftheday.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.barmej.astoronmypictureoftheday.entity.NasaPicture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;
import static com.barmej.astoronmypictureoftheday.MainActivity.SHARED_PREFS;

public class APODData {

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
