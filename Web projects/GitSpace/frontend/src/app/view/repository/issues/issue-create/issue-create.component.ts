import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { IssueService } from '../../../../core/services/issue.service';
import { MarkdownService } from '../../../../core/services/markdown.service';

@Component({
  selector: 'app-issue-create',
  templateUrl: './issue-create.component.html',
  styleUrls: ['./issue-create.component.scss']
})
export class IssueCreateComponent implements OnInit {
  issueForm: FormGroup;
  username: string = '';
  repoName: string = '';
  loading = false;
  previewMode = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private issueService: IssueService,
    public markdownService: MarkdownService
  ) {
    this.issueForm = this.fb.group({
      title: ['', Validators.required],
      body: ['']
    });

    // Force change detection for preview updates
    this.issueForm.get('body')?.valueChanges.subscribe(() => {
      if (this.previewMode) {
        // Trigger change detection
        this.previewMode = this.previewMode;
      }
    });
  }

  ngOnInit(): void {
    // create -> issues -> repo-layout
    this.route.parent?.params.subscribe(params => {
      this.username = params['username'];
      this.repoName = params['repoName'];
    });
  }

  submit() {
    if (this.issueForm.invalid) return;
    
    this.loading = true;
    this.issueService.createIssue(this.username, this.repoName, this.issueForm.value)
      .subscribe({
        next: () => {
          this.router.navigate(['../'], { relativeTo: this.route });
        },
        error: (err) => {
          console.error(err);
          this.loading = false;
        }
      });
  }

  togglePreview() {
    this.previewMode = !this.previewMode;
  }

  getPreviewContent(): string {
    const bodyValue = this.issueForm.value.body || '';
    return this.markdownService.render(bodyValue);
  }
}