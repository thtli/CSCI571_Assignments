import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-seller-tab',
  templateUrl: './seller-tab.component.html',
  styleUrls: ['./seller-tab.component.css']
})
export class SellerTabComponent implements OnInit {
  @Input() sellerInfo;
  @Input() storeInfo;
  max = 100;
  
  constructor() { }

  ngOnInit() {
  }

}
