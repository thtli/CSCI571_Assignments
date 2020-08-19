package com.example.productsearch;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class for the Product (Information) Fragment that displays from PRODUCT tab
 */
public class ProductTabFragment extends Fragment {
    // product info variables
    private JSONObject searchResultData;  //from search results / activity
    private String itemID;
    private JSONObject itemDetails; // from shopping API, called during fragment creation
    private SendItemDetails sendItemDetails;

    // member variables to hold views from fragment layout
    private Context thisContext;
    private TextView mDebug;
    private LinearLayout mProgressBar;
    private ScrollView mDetailResults;
    private TextView mNoResults;

    private TextView mTitle;
    private TextView mPrice;
    private TextView mShipping;
    private RelativeLayout mHighlights;
    private TextView mSubtitleLabel;
    private TextView mSubtitle;
    private TextView mPriceLabel;
    private TextView mHighlightsPrice;
    private TextView mBrandLabel;
    private TextView mBrand;
    private RelativeLayout mSpecifications;
    private TextView mSpecText;

    //gallery
    private LinearLayout mGallery;
    private LayoutInflater mInflater;


    public ProductTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_tab, container, false);

        // set views
        thisContext = container.getContext();
        setViewVariables(view);

        // Get Item details from Activity
        String resultString = getArguments().getString("ProdDetails_item");

        //call private method to get item ID (for use in API call)
        getItemID(resultString);

        //DEBUG
        //mDebug.setVisibility(View.VISIBLE);
        mDebug.setText("Product Tab\n" + itemID);


        //start getting data (product details) from API call
        mDetailResults.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        // API call to get item details from backend
        ApiCall.make(thisContext, "productdetail", itemID,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // get results from shopping API
                        if (response.has("Item")) {
                           itemDetails = response.optJSONObject("Item");
                            sendItemDetails.sendData(itemDetails);
                           // find in product tab information
                           createGalleryView();
                           createDetailsView();
                           mDetailResults.setVisibility(View.VISIBLE);
                        }
                        else {
                            // no results
                            mNoResults.setVisibility(View.VISIBLE);
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // no results
                        mProgressBar.setVisibility(View.GONE);
                        mDetailResults.setVisibility(View.GONE);
                        mNoResults.setVisibility(View.VISIBLE);
                    }
                });


        return view;
    }

    /**
     * interface for sending item details data between fragments and Activity
     */
    public interface SendItemDetails {
        void sendData(JSONObject details);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            sendItemDetails = (SendItemDetails) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error: Activity must implement interface");
        }
    }

    /**
     * Get all the views items inside the fragment
     * @param view fragment
     */
    private void setViewVariables(View view) {
        mProgressBar = view.findViewById(R.id.detail_progressBar_container);
        mDetailResults = view.findViewById(R.id.product_tab_results);
        mNoResults = view.findViewById(R.id.product_tab_noResults);
        mGallery = view.findViewById(R.id.item_gallery);

        //DEBUG
        mDebug = view.findViewById(R.id.fragment_product_debug);

        // Title section
        mTitle = view.findViewById(R.id.product_tab_title);
        mPrice = view.findViewById(R.id.product_tab_title_price);
        mShipping = view.findViewById(R.id.product_tab_title_shipping);
        //highlights section
        mHighlights = view.findViewById(R.id.product_tab_highlights);
        mSubtitleLabel = view.findViewById(R.id.highlights_subtitle_label);
        mSubtitle = view.findViewById(R.id.highlights_subtitle);
        mPriceLabel = view.findViewById(R.id.highlights_price_label);
        mHighlightsPrice = view.findViewById(R.id.highlights_price);
        mBrandLabel = view.findViewById(R.id.highlights_brand_label);
        mBrand = view.findViewById(R.id.highlights_brand);
        //specifications section
        mSpecifications = view.findViewById(R.id.product_tab_specifications);
        mSpecText = view.findViewById(R.id.specification_text);
    }

    /**
     * private method to get the item id from the json object string
     * @param json string of json object
     */
    private void getItemID(String json) {
        try {
            searchResultData = new JSONObject(json);
            //get ID
            itemID = searchResultData.getJSONArray("itemId").getString(0);
        } catch (JSONException e) {
            e.printStackTrace();
            //no results
        }
    }

    /**
     * Get picture URLs from item details and display in a horizontal scroll view
     */
    private void createGalleryView() {
        mInflater = LayoutInflater.from(thisContext);
        // Get picture urls array from item details and display in horizontal scroll view
        if (itemDetails.has("PictureURL")) {
            JSONArray pictureURL = itemDetails.optJSONArray("PictureURL");

            for (int i = 0; i < pictureURL.length(); i++) {
                View view = mInflater.inflate(R.layout.detail_gallery_item, mGallery, false);
                ImageView itemImage = view.findViewById(R.id.gallery_item_image);
                Glide.with(thisContext).load(pictureURL.optString(i)).error(R.drawable.not_found).into(itemImage);
                mGallery.addView(view);
            }
        } else {
            mGallery.setVisibility(View.GONE);
        }
    }

    /**
     * private method to fill in product tab fragment with relevant product detail information
     */
    private void createDetailsView() {
        boolean hasHighlights = false; //determine whether to hide highlights section

        // title ( + price + shipping)
        if (itemDetails.has("Title")) {
            String title = itemDetails.optString("Title");
            mTitle.setText(title);
        } else {
            mTitle.setVisibility(View.GONE);
        }
        if (itemDetails.has("CurrentPrice")) {
            String price = "$";
            price += itemDetails.optJSONObject("CurrentPrice").optString("Value");
            mPrice.setText(price);
            mHighlightsPrice.setText(price);
            hasHighlights = true;
        } else {
            mPrice.setText("N/A");
            mHighlightsPrice.setVisibility(View.GONE);
            mPriceLabel.setVisibility(View.GONE);
        }
        //shipping
        if (searchResultData.has("shippingInfo") &&
                searchResultData.optJSONArray("shippingInfo").optJSONObject(0).has("shippingServiceCost")) {
            String item_shipping = searchResultData.optJSONArray("shippingInfo")
                    .optJSONObject(0)
                    .optJSONArray("shippingServiceCost")
                    .optJSONObject(0)
                    .optString("__value__", "N/A");

            //if cost is $0.0, set string to be Free Shipping
            if (item_shipping.equals("0.0")) {
                item_shipping = "Free Shipping";
            } else if (!item_shipping.equals("N/A")) {
                item_shipping = "$" + item_shipping;
            }
            mShipping.setText(item_shipping);
        }
        else {
            mShipping.setText("N/A");
        }
        //highlights
        //subtitle
        if (itemDetails.has("Subtitle")) {
            String subtitle = itemDetails.optString("Subtitle");
            mSubtitle.setText(subtitle);
            hasHighlights = true;
        } else {
            mSubtitleLabel.setVisibility(View.GONE);
            mSubtitle.setVisibility(View.GONE);
        }
        //price was done with title

        //specifications
        if (itemDetails.has("ItemSpecifics") && itemDetails.optJSONObject("ItemSpecifics").has("NameValueList")) {
            JSONArray array = itemDetails.optJSONObject("ItemSpecifics")
                    .optJSONArray("NameValueList");
            String specList = "";
            for (int i = 0; i < array.length(); i++) {
                JSONObject current = array.optJSONObject(i);
                String current_spec = current.optJSONArray("Value").optString(0);

                if(current.optString("Name").equals("Brand")) {
                    specList = "\u2022" + current_spec + "\n" + specList;
                    hasHighlights = true;
                    mBrand.setText(current_spec);
                }
                else {
                    specList += "\u2022" + current_spec + "\n";
                }
            }
            mSpecText.setText(specList);
        } else {
            mSpecifications.setVisibility(View.GONE);
            mBrandLabel.setVisibility(View.GONE);
            mBrand.setVisibility(View.GONE);
        }

    }
}
