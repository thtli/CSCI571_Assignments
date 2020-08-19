import { Component, OnInit } from '@angular/core';
import { ProductSearchService } from '../product-search.service';
import { ProductDetailService } from '../product-detail.service';
import { ImageSearchService } from '../image-search.service';
import { SimilarItemsService } from '../similar-items.service';


@Component({
  selector: 'app-results-table',
  templateUrl: './results-table.component.html',
  styleUrls: ['./results-table.component.css']
})
export class ResultsTableComponent implements OnInit {

  p = 1; //page

  //for product details results
  selectedItem = {};
  selectedDetails = []; // item details in an array (ebay shopping API)
  selectedImages = []; // images returned from Google API
  similarItems = []; // similar items returned from Ebay sim items API
  rawData; //used in similar items search

  showResultsTable = true;

  results = []; //arrray of results 
  searchQuery ={}; //search query (if blank: no search results at all (query cleared))
  constructor(
    private productSearchService: ProductSearchService,
    private productDetailService: ProductDetailService,
    private imageSearchService: ImageSearchService,
    private similarItemsService: SimilarItemsService
    ) { }

  ngOnInit() {
    // get changes in search Query (from submit or clear buttons)
    this.createTable();
  }
  createTable() {
    // for every new search query, reset the previous items details 
    this.productSearchService.change.subscribe(data => {
      this.results = data[1]; //results array of items
      this.searchQuery = data[0]; //search query 
      //reset all properties of selected Item
      this.selectedItem = {};
      
      this.selectedImages = []; //google image search reset
      this.similarItems = []; //similar items api reset
      this.showResultsTable = true; // return to results table (in case details was selected before)
    });
  }


  selectItem(item: Object):void {
    this.selectedItem = item;

    // Product detail search (ebay API)
    this.productDetailService.getDetails(this.selectedItem['id']).subscribe(data => {
      this.selectedDetails = [];
      this.selectedDetails.push({searched: true}); //use this to determine loading bar or no records
      if (data['Item'] != undefined) {
        this.selectedDetails.push(data['Item']);
      }
    });

    // Google images search (Google API)
    this.imageSearchService.getImages(this.selectedItem['title']).subscribe(data => {
      this.selectedImages = [];
      if (data['items'] != undefined) {
        this.selectedImages = data['items'];
      }
    });

    // Similar Items search (ebay API)
    this.similarItemsService.getSimilarItems(this.selectedItem['id']).subscribe(data => {
      this.similarItems = [];
      if (data['getSimilarItemsResponse'] != undefined){
        this.rawData = data['getSimilarItemsResponse'];
        this.similarItems = this.rawData.itemRecommendations.item;
      }   
    });

    this.returnToDetail();
  }

  returnToSearch() {
    this.showResultsTable = true;
  }
  returnToDetail() {
    this.showResultsTable = false;
  }

 // shopping cart/wish list button updates
 wishListUpdate(item) {
  // if item not in cart, add item
  var wishListArr = JSON.parse(localStorage.getItem('wishlist')) || [];
  if (this.wishListSearch(item.id) == -1) { //item not in wishlist
    wishListArr.push(item);
  }
  else { //item already in wishlist, click to remove
    var itemIndex = this.wishListSearch(item.id);
    wishListArr.splice(itemIndex, 1); //remove specific item from wish list
  }
  localStorage.setItem('wishlist', JSON.stringify(wishListArr)); //update
}

  wishListSearch(currentID) {
    var wishListArr = JSON.parse(localStorage.getItem('wishlist')) || [];
    for(var i=0; i < wishListArr.length; i++) {
      if (wishListArr[i].id == currentID){
        return i; //return index of specific item in wishlist (can be later used to remove)
      }
    }
    return -1;   // not found
  }
}
