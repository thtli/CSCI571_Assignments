import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-similar-items',
  templateUrl: './similar-items.component.html',
  styleUrls: ['./similar-items.component.css']
})
export class SimilarItemsComponent implements OnInit {
  @Input() similarItems;
  @Input() defaultSort;
  //default values
  showMore = false;
  disableAD = true;
  sortOption = 'Default';
  ascendOrDescend = 'Ascending';

  constructor() { }

  ngOnInit() {
  }

  // Show More and Show Less toggle - toggles list itself and the button display
  showMoreLess() {
    if (this.showMore) {
      this.showMore = false;
    }
    else {
      this.showMore = true;
    }
  }

  sortBy() {
    if(this.sortOption == 'Default') {
      this.similarItems = this.defaultSort.slice(); // copy over default sort order
      this.disableAD = true;
    }
    else {
      this.disableAD = false;
      if(this.sortOption == 'Product Name') {
        this.sortProduct('title', this.ascendOrDescend);
      }
      else if (this.sortOption == 'Days Left') {
        this.sortProduct('timeLeft', this.ascendOrDescend);
      }
      else if (this.sortOption == 'Price') {
        this.sortProduct('buyItNowPrice', this.ascendOrDescend, '__value__');
      }
      else if (this.sortOption == 'Shipping Cost') {
        this.sortProduct('shippingCost', this.ascendOrDescend, '__value__');
      }
    }
  }
  
  sortProduct<T>(propName:string , order:string, nestedProp?:string): void {
    // if a nested property provided, sort using nested property
    // (price and shipping cost require this - need to convert to numbers)
    if (nestedProp) { 
      this.similarItems.sort((a, b) => {
        if (Number(a[propName][nestedProp]) < Number(b[propName][nestedProp]))
            return -1;
        if (Number(a[propName][nestedProp]) > Number(b[propName][nestedProp]))
            return 1;
        return 0;
      });
    }

    // if single property is required to sort
    else {
      // if days left chosen, need to trim substring, and convert to number
      if (propName == 'timeLeft') {
        this.similarItems.sort((a, b) => {
        if (Number(a.timeLeft.substring(1,a.timeLeft.indexOf('D'))) < Number(b.timeLeft.substring(1,b.timeLeft.indexOf('D'))))
            return -1;
        if (Number(a.timeLeft.substring(1,a.timeLeft.indexOf('D'))) > Number(b.timeLeft.substring(1,b.timeLeft.indexOf('D'))))
            return 1;
        return 0;
      });
      }
      else {
        this.similarItems.sort((a, b) => {
        if (a[propName] < b[propName])
            return -1;
        if (a[propName] > b[propName])
            return 1;
        return 0;
        });
      }
    }

    if (order == "Descending") {
        this.similarItems.reverse();
    } 
  }   

}
