import { Injectable, EventEmitter, Output } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProductDetailService {
  @Output() change: EventEmitter<Object> = new EventEmitter();
  itemID:string;  //input, item ID to search details of

  constructor(private http: HttpClient) { }

  getDetails(id) {
    this.itemID = id;
    var apiURL = "http://csci571-project-nodejs.us-east-2.elasticbeanstalk.com/productdetail/";
    apiURL += `id=${this.itemID}`;
    
    if (this.itemID != "") {
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
