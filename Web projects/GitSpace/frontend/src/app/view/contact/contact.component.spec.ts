import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ContactComponent } from './contact.component';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('ContactComponent', () => {
  let component: ContactComponent;
  let fixture: ComponentFixture<ContactComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContactComponent ],
      imports: [ ReactiveFormsModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContactComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); 
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form should be invalid when empty', () => {
    expect(component.contactForm.valid).toBeFalsy();
    
    const submitBtn = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(submitBtn.nativeElement.disabled).toBeTrue();
  });

  it('should validate email format', () => {
    const emailControl = component.contactForm.controls['email'];
    
    emailControl.setValue('not-an-email');
    expect(emailControl.hasError('email')).toBeTrue();

    emailControl.setValue('valid@dacia.git');
    expect(emailControl.hasError('email')).toBeFalse();
  });

  it('should validate message length', () => {
    const msgControl = component.contactForm.controls['message'];
    
    msgControl.setValue('Short'); 
    expect(msgControl.valid).toBeFalse();

    msgControl.setValue('This is a long enough message for the validation to pass.');
    expect(msgControl.valid).toBeTrue();
  });

  it('should simulate sending data and show success message', fakeAsync(() => {
    component.contactForm.setValue({
      name: 'Test User',
      email: 'test@dacia.git',
      subject: 'Bug Report',
      message: 'This is a valid message body for testing purposes.'
    });

    expect(component.contactForm.valid).toBeTrue();

    component.submit();
    fixture.detectChanges();

    expect(component.loading).toBeTrue();
    const btn = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(btn.nativeElement.textContent).toContain('Transmitting');

    tick(1500);
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.sent).toBeTrue();

    const successMsg = fixture.debugElement.query(By.css('.success-message'));
    expect(successMsg).toBeTruthy();
    
    const form = fixture.debugElement.query(By.css('form'));
    expect(form).toBeFalsy();
  }));
});