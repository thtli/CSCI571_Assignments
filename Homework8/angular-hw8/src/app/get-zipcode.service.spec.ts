import { TestBed } from '@angular/core/testing';

import { GetZipcodeService } from './get-zipcode.service';

describe('GetZipcodeService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: GetZipcodeService = TestBed.get(GetZipcodeService);
    expect(service).toBeTruthy();
  });
});
