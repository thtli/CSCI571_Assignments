import { Component, OnInit, Input } from '@angular/core';
import { ProductDetailService } from '../product-detail.service';
import { ImageSearchService } from '../image-search.service';
import { SimilarItemsService } from '../similar-items.service';

@Component({
  selector: 'app-wish-list',
  templateUrl: './wish-list.component.html',
  styleUrls: ['./wish-list.component.css']
})
export class WishListComponent implements OnInit {
  @Input() wishlist;
  @Input() totalShopping;

  //for product details results
  selectedItem = {};
  selectedDetails = []; // item details in an array (ebay shopping API)
  selectedImages = []; // images returned from Google API
  similarItems = []; // similar items returned from Ebay sim items API
  rawData; //used in similar items search

  showResultsTable = true;

  constructor(
    private productDetailService: ProductDetailService,
    private imageSearchService: ImageSearchService,
    private similarItemsService: SimilarItemsService) { }

  ngOnInit() {
    this.totalShopping = this.calculateTotal();
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
    this.wishlist = JSON.parse(localStorage.getItem('wishlist')) || [];
    this.totalShopping = this.calculateTotal();
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
  this.wishlist = wishListArr;
  this.totalShopping = this.calculateTotal();
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

  calculateTotal(){
    var total = 0;
    for(var i=0; i < this.wishlist.length; i++) {
      // object price value is a string with $ in front, must extract first to calculte sum
      total += Number(this.wishlist[i].price.substring(1));
    }
    return total;
  }
}
