import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import { RepositoryService } from '../../core/services/repo.service';
import { ActivityService } from '../../core/services/activity.service';
import { Repository } from '../../shared/models/repo.model';
import { Activity } from '../../shared/models/activity.model';
import { User } from '../../shared/models/user.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  
  // Data Streams
  repositories: Repository[] = [];
  activities: Activity[] = [];
  
  // Loading States
  loadingRepos = true;
  loadingActivity = true;

  // Filter Repos
  repoFilter = '';

  constructor(
    private authService: AuthService,
    private repoService: RepositoryService,
    private activityService: ActivityService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    
    if (this.currentUser) {
      this.loadRepositories();
      this.loadActivityFeed();
    }
  }

  private loadRepositories() {
    this.loadingRepos = true;
    this.repoService.getUserRepos(this.currentUser!.username)
      .pipe(
        catchError(err => {
          console.error('Failed to load repos', err);
          return of([]);
        }),
        finalize(() => this.loadingRepos = false)
      )
      .subscribe(repos => {
        this.repositories = repos;
      });
  }

  private loadActivityFeed() {
    this.loadingActivity = true;
    this.activityService.getMyFeed()
      .pipe(
        catchError(err => {
          console.error('Failed to load activity', err);
          return of([]);
        }),
        finalize(() => this.loadingActivity = false)
      )
      .subscribe(feed => {
        this.activities = feed;
      });
  }


  get filteredRepos(): Repository[] {
    if (!this.repoFilter) return this.repositories;
    return this.repositories.filter(r => 
      r.name.toLowerCase().includes(this.repoFilter.toLowerCase())
    );
  }

  getActivityIcon(type: string): string {
    switch (type) {
      case 'PUSH': return 'pi pi-arrow-up';
      case 'ISSUE_CREATED': return 'pi pi-exclamation-circle';
      case 'PR_CREATED': return 'pi pi-share-alt';
      case 'FORK': return 'pi pi-copy';
      default: return 'pi pi-clock';
    }
  }
}