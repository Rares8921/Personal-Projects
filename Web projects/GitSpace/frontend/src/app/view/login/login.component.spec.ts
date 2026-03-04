import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login'], {
      currentUserValue: null
    });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [ LoginComponent ],
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should validate form when filled', () => {
    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password');
    fixture.detectChanges();

    expect(component.loginForm.valid).toBeTruthy();
    const btn = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(btn.nativeElement.disabled).toBeFalse();
  });

  it('should toggle password visibility', () => {
    expect(component.hidePassword).toBeTrue();
    
    component.togglePassword();
    expect(component.hidePassword).toBeFalse();
    
    component.togglePassword();
    expect(component.hidePassword).toBeTrue();
  });

  it('should navigate to home on successful login', fakeAsync(() => {
    authServiceSpy.login.and.returnValue(of({ id: 0, token: 'abc', username: 'test', email: 'test@mail.com' }));
    
    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('password');
    
    component.onSubmit();
    tick();

    expect(authServiceSpy.login).toHaveBeenCalled();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    expect(component.failedAttempts).toBe(0);
  }));

  it('should show error on failed login', fakeAsync(() => {
    authServiceSpy.login.and.returnValue(throwError(() => new Error('Failed')));
    
    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('wrong');
    
    component.onSubmit();
    tick();
    fixture.detectChanges();

    expect(component.error).toContain('Authentication failed');
    expect(component.failedAttempts).toBe(1);
    
    const errorMsg = fixture.debugElement.query(By.css('.alert-box.error'));
    expect(errorMsg.nativeElement.textContent).toContain('Authentication failed');
  }));

  it('should activate captcha after 3 failed attempts', fakeAsync(() => {
    authServiceSpy.login.and.returnValue(throwError(() => new Error('Failed')));
    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('wrong');

    component.onSubmit();
    tick();
    expect(component.failedAttempts).toBe(1);
    expect(component.showCaptcha).toBeFalse();

    component.onSubmit();
    tick();
    expect(component.failedAttempts).toBe(2);
    expect(component.showCaptcha).toBeFalse();

    component.onSubmit();
    tick();
    fixture.detectChanges();

    expect(component.failedAttempts).toBe(3);
    expect(component.showCaptcha).toBeTrue();

    const captchaGroup = fixture.debugElement.query(By.css('.captcha-group'));
    expect(captchaGroup).toBeTruthy();
  }));

  it('should validate captcha logic correctly', fakeAsync(() => {
    authServiceSpy.login.and.returnValue(throwError(() => new Error('Failed')));
    component.loginForm.controls['username'].setValue('testuser');
    component.loginForm.controls['password'].setValue('wrong');

    for (let i = 0; i < 3; i++) {
      component.onSubmit();
      tick();
    }
    fixture.detectChanges();
    
    expect(component.showCaptcha).toBeTrue();
    const firstSum = component.num1 + component.num2;
    
    component.loginForm.controls['captcha'].setValue(firstSum + 1);
    component.onSubmit();
    fixture.detectChanges();
    
    expect(component.error).toContain('Verification failed');
    
    const newSum = component.num1 + component.num2;

    authServiceSpy.login.and.returnValue(of({ id: 0, token: 'abc', username: 'test', email: 'test@mail.com' }));
    component.loginForm.controls['captcha'].setValue(newSum);
    
    component.onSubmit();
    tick();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  }));
});