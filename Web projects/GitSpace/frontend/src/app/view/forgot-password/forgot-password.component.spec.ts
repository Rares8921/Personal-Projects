import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ForgotPasswordComponent } from './forgot-password.component';
import { AuthService } from '../../core/services/auth.service';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError, timer } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { By } from '@angular/platform-browser';

describe('ForgotPasswordComponent', () => {
  let component: ForgotPasswordComponent;
  let fixture: ComponentFixture<ForgotPasswordComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['forgotPassword']);

    await TestBed.configureTestingModule({
      declarations: [ ForgotPasswordComponent ],
      imports: [ 
        ReactiveFormsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ForgotPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    expect(component.forgotForm.valid).toBeFalsy();
    const btn = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(btn.nativeElement.disabled).toBeTrue();
  });

  it('should validate email format', () => {
    const emailControl = component.forgotForm.controls['email'];
    
    emailControl.setValue('invalid-email');
    expect(emailControl.hasError('email')).toBeTrue();

    emailControl.setValue('valid@dacia.git');
    expect(emailControl.hasError('email')).toBeFalse();
  });

  it('should submit successfully and show success message', fakeAsync(() => {
    const mockResponse = { message: 'Check your email' };
    
    authServiceSpy.forgotPassword.and.returnValue(of(mockResponse).pipe(delay(10)));

    component.forgotForm.controls['email'].setValue('test@dacia.git');
    fixture.detectChanges();

    component.onSubmit();
    fixture.detectChanges(); 
    
    expect(component.loading).toBeTrue();
    
    tick(10); 
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(authServiceSpy.forgotPassword).toHaveBeenCalledWith('test@dacia.git');
    expect(component.message).toBe('Check your email');
    expect(component.error).toBe('');
    
    expect(component.forgotForm.controls['email'].value).toBeNull(); 
  }));

  it('should handle error during submission', fakeAsync(() => {
    const errorResponse = {
      status: 404,
      error: { 
        error: 'Email not found' 
      }
    };
    
    authServiceSpy.forgotPassword.and.returnValue(
      timer(10).pipe(switchMap(() => throwError(() => errorResponse)))
    );

    component.forgotForm.controls['email'].setValue('unknown@dacia.git');
    fixture.detectChanges();

    component.onSubmit();
    fixture.detectChanges();
    
    expect(component.loading).toBeTrue();
    
    tick(10);
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('System error. Try again later.'); 
    expect(component.message).toBe('');
  }));
});