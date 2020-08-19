import { TestBed } from '@angular/core/testing';

import { ZipcodeAutoService } from './zipcode-auto.service';

describe('ZipcodeAutoService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ZipcodeAutoService = TestBed.get(ZipcodeAutoService);
    expect(service).toBeTruthy();
  });
});
