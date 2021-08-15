package com.emotaxi.widget;

import android.content.Context;
import android.util.Log;

import com.emotaxi.model.GetProfileModel;
import com.emotaxi.model.SignUpModel;
import com.emotaxi.retrofit.Constant;
import com.google.gson.Gson;

public class DataManager {
    public static DataManager dataManager = new DataManager();
    public SignUpModel signUpModel;
    public GetProfileModel getProfileModel;

    private static DataManager getInstance() {
        return dataManager;
    }


    public SignUpModel getSignUpModel(Context context) {
        String data = SessionManager.Companion.readString(context, Constant.Companion.getUSER_DATA(), "");
        Log.e("TAG USER DATA", data);
        signUpModel = new Gson().fromJson(data, SignUpModel.class);
        return signUpModel;
    }

    public GetProfileModel getGetProfileModel(Context context) {
        String data = SessionManager.Companion.readString(context, Constant.Companion.getProfileData(), "");
        getProfileModel = new Gson().fromJson(data, GetProfileModel.class);
        return getProfileModel;
    }
}
