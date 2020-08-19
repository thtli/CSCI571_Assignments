import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
 
import { Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { ZIPCODES } from './mock-zips';

@Injectable({ providedIn: 'root' })

export class ZipcodeAutoService {

  constructor(private http: HttpClient) { }

  /* GET heroes whose name contains search term */
  search(term: string) {
    if (!term.trim()) {
      // if not search term, return empty hero array.
      return of([]);
    }
    //return of(ZIPCODES);;

    var apiURL = `http://csci571-project-nodejs.us-east-2.elasticbeanstalk.com/geonames/zip=${term}`;
    return this.http.get(apiURL).pipe(map(data => data['zipcodes']));
  }
  
}