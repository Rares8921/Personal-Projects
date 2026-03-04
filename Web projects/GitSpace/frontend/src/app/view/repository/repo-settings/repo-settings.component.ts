import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RepositoryService } from '../../../core/services/repo.service';
import { Repository } from '../../../shared/models/repo.model';

@Component({
  selector: 'app-repo-settings',
  templateUrl: './repo-settings.component.html',
  styleUrls: ['./repo-settings.component.scss']
})
export class RepoSettingsComponent implements OnInit {
  settingsForm: FormGroup;
  repo: Repository | null = null;
  loading = false;
  
  username: string = '';
  repoName: string = '';

  constructor(
    private fb: FormBuilder,
    private repoService: RepositoryService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.settingsForm = this.fb.group({
      description: [''],
      isPublic: [false]
    });
  }

  ngOnInit(): void {
    this.route.parent?.params.subscribe(params => {
      this.username = params['username'];
      this.repoName = params['repoName'];
      this.loadRepo();
    });
  }

  loadRepo() {
    this.repoService.getRepo(this.username, this.repoName).subscribe(repo => {
      this.repo = repo;
      this.settingsForm.patchValue({
        description: repo.description,
        isPublic: repo.isPublic
      });
    });
  }

  updateRepo() {
    if (this.settingsForm.invalid) return;
    this.loading = true;

    // Backend: PUT /api/repos/{username}/{repoName}
    this.repoService.updateRepo(this.username, this.repoName, this.settingsForm.value).subscribe({
      next: (updated) => {
        alert('Repository updated successfully.');
        this.loading = false;
        this.repo = updated;
      },
      error: (err) => {
        alert('Update failed: ' + err.message);
        this.loading = false;
      }
    });
  }

  deleteRepo() {
    const confirmation = prompt(`To confirm, type "${this.repoName}" in the box below`);
    if (confirmation === this.repoName) {
      // Backend: DELETE /api/repos/{username}/{repoName}
      this.repoService.deleteRepo(this.username, this.repoName).subscribe({
        next: () => {
          this.router.navigate(['/']); // Redirect home
        },
        error: (err) => alert('Delete failed: ' + err.message)
      });
    } else {
      alert('Repo name does not match. Deletion cancelled.');
    }
  }
  toggleArchive(status: boolean) {
    const action = status ? 'archive' : 'unarchive';
    if (confirm(`Are you sure you want to ${action} this repository?`)) {
      this.repoService.updateRepo(this.username, this.repoName, { isArchived: status }).subscribe({
        next: (updatedRepo) => {
          this.repo = updatedRepo;
          this.repoService.refreshRepo$.next();
          alert(`Repository ${action}d successfully.`);
        }
      });
    }
  }
}