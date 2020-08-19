package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class for shipping tab info
 */
public class ShippingTabFragment extends Fragment implements View.OnClickListener{
    private Context thisContext;
    private TextView mDebug;

    private JSONObject searchResultData;
    private JSONObject itemDetails;
    private TextView mNoResults;
    private LinearLayout mShippingTabResults;
    private TableLayout mSoldBySection;
    private TableRow mStoreNameRow;
    private TextView mStoreName;
    private TableRow mFeedbackScoreRow;
    private TextView mFeedbackScore;
    private String storeURL;
    private TableRow mPopularityRow;
    private CircularScoreView mPopularity;
    private TableRow mFeedbackStarRow;
    private ImageView mFeedbackStar;

    private TableLayout mShippingSection;
    private TableRow mShipCostRow;
    private TextView mShipCost;
    private TableRow mGlobalShipRow;
    private TextView mGlobalShip;
    private TableRow mHandlingRow;
    private TextView mHandling;
    private TableRow mConditionRow;
    private TextView mCondition;

    private TableLayout mReturnSection;
    private TableRow mPolicyRow;
    private TextView mPolicy;
    private TableRow mReturnsWithinRow;
    private TextView mReturnsWithin;
    private TableRow mRefundModeRow;
    private TextView mRefundMode;
    private TableRow mShippedByRow;
    private TextView mShippedBy;


    public ShippingTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shipping_tab, container, false);
        thisContext = container.getContext();

        mDebug = view.findViewById(R.id.fragment_shipping_debug);
        setViewVariables(view);

        mStoreName.setOnClickListener(this);

        // Get Finding API Item details from Activity
        String resultString = getArguments().getString("ProdDetails_item");
        try {
            searchResultData = new JSONObject(resultString);
        } catch (JSONException e) {
            e.printStackTrace();
            // no results
        }

        // if view reset, but itemDetails retained, fill in tab information
        if (itemDetails != null) {
            displayTabInformation();
        }

        return view;
    }

    /**
     * set member variables to hold view components
     * @param view
     */
    private void setViewVariables(View view) {
        mStoreName = view.findViewById(R.id.shipping_storeName);
        // underline store name
        mStoreName.setPaintFlags(mStoreName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mNoResults = view.findViewById(R.id.shipping_tab_noResults);
        mShippingTabResults = view.findViewById(R.id.shipping_tab_results);
        mSoldBySection = view.findViewById(R.id.shipping_soldBy_section);
        mStoreNameRow = view.findViewById(R.id.shipping_storeName_row);
        mFeedbackScoreRow = view.findViewById(R.id.shipping_feedbackScore_row);
        mFeedbackScore = view.findViewById(R.id.shipping_feedbackScore);
        mPopularityRow = view.findViewById(R.id.shipping_popularity_row);
        mPopularity = view.findViewById(R.id.shipping_popularity);
        mFeedbackStarRow = view.findViewById(R.id.shipping_feedbackStar_row);
        mFeedbackStar = view.findViewById(R.id.shipping_feedbackStar);

        mShippingSection = view.findViewById(R.id.shipping_shipping_section);
        mShipCostRow = view.findViewById(R.id.shipping_shipCost_row);
        mShipCost = view.findViewById(R.id.shipping_shipCost);
        mGlobalShipRow = view.findViewById(R.id.shipping_globalShip_row);
        mGlobalShip = view.findViewById(R.id.shipping_globalShip);
        mHandlingRow = view.findViewById(R.id.shipping_handling_row);
        mHandling = view.findViewById(R.id.shipping_handling);
        mConditionRow = view.findViewById(R.id.shipping_condition_row);
        mCondition = view.findViewById(R.id.shipping_condition);

        mReturnSection = view.findViewById(R.id.shipping_return_section);
        mPolicyRow = view.findViewById(R.id.shipping_policy_row);
        mPolicy = view.findViewById(R.id.shipping_policy);
        mReturnsWithinRow = view.findViewById(R.id.shipping_returnsWithin_row);
        mReturnsWithin = view.findViewById(R.id.shipping_returnsWithin);
        mRefundModeRow = view.findViewById(R.id.shipping_refundMode_row);
        mRefundMode = view.findViewById(R.id.shipping_refundMode);
        mShippedByRow = view.findViewById(R.id.shipping_shippedBy_row);
        mShippedBy = view.findViewById(R.id.shipping_shippedBy);
    }

    /**
     * Get Shopping API details from Activity (sent from Product Tab)
     * @param details item details object
     */
    public void putItemDetails(JSONObject details) {
        itemDetails = details;
        //mDebug.setText(itemDetails.toString());

        // fill in tab information
        if (itemDetails != null) {
            displayTabInformation();
        } else {
            mShippingTabResults.setVisibility(View.GONE);
            mNoResults.setVisibility(View.VISIBLE);
        }
    }

    private void displayTabInformation() {
        // Sold By Section
        boolean soldBySection = false;
        if (itemDetails.has("Storefront")) {
            storeURL = itemDetails.optJSONObject("Storefront").optString("StoreURL");
            String storeName = itemDetails.optJSONObject("Storefront").optString("StoreName");
            mStoreName.setText(storeName);
            soldBySection = true;
        } else {
            mStoreNameRow.setVisibility(View.GONE);
        }
        if (itemDetails.has("Seller")){
            int score = itemDetails.optJSONObject("Seller").optInt("FeedbackScore");
            mFeedbackScore.setText(String.valueOf(score));

            int popularity = itemDetails.optJSONObject("Seller").optInt("PositiveFeedbackPercent");
            mPopularity.setScore(popularity);

            String color = itemDetails.optJSONObject("Seller").optString("FeedbackRatingStar");
            colorFeedbackStar(color);
            soldBySection = true;
        } else {
            mFeedbackScoreRow.setVisibility(View.GONE);
            mFeedbackStarRow.setVisibility(View.GONE);
            mPopularityRow.setVisibility(View.GONE);
        }

        // no results in sold by section to display
        if (!soldBySection) {
            mSoldBySection.setVisibility(View.GONE);
        }

        // Shipping Info section
        boolean shippingSection = false;
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
            mShipCost.setText(item_shipping);
            shippingSection = true;
        }
        else {
            mShipCostRow.setVisibility(View.GONE);
        }

        if (itemDetails.has("GlobalShipping")) {
            if(itemDetails.optBoolean("GlobalShipping")){
                mGlobalShip.setText("Yes");
            } else {
                mGlobalShip.setText("No");
            }
            shippingSection = true;
        } else {
            mGlobalShipRow.setVisibility(View.GONE);
        }

        if (itemDetails.has("HandlingTime")){
            int days = itemDetails.optInt("HandlingTime");
            String handling;
            if (days < 2) {
                handling = days + " day";
            } else {
                handling = days + " days";
            }
            mHandling.setText(handling);
            shippingSection = true;
        } else {
            mHandlingRow.setVisibility(View.GONE);
        }

        if (itemDetails.has("ConditionDescription")){
            mCondition.setText(itemDetails.optString("ConditionDescription"));
            shippingSection = true;
        } else {
            mConditionRow.setVisibility(View.GONE);
        }

        if (!shippingSection) {
            mShippingSection.setVisibility(View.GONE);
        }

        // Returns section
        if (itemDetails.has("ReturnPolicy")) {
            JSONObject returns = itemDetails.optJSONObject("ReturnPolicy");
            if (returns.has("ReturnsAccepted")) {
                mPolicy.setText(returns.optString("ReturnsAccepted"));
            } else {
                mPolicyRow.setVisibility(View.GONE);
            }

            if (returns.has("ReturnsWithin")) {
                mReturnsWithin.setText(returns.optString("ReturnsWithin"));
            } else {
                mReturnsWithinRow.setVisibility(View.GONE);
            }

            if (returns.has("Refund")) {
                mRefundMode.setText(returns.optString("Refund"));
            } else {
                mRefundModeRow.setVisibility(View.GONE);
            }

            if (returns.has("ShippingCostPaidBy")){
                mShippedBy.setText(returns.optString("ShippingCostPaidBy"));
            } else {
                mShippedByRow.setVisibility(View.GONE);
            }
        } else {
            mReturnSection.setVisibility(View.GONE);
        }

    }

    private void colorFeedbackStar(String color) {
        if (color.contains("Shooting")) { // if shooting star feedback, change to different star type
        mFeedbackStar.setImageDrawable(ContextCompat.getDrawable(thisContext, R.drawable.star_circle));
        }

        if (color.contains("Blue")) {
            mFeedbackStar.getDrawable().setTint(Color.BLUE);
        } else if (color.contains("Green")) {
            mFeedbackStar.getDrawable().setTint(Color.GREEN);
        } else if (color.contains("Purple")) {
            int purple = ContextCompat.getColor(thisContext, R.color.purple);
            mFeedbackStar.getDrawable().setTint(purple);
        } else if (color.contains("Red")) {
            mFeedbackStar.getDrawable().setTint(Color.RED);
        } else if (color.contains("Silver")) {
            mFeedbackStar.getDrawable().setTint(Color.GRAY);
        } else if (color.contains("Turquoise")) {
            int turquoise = ContextCompat.getColor(thisContext, R.color.turquoise);
            mFeedbackStar.getDrawable().setTint(turquoise);
        } else if (color.contains("Yellow")) {
            mFeedbackStar.getDrawable().setTint(Color.YELLOW);
        } else {
            mFeedbackStar.getDrawable().setTint(Color.BLACK);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shipping_storeName:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeURL));
                if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                else {
                    Log.d("FacebookShare", "Error opening Facebook Dialog");
                }
                break;
        }
    }
}
