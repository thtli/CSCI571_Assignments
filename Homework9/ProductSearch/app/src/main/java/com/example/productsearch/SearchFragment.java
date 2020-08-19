package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchFragment extends Fragment implements View.OnClickListener {
    private Context thisContext; //used for getting context in other methods
    public static final String EXTRA_QUERY = "com.example.productsearch.extra.QUERY";
    public static final String EXTRA_KEYWORD = "com.example.productsearch.extra.KEYWORD";
    private static final String DEFAULT_MILES = "10";
    // member variables for other search form controls
    private EditText mKeyword;
    private CheckBox mCondNew;
    private CheckBox mCondUsed;
    private CheckBox mCondUnspec;
    private CheckBox mLocalShipping;
    private CheckBox mFreeShipping;
    private EditText mMilesFrom;
    private RadioButton mFromCurrentLoc;
    private RadioButton mFromZipcode;
    private Button mSearchButton;
    private Button mClearButton;

    // member variables for determining display of nearby search options
    private CheckBox mEnableSearch;
    private RelativeLayout mNearbySearchOptions;

    // member variable for displaying spinner
    private Spinner mCategories;

    // member variable for zipcode autocomplete textview
    private static final int TRIGGER_AUTO_TIME = 100;
    private static final long AUTO_COMP_DELAY = 300;
    private AppCompatAutoCompleteTextView mZipcode;
    private AutoZipAdapter autoZipAdapter;
    private Handler handler;

    // member variable for error messages
    private TextView mKeywordError;
    private TextView mZipcodeError;

    // Search Form Values To Send
    private String categorySelected;
    private String selectedZip;
    private String currentZip;

    // DEBUG
    private TextView mDebug; //member variable for a debug text view

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //set member variables
        thisContext = container.getContext(); //need it for getting context in other methods
        mEnableSearch = view.findViewById(R.id.checkBox_nearby_search);
        mNearbySearchOptions = view.findViewById(R.id.nearby_search_options);
        mCategories = view.findViewById(R.id.spinner_category);
        mZipcode = view.findViewById(R.id.editText_zipcode);

        mKeyword = view.findViewById(R.id.editText_keyword);
        mCondNew = view.findViewById(R.id.checkBox_new);
        mCondUsed = view.findViewById(R.id.checkBox_used);
        mCondUnspec = view.findViewById(R.id.checkBox_unspecified);
        mLocalShipping = view.findViewById(R.id.checkBox_local);
        mFreeShipping = view.findViewById(R.id.checkBox_free);
        mMilesFrom = view.findViewById(R.id.editText_miles);
        mFromCurrentLoc = view.findViewById(R.id.radio_current_location);
        mFromZipcode = view.findViewById(R.id.radio_zipcode);

        mKeywordError = view.findViewById(R.id.keyword_error);
        mZipcodeError = view.findViewById(R.id.zipcode_error);

        mSearchButton = view.findViewById(R.id.button_search);
        mClearButton = view.findViewById(R.id.button_clear);

        //DEBUG
        mDebug = view.findViewById(R.id.debug);

        // set on click listener for check box, search button, and clear button
        mEnableSearch.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        mFromCurrentLoc.setOnClickListener(this);
        mFromZipcode.setOnClickListener(this);

        // default values
        categorySelected = "All";

        // set listener on spinner (updated selected string)
        mCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //DEBUG categorySelected = parent.getItemAtPosition(position).toString();
                // set categorySelected to be the short forms for API call based on selection
                if (position == 1) {
                    categorySelected = "Art";
                } else if (position == 2) {
                    categorySelected = "Baby";
                } else if (position == 3) {
                    categorySelected = "Books";
                } else if (position == 4) {
                    categorySelected = "Clothing";
                } else if (position == 5) {
                    categorySelected = "Computers";
                } else if (position == 6) {
                    categorySelected = "Health";
                }else if (position == 7) {
                    categorySelected = "Music";
                }else if (position == 8) {
                    categorySelected = "VideoGames";
                } else {
                    categorySelected = "All";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // create array adapter using string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                thisContext, R.array.category_array, android.R.layout.simple_spinner_item);
        // specify the layout to use for dropdown appearance
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mCategories.setAdapter(adapter);

        //create array adapter for auto complete
        autoZipAdapter = new AutoZipAdapter(thisContext,
                android.R.layout.simple_dropdown_item_1line);

        setUpZipAdapter();

        // Get current location zipcode
        ApiCall.make(thisContext, "ip-api", "json",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            currentZip = response.getString("zip");
                            mDebug.setText(currentZip);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        return view;

    }

    /**
     * Private method to set up auto complete adapter
     */
    private void setUpZipAdapter() {
        mZipcode.setThreshold(1);
        mZipcode.setAdapter(autoZipAdapter);
        mZipcode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedZip = autoZipAdapter.getObject(position);
                //DEBUG
                mDebug.setText(selectedZip);
            }
        });

        mZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_TIME);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_TIME, AUTO_COMP_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == TRIGGER_AUTO_TIME) {
                    if(!TextUtils.isEmpty(mZipcode.getText())) {
                        makeApiCall(mZipcode.getText().toString());
                    }
                }
                return false;
            }
        });
    }

    /**
     * Method for making calls to ZIP geonames API for zipcode autocomplete
     * @param text query
     */
    private void makeApiCall(String text) {
        ApiCall.make(thisContext, "geonames", text, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // parsing response object
                List<String> stringList = new ArrayList<>();
                try {

                    JSONArray array = response.getJSONArray("zipcodes");
                    for (int i = 0; i < array.length(); i++) {
                        stringList.add(array.getString(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //set data and notify
                autoZipAdapter.setData(stringList);
                autoZipAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBox_nearby_search:
                if(mEnableSearch.isChecked()){
                    mNearbySearchOptions.setVisibility(View.VISIBLE);
                }
                else {
                    mNearbySearchOptions.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_current_location:
                mZipcode.setEnabled(false);
                break;
            case R.id.radio_zipcode:
                mZipcode.setEnabled(true);
                break;
            case R.id.button_search:
                //check for errors
                if (displayErrorMessages()){
                    showToast(mSearchButton);
                } else {
                    // start search
                    String queryString = createQueryString();
                    Intent intent = new Intent(getActivity(), SearchResults.class);
                    Bundle extras = new Bundle();
                    extras.putString(EXTRA_QUERY, queryString);
                    extras.putString(EXTRA_KEYWORD, mKeyword.getText().toString());
                    intent.putExtras(extras);
                    startActivity(intent);
                }

                break;
            case R.id.button_clear:
                clearInputs();
                break;
        }
    }

    /**
     * private method to generate query string from search form inputs
     * @return
     */
    private String createQueryString() {
        //keyword=iphone
        //&category=all
        //&new=true
        // &used=true
        // &unspecified=true
        // &local=true
        // &free=true
        // &zipcode=90007
        // &miles=none
        String query = "keyword=";

        //keyword
        try {
            query += URLEncoder.encode(mKeyword.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ;
        //category
        query += "&category=" + categorySelected;

        //condition
        query += "&new=" + String.valueOf(mCondNew.isChecked());
        query += "&used=" + String.valueOf(mCondUsed.isChecked());
        query += "&unspecified=" + String.valueOf(mCondUnspec.isChecked());

        //shipping
        query += "&local=" + String.valueOf(mLocalShipping.isChecked());
        query += "&free=" + String.valueOf(mFreeShipping.isChecked());

        //zipcode
        query += "&zipcode=";
        if (mFromZipcode.isChecked()) {
            query += mZipcode.getText().toString();
        } else {
            query += currentZip;
        }

        // miles from
        query += "&miles=";
        if (mEnableSearch.isChecked()) {
            //if no miles entered
            if (mMilesFrom.getText().toString().equals("")) {
                query += DEFAULT_MILES;
            } else {
                query += mMilesFrom.getText().toString();
            }
        } else {
            query += "none";
        }
        return query;
    }

    /**
     * Private method to display error messages (part of search form validation)
     * @return boolean to indicate whether an error was found
     */
    private boolean displayErrorMessages() {
        boolean errors = false;
        //keyword cannot be empty or all spaces
        if (!Pattern.matches("^(?!\\s*$).+", mKeyword.getText().toString())){
            mKeywordError.setVisibility(View.VISIBLE);
            errors = true;
        }
        else {
            mKeywordError.setVisibility(View.GONE);
        }
        //when from zipcode option is selected zipcode input must be 5 numbers
        if (mFromZipcode.isChecked() &&
                        !Pattern.matches("[0-9]{5}", mZipcode.getText().toString())) {
            mZipcodeError.setVisibility(View.VISIBLE);
            errors = true;
        }
        else {
            mZipcodeError.setVisibility(View.GONE);
        }
        return errors;
    }

    /**
     * method for displaying toast message (error message alert)
    */
     private void showToast(View view) {
        Toast toast = Toast.makeText(thisContext, R.string.input_error_toast, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Private method to clear search form and hide error messages
     */
    private void clearInputs() {
        // clear input values
        mKeyword.setText("");
        mCategories.setSelection(0);
        //mCondNew.setChecked(false);
        //mCondUsed.setChecked(false);
        //mCondUnspec.setChecked(false);
        mEnableSearch.setChecked(false);
        mNearbySearchOptions.setVisibility(View.GONE);
        //mLocalShipping.setChecked(false);
        //mFreeShipping.setChecked(false);
        mMilesFrom.setText("");
        mFromCurrentLoc.setChecked(true);
        mZipcode.setText("");
        // hide error messages
        mKeywordError.setVisibility(View.GONE);
        mZipcodeError.setVisibility(View.GONE);
    }
}
