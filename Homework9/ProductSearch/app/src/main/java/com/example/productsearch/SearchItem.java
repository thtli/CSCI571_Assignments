package com.example.productsearch;

/**
 * Class to hold Search Item - result from similar items ebay API
 */
public class SearchItem {
    private String title;
    private String shipping;
    private String daysLeft;
    private String price;
    private String itemURL;
    private String pictureURL;

    // public constructor to create new search item class
    public SearchItem(String title, String shipping, String daysLeft, String price, String itemURL, String pictureURL) {
        this.title = title;
        this.shipping = shipping;
        this.daysLeft = daysLeft;
        this.price = price;
        this.itemURL = itemURL;
        this.pictureURL = pictureURL;
    }

    // public methods to retrieve values of class
    public String getItemTitle() {
        return title;
    }

    public String getItemShipping() {
        return shipping;
    }

    public String getItemDaysLeft() {
        return daysLeft;
    }

    public String getItemPrice() {
        return price;
    }

    public String getItemURL() {
        return itemURL;
    }

    public String getPictureURL() {
        return pictureURL;
    }
}
