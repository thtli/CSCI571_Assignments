package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class SearchResults extends AppCompatActivity {
    // member variables
    private LinearLayout mResultsDescription;
    private TextView mResultsKeyword;
    private TextView mResultsSize;
    private TextView mNoResults;
    private LinearLayout mProgressBar;
    //RecyclerView
    private RecyclerView mRecyclerView;
    private SearchResultAdapter mSearchAdapter;
    private Context mContext;
    //JSON array from API call response
    private JSONArray itemsList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //get views from xml
        mResultsDescription = findViewById(R.id.results_description);
        mResultsKeyword = findViewById(R.id.results_keyword);
        mResultsSize = findViewById(R.id.results_size);

        mNoResults = findViewById(R.id.noResults);
        mProgressBar = findViewById(R.id.progressBar_container);
        mRecyclerView = findViewById(R.id.recyclerView_results);
        mContext = this;

        //get intent and data
        Bundle extras = getIntent().getExtras();
        String queryString = extras.getString(SearchFragment.EXTRA_QUERY);
        String keyword = extras.getString(SearchFragment.EXTRA_KEYWORD);

        //set keyword in results description
        mResultsKeyword.setText(keyword);

        //start getting data from API call
        mProgressBar.setVisibility(View.VISIBLE);


        ApiCall.make(this, "productsearch", queryString,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // get the search results array from API call
                            // results stored here:
                            // findItemsAdvancedResponse[0].searchResult[0].item
                            JSONObject results = response.getJSONArray("findItemsAdvancedResponse")
                                    .getJSONObject(0)
                                    .getJSONArray("searchResult")
                                    .getJSONObject(0);

                            // no search results, then {@count: 0}
                            if (results.getString("@count").equals("0")) {
                                mNoResults.setVisibility(View.VISIBLE);
                            } else {
                                //get array of items and display results
                                itemsList = results.getJSONArray("item");

                                //Display results in recycler view with adapter
                                // setting up recycler view
                                mRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
                                mSearchAdapter = new SearchResultAdapter(mContext, itemsList);
                                mRecyclerView.setAdapter(mSearchAdapter);
                                mRecyclerView.setVisibility(View.VISIBLE);

                                //Display results description bar with number of results and keyword
                                int size = itemsList.length();
                                mResultsSize.setText(String.valueOf(size));
                                mResultsDescription.setVisibility(View.VISIBLE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mNoResults.setVisibility(View.VISIBLE);
                        }

                        // after getting results, hide progress bar
                        mProgressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // if API call results in an error, hide prog bar and show no results
                        mProgressBar.setVisibility(View.GONE);
                        mNoResults.setVisibility(View.VISIBLE);
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // If page was resumed from a product detail pages, the wishlist may have been altered,
        // must update search results page wishlist button, so alert adapter
        if (mSearchAdapter != null) {
            mSearchAdapter.notifyDataSetChanged();
        }

    }
}
