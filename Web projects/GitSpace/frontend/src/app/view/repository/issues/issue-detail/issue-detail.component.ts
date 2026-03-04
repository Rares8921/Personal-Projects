import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IssueService } from '../../../../core/services/issue.service';
import { MarkdownService } from '../../../../core/services/markdown.service';
import { Issue, IssueComment } from '../../../../shared/models/issue.model';

@Component({
  selector: 'app-issue-detail',
  templateUrl: './issue-detail.component.html',
  styleUrls: ['./issue-detail.component.scss']
})
export class IssueDetailComponent implements OnInit {
  username: string = '';
  repoName: string = '';
  issueNumber: number = 0;
  
  issue: Issue | null = null;
  comments: IssueComment[] = [];
  commentForm: FormGroup;
  loading = true;
  commentPreviewMode = false;

  constructor(
    private route: ActivatedRoute,
    private issueService: IssueService,
    public markdownService: MarkdownService,
    private fb: FormBuilder
  ) {
    this.commentForm = this.fb.group({ body: ['', Validators.required] });
    // Force change detection for comment preview updates
    this.commentForm.get('body')?.valueChanges.subscribe(() => {
      if (this.commentPreviewMode) {
        // Trigger change detection
        this.commentPreviewMode = this.commentPreviewMode;
      }
    });  }

  ngOnInit(): void {
    this.route.parent?.params.subscribe(params => {
      this.username = params['username'];
      this.repoName = params['repoName'];
    });

    this.route.params.subscribe(params => {
      this.issueNumber = +params['id'];
      if(this.issueNumber) this.loadData();
    });
  }

  loadData() {
    this.loading = true;
    this.issueService.getIssue(this.username, this.repoName, this.issueNumber).subscribe(issue => {
      this.issue = issue;
      this.issueService.getComments(this.username, this.repoName, this.issueNumber).subscribe(comments => {
        this.comments = comments;
        this.loading = false;
      });
    });
  }

  postComment() {
    if(this.commentForm.invalid) return;
    this.issueService.addComment(this.username, this.repoName, this.issueNumber, this.commentForm.value.body)
      .subscribe(newComment => {
        this.comments.push(newComment);
        this.commentForm.reset();
      });
  }

  toggleStatus() {
    if(!this.issue) return;
    const newStatus = !this.issue.isOpen;
    this.issueService.changeStatus(this.username, this.repoName, this.issueNumber, newStatus)
      .subscribe(() => {
        this.issue!.isOpen = newStatus;
      });
  }

  toggleCommentPreview() {
    this.commentPreviewMode = !this.commentPreviewMode;
  }

  getCommentPreviewContent(): string {
    const bodyValue = this.commentForm.value.body || '';
    return this.markdownService.render(bodyValue);
  }
}