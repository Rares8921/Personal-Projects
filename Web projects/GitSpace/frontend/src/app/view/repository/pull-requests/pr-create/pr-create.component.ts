import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PullRequestService } from '../../../../core/services/pr.service';
import { RepositoryService } from 'src/app/core/services/repo.service';

@Component({
  selector: 'app-pr-create',
  templateUrl: './pr-create.component.html',
  styleUrls: ['./pr-create.component.scss']
})
export class PrCreateComponent implements OnInit {
  prForm: FormGroup;
  username: string = '';
  repoName: string = '';
  loading = false;
  public branches: string[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private prService: PullRequestService,
    private repoService: RepositoryService
  ) {
    this.prForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      fromBranch: ['', Validators.required], // Ex: feature/login
      toBranch: ['master', Validators.required] // Default master
    });
  }

  ngOnInit(): void {
    this.route.parent?.params.subscribe(params => {
      this.username = params['username'];
      this.repoName = params['repoName'];
    });

    this.repoService.getBranches(this.username, this.repoName).subscribe(data => {
      this.branches = data;

      if (this.branches.includes('master')) {
        this.prForm.patchValue({ toBranch: 'master' });
      }

      const sourceCandidate = this.branches.find(b => b !== 'master') || this.branches[0];
      if (sourceCandidate) {
        this.prForm.patchValue({ 
          fromBranch: sourceCandidate,
          title: `Merge ${sourceCandidate} to master` 
        });
      }

      if (this.branches.includes('master')) {
        this.prForm.patchValue({ toBranch: 'master' });
      }
    });
  }

  submit() {
    if (this.prForm.invalid) return;
    
    this.loading = true;
    const dto = {
      title: this.prForm.value.title,
      description: this.prForm.value.description,
      fromBranch: this.prForm.value.fromBranch,
      toBranch: this.prForm.value.toBranch
    };

    this.prService.create(this.username, this.repoName, dto)
      .subscribe({
        next: (pr) => {
          this.router.navigate(['../', pr.prNumber], { relativeTo: this.route });
        },
        error: (err) => {
          console.error(err);
          this.loading = false;
        }
      });
  }
}