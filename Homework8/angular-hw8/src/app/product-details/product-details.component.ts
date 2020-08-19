import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ProductDetailService } from '../product-detail.service';
import { FacebookService, InitParams, UIParams, UIResponse } from 'ngx-facebook';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {
  @Input() selectedItem;  //id of selected item, taken from results table clicks
  @Input() selectedDetails = []; // item details in an array, used in details table, from API call
  @Input() selectedImages = []; // images returned from Google API
  @Input() similarItems = []; // similar items returned from Ebay sim items API
  @Input() resOrWL;  // 'r'=results or 'w'=wishlist, depending on whether product detail component created is generated from results table or wish list table
                    // id for tabs and tab content named accordingly with the letter prefix

  @Output()
  listClick: EventEmitter<any> = new EventEmitter<any>();

  
  results = []; // array of item properties 
  constructor(
    private productDetailService: ProductDetailService,
    private fb: FacebookService ) {
      let initParams: InitParams = {
        appId      : '2214634821934837',
        xfbml      : true,
        version    : 'v3.2'
      };
   
      fb.init(initParams);
     }

  ngOnInit() {
  }

  returnToSearch(){
    this.listClick.emit(true);
  }
  
  shareOnFB() {
    let params: UIParams = {
      href: `${this.selectedDetails[1].ViewItemURLForNaturalSearch}`,
      method: 'share',
      quote: `Buy ${this.selectedDetails[1].Title} at $${this.selectedDetails[1].CurrentPrice.Value} from link below` 
    };
   
    this.fb.ui(params)
      .then((res: UIResponse) => console.log(res))
      .catch((e: any) => console.error(e));
   
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
