import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NotFoundComponent } from './not-found.component';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotFoundComponent ],
      imports: [ RouterTestingModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display 404 error code', () => {
    const errorCode = fixture.debugElement.query(By.css('.error-code'));
    expect(errorCode).toBeTruthy();
    expect(errorCode.nativeElement.textContent).toContain('404');
  });

  it('should display error message', () => {
    const message = fixture.debugElement.query(By.css('h2'));
    expect(message.nativeElement.textContent).toContain('System error: Resource not found');
  });

  it('should have navigation links', () => {
    const dashboardLink = fixture.debugElement.query(By.css('a[routerLink="/"]'));
    expect(dashboardLink).toBeTruthy();
    
    const reposLink = fixture.debugElement.query(By.css('a[routerLink="/repositories"]'));
    expect(reposLink).toBeTruthy();
  });
});