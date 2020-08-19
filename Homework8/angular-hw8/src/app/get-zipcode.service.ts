import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { map } from 'rxjs/operators';
import { Location } from './location';

@Injectable({
  providedIn: 'root'
})
export class GetZipcodeService {
  private apiURL: string =  'http://ip-api.com/json/';

  constructor(private http: HttpClient) { }

  /** GET zipcode from API */
  getCurrentLoc() {
    return this.http.get(this.apiURL);
  }
}