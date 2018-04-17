package com.example.hang.googletranslate;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HanG on 2/4/2018.
 */

public class Translator {
    private String key = "AIzaSyAy52ilPeKqEhJ_zjwfBLbnvnykB1FDTPw";
    private String targetLang;
    private String oriTxt;
    private JSONArray info;
    private RequestQueue rq;
    private Context context;

    public Translator(Context context){
        this.context = context;
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    public void translate(String oriTxt, String targetLang,final VolleyCallback callback){
        rq = Volley.newRequestQueue(context);
        String urlString = "https://translation.googleapis.com/language/translate/v2?key=" + key + "&q=" + oriTxt + "&target=" + targetLang;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlString, null, new Response.Listener<JSONObject>() {
        String transTxt = null;
            @Override
            public void onResponse(JSONObject response) {
                try {
                    info = response.getJSONObject("data").getJSONArray("translations");
                    JSONObject jsonObject = info.getJSONObject(0);
                    transTxt = jsonObject.getString("translatedText");
                    Log.e("transTxt", jsonObject.getString("translatedText"));
                    if (transTxt.contains("&#39;")) {
                        transTxt = transTxt.replaceAll("&#39;", "'");
                    }
                    callback.onSuccess(transTxt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(jsonObjectRequest);
    }
}
