import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [ RegisterComponent ],
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form initially', () => {
    expect(component.registerForm.valid).toBeFalsy();
  });

  it('should validate username length', () => {
    const username = component.registerForm.controls['username'];
    
    username.setValue('abc');
    expect(username.valid).toBeFalsy();
    
    username.setValue('validuser');
    expect(username.valid).toBeTruthy();
  });

  it('should validate email format', () => {
    const email = component.registerForm.controls['email'];
    
    email.setValue('invalid-email');
    expect(email.hasError('email')).toBeTruthy();
    
    email.setValue('valid@test.com');
    expect(email.valid).toBeTruthy();
  });

  it('should validate password complexity', () => {
    const password = component.registerForm.controls['password'];
    
    password.setValue('weakpass');
    expect(password.hasError('pattern')).toBeTruthy();
    
    password.setValue('StrongP@ss1');
    expect(password.valid).toBeTruthy();
  });

  it('should reject submission with incorrect captcha', () => {
    component.registerForm.patchValue({
      username: 'testuser',
      email: 'test@mail.com',
      password: 'StrongP@ss1',
      captcha: '999' 
    });

    component.num1 = 1;
    component.num2 = 1;
    component.captchaResult = 2;

    component.onSubmit();
    
    expect(component.error).toContain('Incorrect captcha');
    expect(authServiceSpy.register).not.toHaveBeenCalled();
    expect(component.registerForm.get('captcha')?.value).toBeNull();
  });

  it('should submit successfully with correct data and captcha', fakeAsync(() => {
    authServiceSpy.register.and.returnValue(of({}));
    
    component.num1 = 5;
    component.num2 = 5;
    component.captchaResult = 10;

    component.registerForm.setValue({
      username: 'testuser',
      email: 'test@mail.com',
      password: 'StrongP@ss1',
      captcha: '10'
    });

    component.onSubmit();
    
    expect(component.loading).toBeTrue();
    expect(component.successMessage).toContain('Access granted');
    expect(authServiceSpy.register).toHaveBeenCalled();

    tick(2000);
    
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  }));

  it('should handle registration error', fakeAsync(() => {
    const errorResponse = { error: { error: 'Registration failed.' } };
    authServiceSpy.register.and.returnValue(throwError(() => errorResponse));

    component.num1 = 2;
    component.num2 = 3;
    component.captchaResult = 5;

    component.registerForm.setValue({
      username: 'existing',
      email: 'test@mail.com',
      password: 'StrongP@ss1',
      captcha: '5'
    });

    component.onSubmit();

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('Registration failed.');
    expect(component.successMessage).toBe('');

    expect(component.registerForm.get('captcha')?.value).toBe('5'); 
  }));

  it('should toggle password visibility', () => {
    expect(component.hidePassword).toBeTrue();
    component.togglePassword();
    expect(component.hidePassword).toBeFalse();
  });
});