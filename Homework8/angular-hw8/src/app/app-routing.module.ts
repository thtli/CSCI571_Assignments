import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ResultsTableComponent } from './results-table/results-table.component';
import { WishListComponent } from './wish-list/wish-list.component';

const routes: Routes = [
  { path: 'results', component: ResultsTableComponent },
  { path: 'wishlist', component: WishListComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
