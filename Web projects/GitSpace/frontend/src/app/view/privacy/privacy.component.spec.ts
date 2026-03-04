import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PrivacyComponent } from './privacy.component';
import { By } from '@angular/platform-browser';

describe('PrivacyComponent', () => {
  let component: PrivacyComponent;
  let fixture: ComponentFixture<PrivacyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrivacyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrivacyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct title', () => {
    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.textContent).toContain('Data Privacy Protocol');
  });

  it('should display the last updated version', () => {
    const version = fixture.debugElement.query(By.css('.version'));
    expect(version.nativeElement.textContent).toContain('System Cycle 2026.01');
  });

  it('should render all privacy sections', () => {
    const sections = fixture.debugElement.queryAll(By.css('section'));
    expect(sections.length).toBe(4);

    const headers = fixture.debugElement.queryAll(By.css('h3'));
    expect(headers[0].nativeElement.textContent).toContain('1. Data Collection');
    expect(headers[1].nativeElement.textContent).toContain('2. Source Code Usage');
    expect(headers[2].nativeElement.textContent).toContain('3. Cookies & Local Storage');
    expect(headers[3].nativeElement.textContent).toContain('4. Account Termination');
  });
});