import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IssueService } from '../../../../core/services/issue.service';
import { Issue } from '../../../../shared/models/issue.model';

@Component({
  selector: 'app-issue-list',
  templateUrl: './issue-list.component.html',
  styleUrls: ['./issue-list.component.scss']
})
export class IssueListComponent implements OnInit {
  username: string = '';
  repoName: string = '';
  issues: Issue[] = [];
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private issueService: IssueService
  ) {}

  ngOnInit(): void {
    this.route.parent?.params.subscribe(params => {
      this.username = params['username'];
      this.repoName = params['repoName'];
      if(this.username && this.repoName) {
        this.loadIssues();
      }
    });
  }

  loadIssues() {
    this.loading = true;
    this.issueService.getIssues(this.username, this.repoName).subscribe({
      next: (data) => {
        this.issues = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}