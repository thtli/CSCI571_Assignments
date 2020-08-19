package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder> {
    //context used to inflate the layout
    private Context mContext;

    //stored array of search results
    private JSONArray results;
    // sharedPref that stores wishlist for app
    private SharedPreferences sharedPref;


    /** constructor for Search Result Adapter to get context and search results
     * @param context context
     * @param searchResults JSON array of search results
     */
    public SearchResultAdapter(Context context, JSONArray searchResults) {
        mContext = context;
        results = searchResults;
        sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.sharedPreference_key), Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public SearchResultAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // inflating and returning the view holder
        LayoutInflater inflater = LayoutInflater.from(mContext);
        // tutorial has input args as (R.layout.layout_result_card, viewGroup, false)
        View view = inflater.inflate(R.layout.layout_result_card, null);
        return new ResultViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ResultViewHolder resultViewHolder, int position) {
        try {
            // get the item at specified position
            JSONObject item = results.getJSONObject(position);

            // binding data with view holder views
            //Title
            if (item.has("title")) {
                String item_title = item.getJSONArray("title").getString(0);
                resultViewHolder.title.setText(item_title);
            } else {
                resultViewHolder.title.setText("N/A");
            }

            //Zipcode
            if (item.has("postalCode")){
                String item_zipcode = "Zip: ";
                item_zipcode += item.getJSONArray("postalCode").getString(0);
                resultViewHolder.zipcode.setText(item_zipcode);
            }
            else {
                resultViewHolder.zipcode.setText("N/A");
            }

            //shipping
            if (item.has("shippingInfo") &&
                    item.optJSONArray("shippingInfo").getJSONObject(0).has("shippingServiceCost")) {
                String item_shipping = item.getJSONArray("shippingInfo")
                        .getJSONObject(0)
                        .optJSONArray("shippingServiceCost")
                        .optJSONObject(0)
                        .optString("__value__", "N/A");

                //if cost is $0.0, set string to be Free Shipping
                if (item_shipping.equals("0.0")) {
                    item_shipping = "Free Shipping";
                } else if (!item_shipping.equals("N/A")) {
                    item_shipping = "$" + item_shipping;
                }
                resultViewHolder.shipping.setText(item_shipping);
            }
            else {
                resultViewHolder.shipping.setText("N/A");
            }

            // Condition
            if (item.has("condition") &&
                    item.optJSONArray("condition").getJSONObject(0).has("conditionDisplayName")){
                String item_condition = item.getJSONArray("condition")
                        .getJSONObject(0)
                        .optJSONArray("conditionDisplayName")
                        .optString(0, "N/A");
                //TODO: fit in one line
                /*
                if (item_condition.contains("refurbished")){
                    item_condition = "Refurbished";
                }*/
                resultViewHolder.condition.setText(item_condition);
            }
            else {
                resultViewHolder.condition.setText("N/A");
            }

            // price
            if (item.has("sellingStatus") &&
                    item.optJSONArray("sellingStatus").getJSONObject(0).has("currentPrice")) {
                String item_price = "$";
                item_price += item.getJSONArray("sellingStatus")
                        .getJSONObject(0)
                        .optJSONArray("currentPrice")
                        .optJSONObject(0)
                        .optString("__value__", "N/A");

                resultViewHolder.price.setText(item_price);
            }
            else {
                resultViewHolder.price.setText("N/A");
            }

            // Product Image
            if (item.has("galleryURL")) {
                String url = item.getJSONArray("galleryURL").getString(0);

                Glide.with(mContext).load(url).error(R.drawable.not_found).into(resultViewHolder.productImage);
            }

            //wishlist
            String itemID = item.optJSONArray("itemId").optString(0);
            if (searchWishList(itemID) == -1){
                // if item is not in wishlist, image is add cart
                resultViewHolder.wishlistButton.setImageResource(R.drawable.cart_plus);
            } else {
                // if item is already in wishlist, image is remove cart
                resultViewHolder.wishlistButton.setImageResource(R.drawable.cart_remove);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search Wishlist SharedPreferences for specific item
     * @param itemID id string of item
     * @return index of item in JSONArray of wishlist
     */
    private int searchWishList(String itemID) {
        String wishlistString = sharedPref.getString("wishlist","[]");

            try {
                // convert JSON string into JSON array (of objects for each item)
                JSONArray wishlistArray = new JSONArray(wishlistString);
                for (int i = 0; i < wishlistArray.length(); i++) {
                    String currentID = wishlistArray.getJSONObject(i).optJSONArray("itemId").optString(0);
                    if (itemID.equals(currentID)) {
                        return i;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return -1;

    }

    /**
     * update Wishlist in SharedPreference according to whether item object already in wishlist
     * if item already in wishlist, will remove; if item not in wishlist, will add
     * @param item JSONobject of item
     */
    private void wishListUpdate(JSONObject item) {
        String itemID = item.optJSONArray("itemId").optString(0);

        SharedPreferences.Editor editor = sharedPref.edit();
        String wishlistString = sharedPref.getString("wishlist","[]");

        try {
            // convert JSON string into JSON array (of objects for each item)
            JSONArray wishlistArray = new JSONArray(wishlistString);

            // if item not in wishlist, add to wishlist
            if (searchWishList(itemID) == -1) {
                wishlistArray.put(item);
            }

            // if item already in wishlist, remove from wishlist
            else {
                wishlistArray.remove(searchWishList(itemID));
            }

            editor.putString("wishlist", wishlistArray.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return results.length();
    }

    /**
     * ViewHolder class that represents single item result in the RecyclerVIew
     */
    public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // All views within card layout that need to be filled with later
        ImageView productImage, wishlistButton;
        TextView title, zipcode, shipping, condition, price;
        SearchResultAdapter adapter;

        public ResultViewHolder(@NonNull View itemView, SearchResultAdapter adapter) {
            super(itemView);
            this.adapter = adapter;

            //initialize the views
            productImage = itemView.findViewById(R.id.card_product_image);
            title = itemView.findViewById(R.id.card_product_title);
            zipcode = itemView.findViewById(R.id.card_product_zipcode);
            shipping = itemView.findViewById(R.id.card_product_shipping);
            condition = itemView.findViewById(R.id.card_product_condition);
            price = itemView.findViewById(R.id.card_product_price);
            wishlistButton = itemView.findViewById(R.id.card_product_wishlist_button);

            //set onClickListener to entire view
            itemView.setOnClickListener(this);
            wishlistButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // get selected item
            JSONObject selectedItem;
            String itemID ="";
            try {
                // get item details for the selected position
                selectedItem = results.getJSONObject(getAdapterPosition());
                itemID = selectedItem.getJSONArray("itemId").getString(0);


                // if wishlist button clicked
                if (v.getId() == wishlistButton.getId()) {

                    String item_title = selectedItem.getJSONArray("title").getString(0);
                    String message = item_title;

                    // shorten title if it is too long
                    if (item_title.length() > 40) {
                        message = item_title.substring(0,40) + "...";
                    }

                    // item not in wish list yet, add to wish list
                    if (searchWishList(itemID) == -1) {
                        message += " was added to wish list";
                    }
                    else {
                        // item already in wishlist, click removes from wishlist
                        message += "was removed from wish list";
                    }

                    wishListUpdate(selectedItem);
                    // wishlist changed, notify adapter to update wl buttons
                    adapter.notifyDataSetChanged();

                    // show toast with appropriate message
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

                }

                // if card view is clicked
                else {
                    Intent detailIntent = new Intent(mContext, ProductDetailsActivity.class);
                    detailIntent.putExtra("productDetail", selectedItem.toString());
                    mContext.startActivity(detailIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
