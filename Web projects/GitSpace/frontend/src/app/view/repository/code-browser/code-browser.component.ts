import { Component, OnInit, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GitService } from '../../../core/services/git-data.service';
import { RepositoryService } from '../../../core/services/repo.service';
import { GitTreeEntry } from '../../../shared/models/git.model';
import { Repository } from '../../../shared/models/repo.model';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'app-code-browser',
  templateUrl: './code-browser.component.html',
  styleUrls: ['./code-browser.component.scss']
})
export class CodeBrowserComponent implements OnInit {
  username: string = '';
  repoName: string = '';
  branch: string = 'master';
  currentPath: string = '';

  files: GitTreeEntry[] = [];
  branches: string[] = [];
  loading = true;
  error = '';
  repo: Repository | null = null;
  isEmptyRepo = false;
  showBranchDropdown = false;
  showCodeDropdown = false;
  cloneType: 'https' | 'ssh' = 'https';
  copySuccess = false;

  commitsCount: number = 0;
  lastCommitMessage: string = '';
  lastCommitAuthor: string = '';
  lastCommitTime: string = '';

  readmeContent: string | null = null;
  toc: { level: number, text: string, anchor: string }[] = [];

  constructor(
    public route: ActivatedRoute,
    public router: Router,
    private gitService: GitService,
    private repoService: RepositoryService
  ) {}

  ngOnInit(): void {
    combineLatest([
      this.route.parent!.params, // Parametrii din RepositoryDetailComponent
      this.route.params // Parametrii din CodeBrowser
    ]).subscribe(([parentParams, childParams]) => {
      this.username = parentParams['username'];
      this.repoName = parentParams['repoName'];

      this.branch = childParams['branch'] || 'master';
      this.currentPath = childParams['path'] || '';

      this.loadRepo();
      this.loadBranches();
      this.loadTree();
      this.loadCommits();
    });
  }

  loadRepo() {
    this.repoService.getRepo(this.username, this.repoName).subscribe({
      next: (data) => {
        this.repo = data;
      },
      error: (err) => {
        console.error('Failed to load repo info', err);
      }
    });
  }

  loadBranches() {
    this.gitService.getBranches(this.username, this.repoName).subscribe({
      next: (branches) => {
        this.branches = branches;
      },
      error: (err) => {
        console.error('Failed to load branches', err);
        this.branches = ['master'];
      }
    });
  }

  loadCommits() {
    this.gitService.getCommits(this.username, this.repoName, this.branch).subscribe({
      next: (commits) => {
        this.commitsCount = commits.length;
        if (commits.length > 0) {
          const lastCommit = commits[0];
          this.lastCommitMessage = lastCommit.message || 'No message';
          this.lastCommitAuthor = lastCommit.authorName || this.username;
          this.lastCommitTime = this.formatCommitTime(lastCommit.date);
        }
      },
      error: (err) => {
        console.error('Failed to load commits', err);
        this.commitsCount = 0;
      }
    });
  }

  formatCommitTime(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'today';
    if (diffDays === 1) return 'yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`;
    if (diffDays < 365) return `${Math.floor(diffDays / 30)} months ago`;
    return `${Math.floor(diffDays / 365)} years ago`;
  }

  goToCommits() {
    this.router.navigate(['activity'], { relativeTo: this.route.parent });
  }

  loadTree() {
    this.loading = true;
    this.error = '';
    this.isEmptyRepo = false;

    this.readmeContent = null;
    this.toc = [];

    this.gitService.getTree(this.username, this.repoName, this.branch, this.currentPath)
      .subscribe({
        next: (entries) => {
          this.files = entries.sort((a, b) => {
            if (a.type === b.type) return a.name.localeCompare(b.name);
            return a.type === 'tree' ? -1 : 1;
          });
          this.loading = false;
          this.isEmptyRepo = false;
          const readmeEntry = entries.find(e => e.name.toLowerCase() === 'readme.md');
          if (readmeEntry) {
            this.loadReadme(readmeEntry.name);
          }
        },
        error: (err) => {
          if (err.status === 404 && !this.currentPath) {
            this.isEmptyRepo = true;
            this.files = [];
            this.error = '';
          } else {
            this.error = 'Failed to load file tree.';
            console.error(err);
          }
          this.loading = false;
        }
      });
  }

  navigate(entry: GitTreeEntry) {
    if (entry.type === 'tree') {
      // /repo/user/name/tree/branch/path/folder
      const newPath = this.currentPath ? `${this.currentPath}/${entry.name}` : entry.name;
      this.router.navigate(['tree', this.branch, newPath], { relativeTo: this.route.parent });
    } else {
      // Navigam la File Viewer (Blob): /repo/user/name/blob/branch/path/file.ext
      const newPath = this.currentPath ? `${this.currentPath}/${entry.name}` : entry.name;
      this.router.navigate(['blob', this.branch, newPath], { relativeTo: this.route.parent });
    }
  }

  goUp() {
    if (!this.currentPath) return; // Suntem in root

    const segments = this.currentPath.split('/');
    segments.pop(); // Stergem ultimul folder
    const parentPath = segments.join('/');

    if (!parentPath) {
      // Mergem la root
      this.router.navigate(['./'], { relativeTo: this.route.parent });
    } else {
      this.router.navigate(['tree', this.branch, parentPath], { relativeTo: this.route.parent });
    }
  }

  formatSize(bytes: number): string {
    if (!bytes) return '';
    if (bytes < 1024) return bytes + ' B';
    const k = 1024;
    const sizes = ['KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i - 1];
  }

  toggleBranchDropdown() {
    this.showBranchDropdown = !this.showBranchDropdown;
  }

  selectBranch(branchName: string) {
    if (branchName !== this.branch) {
      this.branch = branchName;
      this.showBranchDropdown = false;
      this.router.navigate(['tree', branchName], { relativeTo: this.route.parent });
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    const branchPicker = target.closest('.branch-picker');
    const codeDropdown = target.closest('.code-dropdown-wrapper');
    if (!branchPicker) {
      this.showBranchDropdown = false;
    }
    if (!codeDropdown) {
      this.showCodeDropdown = false;
    }
  }

  toggleCodeDropdown() {
    this.showCodeDropdown = !this.showCodeDropdown;
    this.showBranchDropdown = false;
  }

  getCloneUrl(): string {
    const baseUrl = this.repo?.cloneUrlHttp || `http://localhost:8080/git/${this.username}/${this.repoName}.git`;
    if (this.cloneType === 'https') {
      return baseUrl;
    } else {
      return `git@localhost:${this.username}/${this.repoName}.git`;
    }
  }

  copyCloneUrl(inputElement: HTMLInputElement) {
    inputElement.select();
    navigator.clipboard.writeText(inputElement.value).then(() => {
      this.copySuccess = true;
      setTimeout(() => {
        this.copySuccess = false;
      }, 2000);
    });
  }

  downloadZip() {
    this.loading = true; 
    this.gitService.downloadRepoArchive(this.username, this.repoName, this.branch)
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          
          const a = document.createElement('a');
          a.href = url;
          a.download = `${this.repoName}-${this.branch}.zip`;
          document.body.appendChild(a);
          a.click();
          
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
          
          this.loading = false;
          this.showCodeDropdown = false;
        },
        error: (err) => {
          console.error('Download failed', err);
          alert('Could not download repository.');
          this.loading = false;
        }
      });
  }

  loadReadme(filename: string) {
    const fullPath = this.currentPath ? `${this.currentPath}/${filename}` : filename;
    
    this.gitService.getFileContent(this.username, this.repoName, this.branch, fullPath)
      .subscribe({
        next: (content) => {
          this.readmeContent = content;
          this.generateToc(content);
        },
        error: (err) => console.error('Failed to load README', err)
      });
  }

  generateToc(content: string) {
    this.toc = [];
    const regex = /^(#{1,6})\s+(.*)$/gm;
    let match;
    
    while ((match = regex.exec(content)) !== null) {
      const level = match[1].length;
      const text = match[2].trim();
      const anchor = text.toLowerCase().replace(/[^\w\- ]+/g, '').replace(/\s+/g, '-');
      
      this.toc.push({ level, text, anchor });
    }
  }

  scrollToAnchor(anchor: string, event: Event) {
    event.preventDefault();
    const element = document.getElementById(anchor);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
  
}
