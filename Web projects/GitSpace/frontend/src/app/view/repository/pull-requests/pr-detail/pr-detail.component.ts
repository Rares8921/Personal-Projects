import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PullRequestService } from '../../../../core/services/pr.service';
import { PullRequest, PullRequestFile } from '../../../../shared/models/pr.model';
import { combineLatest } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MarkdownService } from 'src/app/core/services/markdown.service';

@Component({
  selector: 'app-pr-detail',
  templateUrl: './pr-detail.component.html',
  styleUrls: ['./pr-detail.component.scss']
})
export class PrDetailComponent implements OnInit {
  username: string = '';
  repoName: string = '';
  prNumber: number = 0;
  
  pr: PullRequest | null = null;
  files: PullRequestFile[] = [];
  
  activeTab: 'conversation' | 'files' = 'conversation';
  loading = true;
  merging = false;

  comments: any[] = [];
  reviewForm!: FormGroup;
  inlineCommentForm!: FormGroup;
  activeInlineCell: { file: string, line: number } | null = null;

  constructor(
    private route: ActivatedRoute,
    private prService: PullRequestService,
    private fb: FormBuilder,
    public markdownService: MarkdownService
  ) {
    this.reviewForm = this.fb.group({ body: ['', Validators.required] });
    this.inlineCommentForm = this.fb.group({ body: ['', Validators.required] });
  }

  ngOnInit(): void {
    if (this.route.parent) {
      combineLatest([
        this.route.parent.params,
        this.route.params
      ]).subscribe(([parentParams, params]) => {
        this.username = parentParams['username'];
        this.repoName = parentParams['repoName'];
        this.prNumber = +params['id'];

        if (this.username && this.repoName && this.prNumber) {
          this.loadData();
          this.loadComments();
        }
      });
    }
  }

  loadData() {
    this.loading = true;
    this.prService.get(this.username, this.repoName, this.prNumber).subscribe({
      next: (pr) => {
        this.pr = pr;
        // Incarcam fisierele doar dupa ce avem PR-ul
        this.prService.getFiles(this.username, this.repoName, this.prNumber).subscribe({
          next: (files) => {
            this.files = files;
            this.loading = false;
          },
          error: () => this.loading = false
        });
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  merge() {
    if(!this.pr) return;
    this.merging = true;
    this.prService.merge(this.username, this.repoName, this.prNumber).subscribe({
      next: () => {
        if(this.pr) this.pr.state = 'MERGED';
        this.merging = false;
      },
      error: (err) => {
        alert('Merge failed: ' + err.error?.message);
        this.merging = false;
      }
    });
  }

  // User Story 3: Helper pentru afisarea diff-ului colorat
  getDiffLines(diff: string): { marker: string, content: string }[] {
    if (!diff) return [];
    return diff.split('\n').map(line => {
      if (line.startsWith('+')) return { marker: '+', content: line.substring(1) };
      if (line.startsWith('-')) return { marker: '-', content: line.substring(1) };
      return { marker: ' ', content: line };
    });
  }

  getLineClass(line: { marker: string }): string {
    if (line.marker === '+') return 'line-added';
    if (line.marker === '-') return 'line-deleted';
    return 'line-neutral';
  }

  loadComments() {
    this.prService.getComments(this.username, this.repoName, this.prNumber).subscribe(data => {
      this.comments = data;
    });
  }

  postGeneralComment() {
    if (this.reviewForm.invalid) return;
    const payload = { body: this.reviewForm.value.body };
    this.prService.addComment(this.username, this.repoName, this.prNumber, payload).subscribe(newC => {
      this.comments.push(newC);
      this.reviewForm.reset();
    });
  }

  // Review (Aprobare)
  approvePR() {
    this.prService.submitReview(this.username, this.repoName, this.prNumber, 'APPROVED').subscribe(() => {
      alert('PR Approved');
      location.reload(); // Refresh pentru a vedea statusul nou
    });
  }

  // Deschide formularul de comentariu pe o linie din Diff
  openInlineComment(file: string, lineIndex: number) {
    this.activeInlineCell = { file, line: lineIndex };
    this.inlineCommentForm.reset();
  }

  postInlineComment() {
    if (this.inlineCommentForm.invalid || !this.activeInlineCell) return;
    const payload = {
      body: this.inlineCommentForm.value.body,
      filePath: this.activeInlineCell.file,
      lineNumber: this.activeInlineCell.line
    };
    this.prService.addComment(this.username, this.repoName, this.prNumber, payload).subscribe(newC => {
      this.comments.push(newC);
      this.activeInlineCell = null;
    });
  }

  getCommentsForLine(file: string, line: number) {
    return this.comments.filter(c => c.filePath === file && c.lineNumber === line);
  }
}