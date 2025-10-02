import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SignatureOpsComponent } from './signature-ops.component';

describe('SignatureOpsComponent', () => {
  let component: SignatureOpsComponent;
  let fixture: ComponentFixture<SignatureOpsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SignatureOpsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SignatureOpsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
