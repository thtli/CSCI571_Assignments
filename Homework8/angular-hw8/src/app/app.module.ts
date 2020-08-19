import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule }    from '@angular/common/http';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatFormFieldModule} from '@angular/material/form-field';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatTooltipModule} from '@angular/material/tooltip';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RoundProgressModule} from 'angular-svg-round-progressbar';
import { FacebookModule } from 'ngx-facebook';


import { AppComponent } from './app.component';
import { ProductSearchComponent } from './product-search/product-search.component';
import { NoResultsComponent } from './no-results/no-results.component';
import { ResultsTableComponent } from './results-table/results-table.component';

import { GetZipcodeService } from './get-zipcode.service';
import { ProgressBarComponent } from './progress-bar/progress-bar.component';
import { WishListComponent } from './wish-list/wish-list.component';
import { ProductDetailsComponent } from './product-details/product-details.component';
import { PictureModalComponent } from './picture-modal/picture-modal.component';
import { PhotosTabComponent } from './photos-tab/photos-tab.component';
import { ShippingTabComponent } from './shipping-tab/shipping-tab.component';
import { SellerTabComponent } from './seller-tab/seller-tab.component';
import { SimilarItemsComponent } from './similar-items/similar-items.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductSearchComponent,
    NoResultsComponent,
    ResultsTableComponent,
    ProgressBarComponent,
    WishListComponent,
    ProductDetailsComponent,
    PictureModalComponent,
    PhotosTabComponent,
    ShippingTabComponent,
    SellerTabComponent,
    SimilarItemsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    NgxPaginationModule,
    MatTooltipModule,
    BrowserAnimationsModule,
    RoundProgressModule,
    FacebookModule.forRoot()
  ],
  providers: [GetZipcodeService],
  bootstrap: [AppComponent]
})
export class AppModule { }
