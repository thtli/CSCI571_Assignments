import { Injectable, EventEmitter, Output } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ImageSearchService {
  product: string;

  constructor(private http: HttpClient) { }

  getImages(product: string) {
    this.product = product;
    var apiURL = "http://csci571-project-nodejs.us-east-2.elasticbeanstalk.com/imagesearch/";
    apiURL += `product=${encodeURI(this.product.substring(0,30))}`; 
    //only send part of title to increase chances of results

    
    if (this.product != "") {
      return this.http.get(apiURL).pipe(
        catchError(this.handleError())
      );
    }
    else {
      return of([]);
    }
  }

  handleError() {
    return (error: any): Observable<any> => {
      // Let the app keep running by returning an empty result.
      return of([]);
    };
  }
}
