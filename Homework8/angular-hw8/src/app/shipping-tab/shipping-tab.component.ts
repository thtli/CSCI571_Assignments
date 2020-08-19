import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-shipping-tab',
  templateUrl: './shipping-tab.component.html',
  styleUrls: ['./shipping-tab.component.css']
})
export class ShippingTabComponent implements OnInit {
  @Input() selectedItemShipping;
  @Input() selectedItemReturns;
  constructor() { }

  ngOnInit() {
  }

}
