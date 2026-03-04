import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TermsComponent } from './terms.component';
import { By } from '@angular/platform-browser';

describe('TermsComponent', () => {
  let component: TermsComponent;
  let fixture: ComponentFixture<TermsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TermsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TermsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct title', () => {
    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.textContent).toContain('Terms of Service');
  });

  it('should display the effective date', () => {
    const version = fixture.debugElement.query(By.css('.version'));
    expect(version.nativeElement.textContent).toContain('Effective Date: Immediate');
  });

  it('should render all terms sections', () => {
    const sections = fixture.debugElement.queryAll(By.css('section'));
    expect(sections.length).toBe(4);

    const headers = fixture.debugElement.queryAll(By.css('h3'));
    expect(headers[0].nativeElement.textContent).toContain('1. Acceptable Use');
    expect(headers[1].nativeElement.textContent).toContain('2. Service Availability');
    expect(headers[2].nativeElement.textContent).toContain('3. API Usage');
    expect(headers[3].nativeElement.textContent).toContain('4. Liability');
  });
});