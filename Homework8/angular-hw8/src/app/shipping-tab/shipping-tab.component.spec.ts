import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShippingTabComponent } from './shipping-tab.component';

describe('ShippingTabComponent', () => {
  let component: ShippingTabComponent;
  let fixture: ComponentFixture<ShippingTabComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShippingTabComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShippingTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
