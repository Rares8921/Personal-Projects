import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ResetPasswordComponent } from './reset-password.component';
import { AuthService } from '../../core/services/auth.service';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['resetPassword']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [ ResetPasswordComponent ],
      imports: [ 
        ReactiveFormsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: { token: 'valid-token-123' }
            }
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with token from route', () => {
    expect(component.token).toBe('valid-token-123');
    expect(component.resetForm.enabled).toBeTrue();
    expect(component.error).toBe('');
  });

  it('should validate weak password', () => {
    const password = component.resetForm.controls['password'];
    password.setValue('weak');
    expect(password.hasError('pattern')).toBeTrue();

    password.setValue('StrongP@ss1');
    expect(password.hasError('pattern')).toBeFalse();
  });

  it('should validate password mismatch', () => {
    component.resetForm.patchValue({
      password: 'StrongP@ss1',
      confirmPassword: 'DifferentP@ss2'
    });
    expect(component.resetForm.hasError('mismatch')).toBeTrue();

    component.resetForm.patchValue({
      confirmPassword: 'StrongP@ss1'
    });
    expect(component.resetForm.hasError('mismatch')).toBeFalse();
  });

  it('should toggle password visibility', () => {
    expect(component.hidePassword).toBeTrue();
    component.togglePassword();
    expect(component.hidePassword).toBeFalse();
    component.togglePassword();
    expect(component.hidePassword).toBeTrue();
  });

  it('should submit successfully and navigate after delay', fakeAsync(() => {
    authServiceSpy.resetPassword.and.returnValue(of({ success: true }));

    component.resetForm.setValue({
      password: 'StrongP@ss1',
      confirmPassword: 'StrongP@ss1'
    });

    component.onSubmit();
    fixture.detectChanges();

    expect(component.loading).toBeTrue();
    expect(authServiceSpy.resetPassword).toHaveBeenCalledWith('valid-token-123', 'StrongP@ss1');

    tick(); 
    fixture.detectChanges();

    expect(component.message).toContain('Key updated successfully');
    expect(component.resetForm.disabled).toBeTrue();
    
    tick(3000);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  }));

  it('should handle submission error', () => {
    const errorResponse = { error: { error: 'Token expired' } };
    authServiceSpy.resetPassword.and.returnValue(throwError(() => errorResponse));

    component.resetForm.setValue({
      password: 'StrongP@ss1',
      confirmPassword: 'StrongP@ss1'
    });

    component.onSubmit();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('Token expired');
    expect(component.message).toBe('');
  });
});

describe('ResetPasswordComponent (Missing Token)', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['resetPassword']);

    await TestBed.configureTestingModule({
      declarations: [ ResetPasswordComponent ],
      imports: [ ReactiveFormsModule, RouterTestingModule ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: {} // No token
            }
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show error and disable form if token is missing', () => {
    expect(component.token).toBeUndefined();
    expect(component.error).toContain('Token Missing');
    expect(component.resetForm.disabled).toBeTrue();
  });

  it('should not submit if token is missing', () => {
    component.onSubmit();
    expect(authServiceSpy.resetPassword).not.toHaveBeenCalled();
  });
});