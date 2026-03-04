import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RepositoryService } from '../../core/services/repo.service';
import { AuthService } from '../../core/services/auth.service';
import { Repository } from '../../shared/models/repo.model';

@Component({
  selector: 'app-repository-detail',
  templateUrl: './repository-detail.component.html',
  styleUrls: ['./repository-detail.component.scss']
})
export class RepositoryDetailComponent implements OnInit {
  repo: Repository | null = null;
  loading = true;
  isAdmin: boolean = false;
  error: string | null = null;

  username: string = '';
  repoName: string = '';
  currentUser: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private repoService: RepositoryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    this.currentUser = user ? user.username : null;
    this.isAdmin = !!user?.roles?.includes('ROLE_ADMIN');
      
    this.repoService.refreshRepo$.subscribe(() => {
      this.loadRepository();
    });

    this.route.params.subscribe(params => {
      this.username = params['username'];
      this.repoName = params['repoName'];

      if (this.username && this.repoName) {
        this.loadRepository();
      }
    });
  }

  loadRepository() {
    this.loading = true;
    this.repoService.getRepo(this.username, this.repoName).subscribe({
      next: (data) => {
        this.repo = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Repository not found or access denied.';
        this.loading = false;
      }
    });
  }


  toggleStar() {
    if(!this.repo) return;
    // TODO
    this.repo.starsCount++;
  }

  canViewStats(): boolean {
  if (!this.repo) return false;
    return this.currentUser === this.repo.ownerUsername || this.isAdmin;
  }
}
