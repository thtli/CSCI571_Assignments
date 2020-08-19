import { Component, OnInit } from '@angular/core';
import { FormGroup, NgForm, FormControl } from '@angular/forms';

import { GetZipcodeService } from '../get-zipcode.service';
import { ZipcodeAutoService } from '../zipcode-auto.service';
import { Observable, Subject } from 'rxjs';
import {
   debounceTime, distinctUntilChanged, switchMap, map, startWith
} from 'rxjs/operators';

import { ProductSearchService } from '../product-search.service';

@Component({
  selector: 'app-product-search',
  templateUrl: './product-search.component.html',
  styleUrls: ['./product-search.component.css']
})


export class ProductSearchComponent implements OnInit {
  loc: any;
  currentZip: string;
  
  //searchQuery blank object, add keys(properties) as needed
  searchQuery: {[k: string]: any} = {};
  resultsPill= false;
  wishListPill=false;

  //Form variables, will be updated dynamically with user changes
  fromWhere = "fromHere";
  keyword = "";
  category = "All";
  cond_new = false;
  cond_used = false;
  cond_unspecified = false;
  localPickup = false;
  freeShipping = false;
  distance = "";
  zipcodeDisabled = true;
  zipcode = "";
  
  //Autocomplete zipcode variables 
  autoOptions$: any;
  private searchZipTerms = new Subject<string>();

  //wishlist
  wishlist;
  totalShopping = 0;

  // constructors, methods/functions
  constructor(
    private getzipcodeService: GetZipcodeService,
    private zipcodeAutoService: ZipcodeAutoService,
    private productSearchService: ProductSearchService
  ) { }
  
  // Push a zipcode search term into the observable stream.
  zipSearch(term: string): void {
    this.searchZipTerms.next(term);
  }

  ngOnInit(): void {
    // get current location zipcode via service
    this.getzipcodeService.getCurrentLoc().subscribe(
      data => { this.loc = data; this.currentZip = this.loc.zip;});
    
    
    this.autoOptions$ = this.searchZipTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),
 
      // ignore new term if same as previous term
      distinctUntilChanged(),
 
      // switch to new search observable each time the term changes
      switchMap((term: string) => this.zipcodeAutoService.search(term)),
    );
    
    this.wishlist = JSON.parse(localStorage.getItem('wishlist')) || []; // get existing wishlist or send empty array
  }  

  zipcodeDisable() {
    if (this.fromWhere == "fromZipcode") {
      this.zipcodeDisabled = true;
      this.zipcode = "";
    } else
    {
      this.zipcodeDisabled = false;
    }
  }
  // Reset form
  resetValues(form: NgForm){
    // reset form values to same variables from ngModel and set default values
    form.resetForm({ 
      keyword: this.keyword = "", 
      category: this.category = "All",
      New: this.cond_new = false,
      Used: this.cond_used = false,
      Unspecified: this.cond_unspecified = false,
      LocalPickup: this.localPickup = false,
      FreeShipping: this.freeShipping = false,
      miles: this.distance = "",
      fromWhere: this.fromWhere = "fromHere",
      zipcode: this.zipcode = ""
    })

    // clear search query object
    this.searchQuery = {};
    this.productSearchService.submitQuery(this.searchQuery);
    this.resultsPill= false;
    this.wishListPill=false;

  }

  // form search query object
  createSearchQuery() {
    this.searchQuery.keyword = this.keyword;
    this.searchQuery.category = this.category;
    this.searchQuery.cond_new = this.cond_new.toString();
    this.searchQuery.cond_used = this.cond_used.toString();
    this.searchQuery.cond_unspecified = this.cond_unspecified.toString();
    this.searchQuery.freeShipping = this.freeShipping.toString();
    this.searchQuery.localPickup = this.localPickup.toString();
    
    // set as user entered distance or default
    if (this.distance == "") {
      this.searchQuery.distance = "10";
    }
    else {
      this.searchQuery.distance = this.distance;
    }
    // zipcode is either current location or user entered distance
    if (this.fromWhere == "fromHere") {
      this.searchQuery.zipcode = this.currentZip;
    }
    else {
      this.searchQuery.zipcode = this.zipcode;
    }

    this.productSearchService.submitQuery(this.searchQuery);
    this.resultsPill = true;
  }
  
  // Results and WishList Pill navigation
  showResults() {
    this.resultsPill= true;
    this.wishListPill=false;
  }
  showWishList() {
    this.resultsPill= false;
    this.wishListPill=true;
    this.wishlist = JSON.parse(localStorage.getItem('wishlist')) || []; // get existing wishlist or send empty array
    this.totalShopping = this.calculateTotal();
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
