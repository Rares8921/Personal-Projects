import { Component, OnInit } from '@angular/core';
import { RepositoryService } from '../../core/services/repo.service';
import { AuthService } from '../../core/services/auth.service';
import { Repository } from '../../shared/models/repo.model'; 
import { User } from '../../shared/models/user.model';

@Component({
  selector: 'app-repositories',
  templateUrl: './repositories.component.html',
  styleUrls: ['./repositories.component.scss']
})
export class RepositoriesComponent implements OnInit {
  
  repositories: Repository[] = [];
  currentUser: User | null = null;
  loading: boolean = true;

  currentPage = 0;
  pageSize = 20;
  totalPages = 0;

  constructor(
    private repositoryService: RepositoryService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (this.currentUser) {
        this.loadRepositories();
      } else {
        this.loading = false;
      }
    });
  }

  loadRepositories() {
    this.loading = true;
    
    this.repositoryService.getExploreRepos(this.currentPage, this.pageSize).subscribe({
      next: (pageData) => {
        this.repositories = pageData.content;
        this.totalPages = pageData.totalPages;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching top repos:', err);
        this.loading = false;
      }
    });
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadRepositories();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRepositories();
    }
  }

  getLanguageColor(language?: string): string {
    if (!language) return '#cccccc';
    switch (language.toLowerCase()) {
      case 'typescript': return '#2b7489';
      case 'javascript': return '#f1e05a';
      case 'java': return '#b07219';
      case 'python': return '#3572A5';
      case 'c++': return '#f34b7d';
      case 'c#': return '#178600';
      case 'rust': return '#dea584';
      case 'go': return '#00ADD8';
      case 'html': return '#e34c26';
      case 'css': return '#563d7c';
      default: return '#cccccc';
    }
  }
}