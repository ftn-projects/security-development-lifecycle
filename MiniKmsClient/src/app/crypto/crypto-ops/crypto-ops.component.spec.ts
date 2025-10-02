import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CryptoOpsComponent } from './crypto-ops.component';

describe('CryptoOpsComponent', () => {
  let component: CryptoOpsComponent;
  let fixture: ComponentFixture<CryptoOpsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CryptoOpsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CryptoOpsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
