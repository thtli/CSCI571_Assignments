package com.example.productsearch;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


/**
 * Class for Google tab
 */
public class GoogleTabFragment extends Fragment {
    private TextView mDebug;
    private Context thisContext;

    private TextView mNoResults;
    private RecyclerView mResults;
    private LinearLayout mProgressBar;

    private String itemTitle;
    private JSONArray googleSearchResults;

    public GoogleTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_tab, container, false);
        thisContext = container.getContext();
        setViewVariables(view);

        mDebug = view.findViewById(R.id.fragment_google_debug);

        // Get Item details from Activity
        itemTitle = getArguments().getString("ProdDetails_title");
        //mDebug.setText("Google Tab\n" + itemTitle);

        //start getting data (product details) from API call
        mResults.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        // API call to get item details from backend
        ApiCall.make(thisContext, "imagesearch", itemTitle.substring(0,20),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // get results from shopping API
                        if (response.has("items")) {
                            googleSearchResults = response.optJSONArray("items");
                            mResults.setVisibility(View.VISIBLE);
                            createGalleryView();
                        }
                        else {
                            // no results
                            mResults.setVisibility(View.GONE);
                            mNoResults.setVisibility(View.VISIBLE);
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // no results
                        mProgressBar.setVisibility(View.GONE);
                        mResults.setVisibility(View.GONE);
                        mNoResults.setVisibility(View.VISIBLE);
                    }
                });


        return view;
    }

    /**
     * Private method to set up google photos recycler view
     * sets up layout manager and adapter
     */
    private void createGalleryView() {
        mResults.setHasFixedSize(true);
        mResults.setLayoutManager(new LinearLayoutManager(thisContext));
        GoogleAdapter adapter = new GoogleAdapter(thisContext, googleSearchResults);
        mResults.setAdapter(adapter);
    }

    /**
     * set variables to relevant views
     * @param view view of fragment
     */
    private void setViewVariables(View view) {
        mProgressBar = view.findViewById(R.id.google_progressBar_container);
        mResults = view.findViewById(R.id.google_recycler_results);
        mNoResults = view.findViewById(R.id.google_tab_noResults);
    }
}

/**
 * Google Adapter class for recycler view to display search results
 */
class GoogleAdapter extends RecyclerView.Adapter<GoogleAdapter.ImageViewHolder> {
    private Context mContext;
    private JSONArray results;

    // Constructor to get contest and list of google images
    public GoogleAdapter(Context mContext, JSONArray results) {
        this.mContext = mContext;
        this.results = results;
    }


    @NonNull
    @Override
    public GoogleAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // inflating and returning view holder
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.google_image_card, null);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoogleAdapter.ImageViewHolder imageViewHolder, int i) {
        //getting image at specified position
        String url = results.optJSONObject(i).optString("link");
        Glide.with(mContext).load(url).error(R.drawable.not_found).into(imageViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return results.length();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.google_gallery_imageView);
        }
    }
}
