import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RepositoryService } from '../../../core/services/repo.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-create-repo',
  templateUrl: './create-repo.component.html',
  styleUrls: ['./create-repo.component.scss']
})
export class CreateRepoComponent implements OnInit {
  repoForm!: FormGroup;
  loading = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private repoService: RepositoryService,
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.repoForm = this.fb.group({
      name: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9-_]+$/)]], // caractere safe
      description: [''],
      isPublic: [true],
      license: ['None']
    });
  }

  get f() { return this.repoForm.controls; }

  onSubmit() {
    if (this.repoForm.invalid) return;

    this.loading = true;
    this.error = '';

    const request = {
      name: this.repoForm.value.name,
      description: this.repoForm.value.description,
      isPublic: this.repoForm.value.isPublic,
      license: this.repoForm.value.license
    };

    this.repoService.createRepo(request).subscribe({
      next: (repo) => {
        // /repo/username/repoName
        const currentUser = this.authService.currentUserValue?.username;
        this.router.navigate(['/repo', currentUser, repo.name]);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Failed to create repository. Name might be taken.';
      }
    });
  }
}