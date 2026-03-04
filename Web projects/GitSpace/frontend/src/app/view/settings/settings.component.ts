import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { FileUploadService } from '../../core/services/file-upload.service';
import { User, UserProfile, UserSettingsDto } from '../../shared/models/user.model';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  profileForm!: FormGroup;
  
  currentUser: User | null = null;
  userProfile: UserProfile | null = null;
  
  loading = false;
  uploadingAvatar = false;
  avatarUrl = 'assets/static/default-avatar.jpg';

  cliToken = 'gh_p_xxxxxxxxxxxxxxxxxxxx';

  activeTab: string = 'profile';

  isDarkMode = true;
  private readonly THEME_KEY = 'dacia_git_theme';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private fileUploadService: FileUploadService
  ) { }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    
    this.initializeTheme();

    this.profileForm = this.fb.group({
      fullName: [''],
      bio: [''],
      location: [''],
      website: [''],
      isPublic: [true]
    });

    if (this.currentUser) {
      this.loadProfileData();
    }
  }

  initializeTheme() {
    const savedTheme = localStorage.getItem(this.THEME_KEY);
    
    if (savedTheme === 'light') {
      this.setLightMode();
    } else {
      this.setDarkMode();
    }
  }

  toggleTheme() {
    if (this.isDarkMode) {
      this.setLightMode();
    } else {
      this.setDarkMode();
    }
  }

  private setLightMode() {
    this.isDarkMode = false;
    document.documentElement.setAttribute('data-theme', 'light');
    localStorage.setItem(this.THEME_KEY, 'light');
  }

  private setDarkMode() {
    this.isDarkMode = true;
    document.documentElement.removeAttribute('data-theme');
    localStorage.setItem(this.THEME_KEY, 'dark');
  }

  loadProfileData() {
    if (!this.currentUser) return;

    this.avatarUrl = this.currentUser.avatarUrl || 'assets/static/default-avatar.jpg';
    
    this.loading = true;
    
    this.userService.getProfile(this.currentUser.username).subscribe({
      next: (profile) => {
        this.userProfile = profile;
        
        this.profileForm.patchValue({
          fullName: this.currentUser?.fullName || '', 
          bio: profile.location, // TODO verif
          location: profile.location,
          website: profile.website,
          isPublic: profile.isPublic
        });
        
        if (this.currentUser?.bio) {
            this.profileForm.patchValue({ bio: this.currentUser.bio });
        }

        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.uploadingAvatar = true;
      this.fileUploadService.uploadAvatar(file).subscribe({
        next: (response) => {
          this.avatarUrl = response.fileDownloadUri;
          this.uploadingAvatar = false;
          
          this.authService.updateLocalUser({ avatarUrl: response.fileDownloadUri });
        },
        error: () => {
          alert('Failed to upload avatar.');
          this.uploadingAvatar = false;
        }
      });
    }
  }

  saveProfile() {
    if (this.profileForm.invalid) return;

    this.loading = true;
    const dto: UserSettingsDto = this.profileForm.value;

    this.userService.updateSettings(dto).subscribe({
      next: () => {
        alert('Profile updated successfully.');
        this.loading = false;
        
        this.authService.updateLocalUser({ 
          fullName: dto.fullName,
          bio: dto.bio
        });
      },
      error: (err) => {
        alert('Failed to update profile.');
        this.loading = false;
      }
    });
  }

  requestPasswordReset() {
    if(!this.currentUser?.email) {
      alert('Please update your email in profile first.'); 
      return;
    }
    
    if(confirm('We will send a password reset link to ' + this.currentUser.email + '. Continue?')) {
      this.authService.forgotPassword(this.currentUser.email).subscribe({
        next: () => alert('Reset link sent to email.'),
        error: (err) => alert(err.error?.message || 'Failed to send reset link.')
      });
    }
  }

  regenerateToken() {
    this.cliToken = 'gh_p_' + Math.random().toString(36).substr(2, 16);
  }
}