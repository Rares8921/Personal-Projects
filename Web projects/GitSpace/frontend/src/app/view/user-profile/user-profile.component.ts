import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service'; 
import { UserProfileDto } from '../../shared/models/user.model';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {
  profile: UserProfileDto | null = null;
  currentUser: string | null = null;
  loading = true;
  actionLoading = false; // Pentru buton
  error = '';

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    this.currentUser = user ? user.username : null;

    this.route.params.subscribe(params => {
      const username = params['username'];
      if (username) {
        this.loadProfile(username);
      }
    });
  }

  loadProfile(username: string) {
    this.loading = true;
    this.userService.getProfile(username).subscribe({
      next: (data: any) => { 
        this.profile = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'User not found.';
        this.loading = false;
      }
    });
  }

  onFollowAction() {
    if (!this.profile || !this.currentUser) return;
    if (this.actionLoading) return;

    this.actionLoading = true;

    if (this.profile.isFollowing || this.profile.isPending) {
      this.userService.unfollow(this.profile.username).subscribe({
        next: () => {
          if (this.profile!.isFollowing) this.profile!.followers--; 
          this.profile!.isFollowing = false;
          this.profile!.isPending = false;
          this.actionLoading = false;
        },
        error: () => this.actionLoading = false
      });
    } else {
      this.userService.follow(this.profile.username).subscribe({
        next: (response: any) => {
          if (response.message === 'Follow request sent') {
            this.profile!.isPending = true;
          } else {
            this.profile!.isFollowing = true;
            this.profile!.followers++;
          }
          this.actionLoading = false;
        },
        error: () => this.actionLoading = false
      });
    }
  }

  getButtonText(): string {
    if (this.profile?.isFollowing) return 'Unfollow';
    if (this.profile?.isPending) return 'Request Sent';
    return !this.profile?.isPublic ? 'Request to Follow' : 'Follow';
  }
  
  getLanguageColor(language?: string): string {
    if (!language) return '#cccccc';
    switch (language.toLowerCase()) {
      case 'typescript': return '#2b7489';
      case 'java': return '#b07219';
      case 'python': return '#3572A5';
      case 'c++': return '#f34b7d';
      default: return '#cccccc';
    }
  }
}