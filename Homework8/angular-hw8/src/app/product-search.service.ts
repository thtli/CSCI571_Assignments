import { Injectable, EventEmitter, Output } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})

export class ProductSearchService {
  @Output() change: EventEmitter<Object> = new EventEmitter();
  searchQuery: {[k: string]: any} = {};

  constructor(private http: HttpClient) { }


  results = []; //arrray of results 
  rawData$: any = {}; //holds the api data from the service
  
  // update search query (either from submit button or clear button)
  submitQuery(query:{[k: string]: any}) {
    this.results = [];
    this.searchQuery = query;

    // if search query is not empty, run http request
    if (JSON.stringify(this.searchQuery) != JSON.stringify({})) {
      var apiURL = "http://csci571-project-nodejs.us-east-2.elasticbeanstalk.com/productsearch/";
      apiURL +=  `keyword=${encodeURI(this.searchQuery.keyword)}`;
      apiURL += `&category=${this.searchQuery.category}`;
      apiURL += `&new=${this.searchQuery.cond_new}`;
      apiURL += `&used=${this.searchQuery.cond_used}`;
      apiURL += `&unspecified=${this.searchQuery.cond_unspecified}`;
      apiURL += `&local=${this.searchQuery.localPickup}`;
      apiURL += `&free=${this.searchQuery.freeShipping}`;
      apiURL += `&zipcode=${this.searchQuery.zipcode}`;
      apiURL += `&miles=${this.searchQuery.distance}`;
      
      this.http.get(apiURL).pipe(
        catchError(this.handleError()))
        .subscribe( data => {
          if (data.length == 0) {
            this.results.push({searched: true});
          } 
          else {
            this.rawData$ = data['findItemsAdvancedResponse'][0].searchResult[0].item; 
            // no search results
            if (this.rawData$ == undefined) {
              this.results.push({searched: true});
      
            }
            // format search results into results array 
            else {
      
              for (var i = 0; i < this.rawData$.length; i++) {
                // create temp object to hold single item result
                var tempObj: {[k: string]: any} = {};
                //Column 1 (index)
                tempObj.index = i+1;
      
                //Column 2 (Image)
                if (this.rawData$[i].galleryURL == undefined) {
                  tempObj.image = "N/A";
                }
                else {
                  tempObj.image = this.rawData$[i].galleryURL[0];
                }
      
                //Column 3 (Title) & getting ID
                tempObj.id = this.rawData$[i].itemId[0];
                tempObj.title = this.rawData$[i].title[0];
      
                //Column 4 (Price)
                tempObj.price = "$" + this.rawData$[i].sellingStatus[0].currentPrice[0].__value__;
      
                //Column 5 (Shipping)
                if (this.rawData$[i].shippingInfo[0].shippingServiceCost == undefined) {
                  tempObj.shipping = "N/A";
                }
                else if (this.rawData$[i].shippingInfo[0].shippingServiceCost[0].__value__ == "0.0") {
                  tempObj.shipping = "Free Shipping";
                }
                else {
                  tempObj.shipping = "$" + this.rawData$[i].shippingInfo[0].shippingServiceCost[0].__value__;
                }

                //Extra shipping info needed for item details 
                if (this.rawData$[i].shippingInfo != undefined) {
                  tempObj.shippinginfo = this.rawData$[i].shippingInfo[0];
                }

                //Extra returns info needed for item details
                if (this.rawData$[i].returnsAccepted != undefined) {
                  tempObj.returns = this.rawData$[i].returnsAccepted[0];
                }
      
                //Column 6 (Zip)
                if (this.rawData$[i].postalCode == undefined) {
                  tempObj.zip = "N/A";
                }
                else {
                  tempObj.zip = this.rawData$[i].postalCode[0];
                }
              
                //Column 7 (Seller)  
                tempObj.seller = this.rawData$[i].sellerInfo[0].sellerUserName[0];
                
                tempObj.searched = true;
                this.results.push(tempObj);
              }
              
            }
          }
          
      });
    }
    
    this.change.emit([this.searchQuery, this.results]);
  }
  handleError() {
    return (error: any): Observable<any> => {
      // Let the app keep running by returning an empty result.
      return of([]);
    };
  } 
}