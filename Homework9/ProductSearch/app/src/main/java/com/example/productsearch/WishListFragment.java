package com.example.productsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


public class WishListFragment extends Fragment {
    private Context mContext;
    private TextView mDebug;
    private RecyclerView mRecyclerView;
    private WishListAdapter mSearchAdapter;
    private TextView mNoWishes;
    private TextView mCount;
    private TextView mTotalPrice;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        mContext = container.getContext();
        mNoWishes = view.findViewById(R.id.no_wishes);
        mCount = view.findViewById(R.id.wishlist_count);
        mTotalPrice = view.findViewById(R.id.wishlist_totalPrice);

        mRecyclerView = view.findViewById(R.id.wishlist_recyclerView_items);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));

        updateWishList(view);

        //mDebug = view.findViewById(R.id.wishlist_debug);
        //mDebug.setText(wishlistString);


        return view;
    }

    private void updateWishList(View view) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.sharedPreference_key), Context.MODE_PRIVATE);
        String wishlistString = sharedPref.getString("wishlist","[]");
        try {
            JSONArray wishlist = new JSONArray(wishlistString);
            mSearchAdapter = new WishListAdapter(mContext, view);
            mRecyclerView.setAdapter(mSearchAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
            mRecyclerView.setVisibility(View.GONE);
            mNoWishes.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // If page was resumed from a product detail pages, the wishlist may have been altered,
        updateWishList(view);
    }
}
