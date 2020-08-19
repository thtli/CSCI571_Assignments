package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Class for Similar Tab
 */
public class SimilarTabFragment extends Fragment {
    private TextView mDebug;

    private Context thisContext;
    private TextView mNoResults;
    private RecyclerView mResults;
    private LinearLayout mProgressBar;
    private Spinner mSortBySpinner;
    private Spinner mAscDescSpinner;
    private SimilarItemsAdapter simItemsAdapter;

    private JSONObject searchResultData;
    private String itemID;
    private JSONArray similarItemsResult;
    private List<SearchItem> itemsList;
    private List<SearchItem> defaultSortList;
    private String sortTypeSelected;
    private String ascDescSelected;

    public SimilarTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_similar_tab, container, false);
        thisContext = container.getContext();
        setViewVariables(view);

        // Get Item details from Activity
        String resultString = getArguments().getString("ProdDetails_item");
        getItemID(resultString);
        setUpSpinners();
        //default sort selections
        sortTypeSelected = "Default";
        ascDescSelected = "Ascending";


        mDebug = view.findViewById(R.id.fragment_similar_debug);
        mDebug.setText("Similar Tab\n" + itemID);

        //start getting data (product details) from API call
        mResults.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        // API call to get item details from backend
        ApiCall.make(thisContext, "similaritems", itemID,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // get results from shopping API
                        try {
                            similarItemsResult = response.getJSONObject("getSimilarItemsResponse")
                                    .getJSONObject("itemRecommendations")
                                    .getJSONArray("item");

                            if (similarItemsResult.length() > 0) {
                                createSearchItemList();
                                createSimilarItemsRecyclerView();
                                mResults.setVisibility(View.VISIBLE);
                                setUpSpinnerListeners();

                                //enable sortBy spinner if there are results
                                // when default is selected, asc/desc spinner is disabled
                                mSortBySpinner.setEnabled(true);
                            }
                            else {
                                // no results
                                mResults.setVisibility(View.GONE);
                                mNoResults.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // no results or error
                            mResults.setVisibility(View.GONE);
                            mNoResults.setVisibility(View.VISIBLE);
                        }

                        mProgressBar.setVisibility(View.GONE);
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressBar.setVisibility(View.GONE);
                        mResults.setVisibility(View.GONE);
                        mNoResults.setVisibility(View.VISIBLE);
                    }
                });


        return view;
    }



    /**
     * private method to add all result items from API to SearchItem class
     */
    private void createSearchItemList() {
        itemsList = new ArrayList<SearchItem>();
        for (int i = 0; i < similarItemsResult.length(); i++) {
            SearchItem currentItem;
            try {
                JSONObject item = similarItemsResult.getJSONObject(i);

                //title
                String title = item.optString("title","N/A");

                //shipping
                String shipping = "N/A";
                if (item.has("shippingCost")) {
                    shipping = item.optJSONObject("shippingCost").optString("__value__", "N/A");
                }

                //days left
                String daysLeft = "N/A";
                if (item.has("timeLeft")){
                    String days = item.getString("timeLeft");
                    daysLeft = days.substring(1,days.indexOf('D'));
                }

                // Price
                String price = "N/A";
                if (item.has("buyItNowPrice")){
                    price = item.getJSONObject("buyItNowPrice").optString("__value__", "N/A");
                }

                // item URL
                String itemURL = item.optString("viewItemURL", "N/A");

                // picture URL
                String pictureURL = item.optString("imageURL", "N/A");

                currentItem = new SearchItem(title, shipping, daysLeft, price, itemURL, pictureURL);
                itemsList.add(currentItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        defaultSortList = new ArrayList<>(itemsList); // use to return to default sort
    }

    private void createSimilarItemsRecyclerView() {
        mResults.setLayoutManager(new LinearLayoutManager(thisContext));
        simItemsAdapter = new SimilarItemsAdapter(thisContext, itemsList);
        mResults.setAdapter(simItemsAdapter);
    }

    private void setViewVariables(View view) {
        mNoResults = view.findViewById(R.id.simItems_tab_noResults);
        mResults = view.findViewById(R.id.simItems_recyclerView_results);
        mProgressBar = view.findViewById(R.id.simItems_progressBar_container);
        mSortBySpinner = view.findViewById(R.id.simItems_sortBy_spinner);
        mAscDescSpinner = view.findViewById(R.id.simItems_ascDes_spinner);

        // disable spinners by default (enable upon results)
        mSortBySpinner.setEnabled(false);
        mAscDescSpinner.setEnabled(false);
    }

    private void setUpSpinners() {
        // Sort By (Sort Type) Spinner
        // create array adapter using string array
        ArrayAdapter<CharSequence> sortType_adapter = ArrayAdapter.createFromResource(
                thisContext, R.array.sortType_array, android.R.layout.simple_spinner_item);
        // specify the layout to use for dropdown appearance
        sortType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortBySpinner.setAdapter(sortType_adapter);


        // Ascending/ Descending Spinner
        // create array adapter using string array
        ArrayAdapter<CharSequence> ascDesc_adapter = ArrayAdapter.createFromResource(
                thisContext, R.array.ascendDescend_array, android.R.layout.simple_spinner_item);
        // specify the layout to use for dropdown appearance
        ascDesc_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAscDescSpinner.setAdapter(ascDesc_adapter);

    }

    /**
     * Private method for setting up the OnItemSelected Listeners for the Spinners
     * only run when there are results from API call
     * so that there are items to be selected (and array list is available)
     */
    private void setUpSpinnerListeners() {
        // Sort By spinner
        mSortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // sort
                switch (position) {
                    case 0: //default
                        sortTypeSelected = "Default";
                        // set the list back to its default by copying values of default list
                        Collections.copy(itemsList,defaultSortList);
                        mAscDescSpinner.setEnabled(false);
                        break;
                    case 1: //name
                        sortTypeSelected = "Name";
                        mAscDescSpinner.setEnabled(true);

                        Collections.sort(itemsList, new Comparator<SearchItem>() {
                            @Override
                            public int compare(SearchItem o1, SearchItem o2) {
                                if (ascDescSelected.equals("Ascending")) {
                                    return o1.getItemTitle().compareTo(o2.getItemTitle());
                                } else {
                                    return o2.getItemTitle().compareTo(o1.getItemTitle());
                                }
                            }
                        });
                        break;
                    case 2: //price
                        sortTypeSelected = "Price";
                        mAscDescSpinner.setEnabled(true);

                        Collections.sort(itemsList, new Comparator<SearchItem>() {
                            @Override
                            public int compare(SearchItem o1, SearchItem o2) {
                                double num1 = Double.parseDouble(o1.getItemPrice());
                                double num2 = Double.parseDouble(o2.getItemPrice());
                                if (ascDescSelected.equals("Ascending")){
                                    return (int)Math.round(num1 - num2);
                                } else {
                                    return (int)Math.round(num2 - num1);
                                }
                            }
                        });
                        break;
                    case 3: //days
                        sortTypeSelected = "Days";
                        mAscDescSpinner.setEnabled(true);

                        Collections.sort(itemsList, new Comparator<SearchItem>() {
                            @Override
                            public int compare(SearchItem o1, SearchItem o2) {
                                int int1 = Integer.parseInt(o1.getItemDaysLeft());
                                int int2 = Integer.parseInt(o2.getItemDaysLeft());
                                if (ascDescSelected.equals("Ascending")){
                                    return int1 - int2;
                                } else {
                                    return int2 - int1;
                                }
                            }
                        });
                        break;
                }
                //need to notify adapter that the list (data set) has changed
                simItemsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Ascending/Descending Spinner
        mAscDescSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //DEBUG categorySelected = parent.getItemAtPosition(position).toString();
                // set categorySelected to be the short forms for API call based on selection
                switch (position) {
                    case 0: //ascending
                        ascDescSelected = "Ascending";
                        break;
                    case 1: //descending
                        ascDescSelected = "Descending";
                        break;
                }

                Collections.reverse(itemsList);
                //need to notify adapter that the list (data set) has changed
                simItemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
}

/**
 * Similar Items Adapter class for recycler view to display search results
 */
class SimilarItemsAdapter extends RecyclerView.Adapter<SimilarItemsAdapter.ImageViewHolder> {
    private Context mContext;
    private List<SearchItem> results;

    public SimilarItemsAdapter(Context context, List<SearchItem> results) {
        mContext = context;
        this.results = results;
    }

    @NonNull
    @Override
    public SimilarItemsAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // inflating and returning view holder
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.similar_items_card, null);
        return new SimilarItemsAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarItemsAdapter.ImageViewHolder imageViewHolder, int i) {
            SearchItem item = results.get(i);

            //title
            imageViewHolder.title.setText(item.getItemTitle());

            //shipping
            String shipping = item.getItemShipping();
            if (shipping.equals("0.00")){
                shipping = "Free Shipping";
            }
            else if (!shipping.equals("N/A")){
                shipping = "$" + shipping;
            }
            imageViewHolder.shipping.setText(shipping);

            //days left
            String daysLeft = item.getItemDaysLeft();
            if (Integer.parseInt(daysLeft) < 2) {
                daysLeft = daysLeft + " Day Left";
            } else if (!daysLeft.equals("N/A")) {
                daysLeft = daysLeft + " Days Left";
            }
            imageViewHolder.daysLeft.setText(daysLeft);

            // Price
            String price = item.getItemPrice();
            if (!price.equals("N/A")){
                price = "$" + price;
            }
            imageViewHolder.price.setText(price);

            // picture
            String url = item.getPictureURL();
            if (!url.equals("N/A")) {
                Glide.with(mContext).load(url).error(R.drawable.not_found).into(imageViewHolder.imageView);
            }

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView title, shipping, daysLeft, price;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.simItems_title);
            shipping = itemView.findViewById(R.id.simItems_shipping);
            daysLeft = itemView.findViewById(R.id.simItems_daysLeft);
            price = itemView.findViewById(R.id.simItems_price);
            imageView = itemView.findViewById(R.id.simItems_imageView);

            //set onClickListener to entire view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // get selected item
            SearchItem selectedItem = results.get(getAdapterPosition());
            String url = selectedItem.getItemURL();
            if (!url.equals("N/A")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if(intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
                else {
                    Log.d("similarItems", "Error opening similar Item");
                }
            }

        }
    }
}
