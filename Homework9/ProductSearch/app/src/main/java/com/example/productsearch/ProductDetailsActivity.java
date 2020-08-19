package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class ProductDetailsActivity extends AppCompatActivity implements ProductTabFragment.SendItemDetails {
    // selectedItem properties from ebay Finding API
    private String itemJSONString;
    private JSONObject selectedItem;
    private JSONObject itemDetails; // from Shopping API, receive from product fragment
    private String item_title;
    private String itemID;
    private Context thisContext;
    private FloatingActionButton mWishlistFab;

    ShippingTabFragment tab2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product_details);
        setSupportActionBar(toolbar);
        thisContext = this;
        mWishlistFab = findViewById(R.id.wishlist_fab);

        //show back button to return to previous screen
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.product_details_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.product_details_tabs);
        //set colors for tab icons using color state list xml icon_colors
        ColorStateList colors = getColorStateList(R.color.icon_color);
        for(int i = 0 ; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Drawable icon = tab.getIcon();
            icon.setTintList(colors);
        }

        // Get selected item properties from intent
        Intent intent = getIntent();
        itemJSONString = intent.getStringExtra("productDetail");
        //Set Product Details page title to be item title
        try {
            selectedItem = new JSONObject(itemJSONString);
            itemID = selectedItem.optJSONArray("itemId").optString(0);
            if (searchWishList(itemID) == -1) {
                mWishlistFab.setImageResource(R.drawable.cart_plus);
            }
            else {
                mWishlistFab.setImageResource(R.drawable.cart_remove);
            }

            if (selectedItem.has("title")) {
                item_title = selectedItem.getJSONArray("title").getString(0);
                getSupportActionBar().setTitle(item_title);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            getSupportActionBar().setTitle("No Results");
        }

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.wishlist_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = item_title;

                // shorten title if it is too long
                if (item_title.length() > 40) {
                    message = item_title.substring(0,40) + "...";
                }

                // item not in wish list yet, add to wish list
                if (searchWishList(itemID) == -1) {
                    message += " was added to wish list";
                    mWishlistFab.setImageResource(R.drawable.cart_remove);
                }
                else {
                    // item already in wishlist, click removes from wishlist
                    message += "was removed from wish list";
                    mWishlistFab.setImageResource(R.drawable.cart_plus);
                }

                wishListUpdate(selectedItem);

                // show toast with appropriate message
                Toast toast = Toast.makeText(thisContext, message, Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }


    /**
     * Search Wishlist SharedPreferences for specific item
     * @param itemID id string of item
     * @return index of item in JSONArray of wishlist
     */
    private int searchWishList(String itemID) {
        SharedPreferences sharedPref = thisContext.getSharedPreferences(thisContext.getString(R.string.sharedPreference_key), Context.MODE_PRIVATE);
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

        SharedPreferences sharedPref = thisContext.getSharedPreferences(thisContext.getString(R.string.sharedPreference_key), Context.MODE_PRIVATE);
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
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * back button to go back to previous activity/screen (without reloading)
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Menu selection
        if (id == R.id.action_facebook) {
            String url = getFBShareDialog(itemDetails);
            //DEBUG Toast.makeText(this, url, Toast.LENGTH_LONG).show();

            //new Intent to open up browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            // Android package manager looks for an Activity to handle Intent
            if(intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            else {
                Log.d("FacebookShare", "Error opening Facebook Dialog");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Interface method, activity receives the data from prodcut fragment
     * activity then sends this data to other fragments
     * @param details
     */
    @Override
    public void sendData(JSONObject details) {
        itemDetails = details;
        tab2.putItemDetails(itemDetails);
        /*
        ShippingTabFragment shippingTabFragment = (ShippingTabFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_shipping);
        // create fragment and give it argument for details
        ShippingTabFragment newFragment = new ShippingTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ProdDetails_item", itemJSONString);
        bundle.putString("item_details", details.toString());
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // replace old fragment with new fragment (that has details)
        transaction.replace(R.id.fragment_shipping, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("ProdDetails_item", itemJSONString);
            switch (position) {
                // in each switch case, create tab fragment, then pass item object to fragment
                case 0:
                    ProductTabFragment tab1 = new ProductTabFragment();
                    tab1.setArguments(bundle);
                    return tab1;
                case 1:
                    tab2 = new ShippingTabFragment();
                    tab2.setArguments(bundle);
                    return tab2;
                case 2:
                    Bundle titleBundle = new Bundle();
                    titleBundle.putString("ProdDetails_title", item_title);
                    GoogleTabFragment tab3 = new GoogleTabFragment();
                    tab3.setArguments(titleBundle);
                    return tab3;
                case 3:
                    SimilarTabFragment tab4 = new SimilarTabFragment();
                    tab4.setArguments(bundle);
                    return tab4;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

    /** private method to get url that opens facebook share dialog
     * create url using item details
     * @return url
     */
    private String getFBShareDialog(@NonNull JSONObject item) {
        String price = "";
        String quote = "";
        String itemURL = "";
        String url = "https://www.facebook.com/dialog/share?app_id=2214634821934837%20&href=";

        // item url to share (href)
        if (item.has("ViewItemURLForNaturalSearch")) {
            itemURL = item.optString("ViewItemURLForNaturalSearch");
        }
        // item price
        if (item.has("CurrentPrice")) {
            price = item.optJSONObject("CurrentPrice").optString("Value");
        }

        // quote
        quote = "Buy " + item_title + " at $" + price + " from link below";
        try {
            itemURL = URLEncoder.encode(itemURL, "utf-8");
            quote = URLEncoder.encode(quote, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // create url combining all parts and include #hashtag
        url += itemURL + "&quote=" + quote + "&hashtag=%23CSCI571Spring2019Ebay";
        return url;
    }

}
