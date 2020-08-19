import { TestBed } from '@angular/core/testing';

import { ImageSearchService } from './image-search.service';

describe('ImageSearchService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ImageSearchService = TestBed.get(ImageSearchService);
    expect(service).toBeTruthy();
  });
});
