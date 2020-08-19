import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SellerTabComponent } from './seller-tab.component';

describe('SellerTabComponent', () => {
  let component: SellerTabComponent;
  let fixture: ComponentFixture<SellerTabComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SellerTabComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SellerTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
