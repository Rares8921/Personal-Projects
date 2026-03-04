import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SettingsComponent } from './settings.component';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { FileUploadService } from '../../core/services/file-upload.service';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { delay } from 'rxjs/operators'; // Importam delay
import { By } from '@angular/platform-browser';
import { User, UserProfile } from '../../shared/models/user.model';

const mockUser: User = {
  id: 1,
  username: 'testuser',
  email: 'test@dacia.git',
  token: 'x',
  avatarUrl: 'old-avatar.png',
  fullName: 'Test User',
  bio: 'Old Bio'
};

const mockProfile: UserProfile = {
  followers: 10,
  following: 5,
  location: 'Bucharest',
  website: 'https://dacia.git',
  createdAt: '2025-01-01'
};

describe('SettingsComponent', () => {
  let component: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let fileUploadSpy: jasmine.SpyObj<FileUploadService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getProfile', 'updateSettings']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['updateLocalUser', 'forgotPassword'], {
      currentUserValue: mockUser
    });
    fileUploadSpy = jasmine.createSpyObj('FileUploadService', ['uploadAvatar']);

    await TestBed.configureTestingModule({
      declarations: [ SettingsComponent ],
      imports: [ ReactiveFormsModule, RouterTestingModule ],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: FileUploadService, useValue: fileUploadSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load profile data on init', fakeAsync(() => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    
    fixture.detectChanges();
    tick();

    expect(userServiceSpy.getProfile).toHaveBeenCalledWith('testuser');
    expect(component.profileForm.get('location')?.value).toBe('Bucharest');
    expect(component.profileForm.get('website')?.value).toBe('https://dacia.git');
  }));

  it('should upload avatar', fakeAsync(() => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    fixture.detectChanges();

    const mockFile = new File([''], 'avatar.png', { type: 'image/png' });
    const mockEvent = { target: { files: [mockFile] } };
    
    // FIX: Structura completa si DELAY pentru a testa starea de loading (uploadingAvatar=true)
    const response: any = { 
      fileName: 'avatar.png',
      fileDownloadUri: 'new-avatar.png',
      fileType: 'image/png',
      size: 1234
    };

    // Folosim pipe(delay(10)) pentru ca observable-ul sa fie asincron
    fileUploadSpy.uploadAvatar.and.returnValue(of(response).pipe(delay(10)));

    component.onFileSelected(mockEvent);
    
    // Acum starea ar trebui sa fie true
    expect(component.uploadingAvatar).toBeTrue();

    // Avansam timpul
    tick(10);

    expect(component.avatarUrl).toBe('new-avatar.png');
    expect(component.uploadingAvatar).toBeFalse();
    expect(authServiceSpy.updateLocalUser).toHaveBeenCalledWith({ avatarUrl: 'new-avatar.png' });
  }));

  it('should update profile settings', fakeAsync(() => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    fixture.detectChanges();
    tick();

    component.profileForm.patchValue({
      fullName: 'New Name',
      bio: 'New Bio'
    });

    // FIX: Adaugam delay pentru a putea prinde loading=true
    userServiceSpy.updateSettings.and.returnValue(of({}).pipe(delay(10)));
    spyOn(window, 'alert');

    component.saveProfile();
    
    // Verificam loading inainte de finalizarea request-ului
    expect(component.loading).toBeTrue();

    tick(10); // Finalizam request-ul

    expect(component.loading).toBeFalse();
    expect(userServiceSpy.updateSettings).toHaveBeenCalled();
    expect(authServiceSpy.updateLocalUser).toHaveBeenCalledWith(jasmine.objectContaining({ fullName: 'New Name' }));
    expect(window.alert).toHaveBeenCalledWith('Profile updated successfully.');
  }));

  it('should request password reset', () => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    fixture.detectChanges();

    authServiceSpy.forgotPassword.and.returnValue(of({}));
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert');

    component.requestPasswordReset();

    expect(window.confirm).toHaveBeenCalled();
    expect(authServiceSpy.forgotPassword).toHaveBeenCalledWith('test@dacia.git');
    expect(window.alert).toHaveBeenCalledWith('Reset link sent to email.');
  });

  it('should switch tabs', () => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    fixture.detectChanges();

    expect(component.activeTab).toBe('profile');
    
    component.activeTab = 'security';
    fixture.detectChanges();
    
    const securitySection = fixture.debugElement.query(By.css('.security-row'));
    expect(securitySection).toBeTruthy();
  });

  it('should regenerate token', () => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    fixture.detectChanges();

    const oldToken = component.cliToken;
    component.regenerateToken();
    expect(component.cliToken).not.toBe(oldToken);
    expect(component.cliToken.startsWith('gh_p_')).toBeTrue();
  });
});