import { TestBed } from '@angular/core/testing';

import { SimilarItemsService } from './similar-items.service';

describe('SimilarItemsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SimilarItemsService = TestBed.get(SimilarItemsService);
    expect(service).toBeTruthy();
  });
});
