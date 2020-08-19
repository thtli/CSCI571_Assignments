package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.TestLooperManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.WishViewHolder> {
    private Context mContext;
    private JSONArray wishlist;
    private SharedPreferences sharedPref;
    private View wishlistView;


    public WishListAdapter(Context context, View wishlistView) {
        mContext = context;
        this.wishlistView = wishlistView;
        sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.sharedPreference_key), Context.MODE_PRIVATE);
        String wishlistString = sharedPref.getString("wishlist","[]");

        try {
            wishlist = new JSONArray(wishlistString);
        } catch (JSONException e) {
            e.printStackTrace();
            wishlist = new JSONArray();
        }

        updateWishListView();
    }

    @NonNull
    @Override
    public WishListAdapter.WishViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate and return view holder
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_result_card, null);
        return new WishViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.WishViewHolder wishViewHolder, int i) {
        try {
            // get the item at specified position
            JSONObject item = wishlist.getJSONObject(i);

            // binding data with view holder views
            //Title
            if (item.has("title")) {
                String item_title = item.getJSONArray("title").getString(0);
                wishViewHolder.title.setText(item_title);
            } else {
                wishViewHolder.title.setText("N/A");
            }

            //Zipcode
            if (item.has("postalCode")){
                String item_zipcode = "Zip: ";
                item_zipcode += item.getJSONArray("postalCode").getString(0);
                wishViewHolder.zipcode.setText(item_zipcode);
            }
            else {
                wishViewHolder.zipcode.setText("N/A");
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
                wishViewHolder.shipping.setText(item_shipping);
            }
            else {
                wishViewHolder.shipping.setText("N/A");
            }

            // Condition
            if (item.has("condition") &&
                    item.optJSONArray("condition").getJSONObject(0).has("conditionDisplayName")){
                String item_condition = item.getJSONArray("condition")
                        .getJSONObject(0)
                        .optJSONArray("conditionDisplayName")
                        .optString(0, "N/A");
                /* TODO: refurbished edit out
                if (item_condition.contains("refurbished")){
                    item_condition = "Refurbished";
                }*/
                wishViewHolder.condition.setText(item_condition);
            }
            else {
                wishViewHolder.condition.setText("N/A");
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

                wishViewHolder.price.setText(item_price);
            }
            else {
                wishViewHolder.price.setText("N/A");
            }

            // Product Image
            if (item.has("galleryURL")) {
                String url = item.getJSONArray("galleryURL").getString(0);

                Glide.with(mContext).load(url).error(R.drawable.not_found).into(wishViewHolder.productImage);
            }

            //wishlist
            String itemID = item.optJSONArray("itemId").optString(0);
            if (searchWishList(itemID) == -1){
                // if item is not in wishlist, image is add cart
                wishViewHolder.wishlistButton.setImageResource(R.drawable.cart_plus);
            } else {
                // if item is already in wishlist, image is remove cart
                wishViewHolder.wishlistButton.setImageResource(R.drawable.cart_remove);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the wishlistView: updates the description banner message at the bottom
     * with the count and total price, and the appearance of the no wishes message
     */
    private void updateWishListView() {
        TextView mCount = wishlistView.findViewById(R.id.wishlist_count);
        TextView mTotalPrice = wishlistView.findViewById(R.id.wishlist_totalPrice);
        RecyclerView mWishes = wishlistView.findViewById(R.id.wishlist_recyclerView_items);
        TextView mNoWishes = wishlistView.findViewById(R.id.no_wishes);

        String message ="Wishlist total("+ wishlist.length();
        if (wishlist.length() == 1) {
            message +=" item):";
        } else {
            message += " items):";
        }

        mCount.setText(message);
        double total = 0.0;

        if (wishlist.length() == 0) {
            mWishes.setVisibility(View.GONE);
            mNoWishes.setVisibility(View.VISIBLE);
        } else {
            for (int j = 0; j < wishlist.length(); j++) {
                JSONObject item = wishlist.optJSONObject(j);
                if (item.has("sellingStatus") &&
                        item.optJSONArray("sellingStatus").optJSONObject(0).has("currentPrice")) {
                    String currentPrice = item.optJSONArray("sellingStatus")
                            .optJSONObject(0)
                            .optJSONArray("currentPrice")
                            .optJSONObject(0)
                            .optString("__value__", "0");

                    total += Double.parseDouble(currentPrice);
                }
            }

            mWishes.setVisibility(View.VISIBLE);
            mNoWishes.setVisibility(View.GONE);
        }

        String totalString = "$" + String.format(Locale.US,"%.2f", total);
        mTotalPrice.setText(totalString);
    }

    /**
     * Search Wishlist SharedPreferences for specific item
     * @param itemID string of item id
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

        // if item not in wishlist, add to wishlist
        if (searchWishList(itemID) == -1) {
            wishlist.put(item);
        }

        // if item already in wishlist, remove from wishlist
        else {
            wishlist.remove(searchWishList(itemID));
        }

        editor.putString("wishlist", wishlist.toString());

        editor.apply();
    }

    @Override
    public int getItemCount() {
        return wishlist.length();
    }

    public class WishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // All views within card layout that need to be filled with later
        ImageView productImage, wishlistButton;
        TextView title, zipcode, shipping, condition, price;
        WishListAdapter adapter;

        public WishViewHolder(@NonNull View itemView, WishListAdapter adapter) {
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
                selectedItem = wishlist.getJSONObject(getAdapterPosition());
                itemID = selectedItem.getJSONArray("itemId").getString(0);


                // if wishlist button clicked
                if (v.getId() == wishlistButton.getId()) {
                    //TODO: implement wishlist function

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
                    updateWishListView();

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
