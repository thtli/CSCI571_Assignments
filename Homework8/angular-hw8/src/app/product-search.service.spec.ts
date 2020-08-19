import { TestBed } from '@angular/core/testing';

import { ProductSearchService } from './product-search.service';

describe('ProductSearchService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ProductSearchService = TestBed.get(ProductSearchService);
    expect(service).toBeTruthy();
  });
});
