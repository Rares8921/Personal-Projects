import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { ActivityService } from '../../core/services/activity.service';
import { SnippetService } from '../../core/services/snippet.service';
import { User } from '../../shared/models/user.model';
import { Activity } from 'src/app/shared/models/activity.model';
import { Snippet } from 'src/app/shared/models/snippet.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  currentUser: User | null = null;
  
  feed: Activity[] = [];
  trendingSnippets: Snippet[] = [];
  
  loadingFeed = false;

  constructor(
    private authService: AuthService,
    private activityService: ActivityService,
    private snippetService: SnippetService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (this.currentUser) {
        this.loadCommunityData();
      }
    });
  }

  loadCommunityData() {
    this.loadingFeed = true;

    this.activityService.getMyFeed().subscribe({
      next: (data) => {
        this.feed = data;
      },
      error: (err) => {
        console.error('Failed to load activity feed', err);
        this.feed = [];
      }
    });

    this.snippetService.getPublic().subscribe({
      next: (data) => {
        this.trendingSnippets = data ? data.slice(0, 5) : [];
        this.loadingFeed = false;
      },
      error: (err) => {
        console.error('Failed to load snippets', err);
        this.loadingFeed = false;
      }
    });
  }

  getActivityIcon(type: string): string {
    switch (type) {
      case 'PUSH': return 'pi pi-arrow-up';
      case 'ISSUE_CREATED': return 'pi pi-exclamation-circle';
      case 'PR_CREATED': return 'pi pi-sitemap';
      case 'FORK': return 'pi pi-copy';
      case 'REPO_CREATED': return 'pi pi-database';
      default: return 'pi pi-info-circle';
    }
  }
}