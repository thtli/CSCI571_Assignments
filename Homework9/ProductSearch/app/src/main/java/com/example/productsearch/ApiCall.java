package com.example.productsearch;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class to make all API calls to node.js & ip-api
 */
public class ApiCall {
    private static ApiCall mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    public ApiCall(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ApiCall getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new ApiCall(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String API, String query, Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener) {
        String encodedQuery = null;
        String url;
        try {
            encodedQuery = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedQuery = "";
        }
        //switch case for each API
        switch (API) {

            case "geonames":
                url = "https://csci-571-nodejs-project.appspot.com/geonames/zip=" + encodedQuery;
                break;
            case "ip-api":
                url = "http://ip-api.com/" + query;
                break;
            case "productsearch":
                url = "https://csci-571-nodejs-project.appspot.com/productsearch/" + query;
                break;
            case "productdetail":
                url = "https://csci-571-nodejs-project.appspot.com/productdetail/id=" + query;
                break;
            case "imagesearch":
                url = "https://csci-571-nodejs-project.appspot.com/imagesearch/product=" + encodedQuery;
                break;
            case "similaritems":
                url = "https://csci-571-nodejs-project.appspot.com/similaritems/id=" + query;
                break;
            default:
                url = "";

        }

        //String url = "https://itunes.apple.com/search?term=" + query + "&country=US";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        ApiCall.getInstance(ctx).addToRequestQueue(jsonObjectRequest);
    }

}
