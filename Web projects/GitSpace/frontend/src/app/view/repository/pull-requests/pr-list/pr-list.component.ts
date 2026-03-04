import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PullRequestService } from '../../../../core/services/pr.service';
import { PullRequest } from '../../../../shared/models/pr.model';

@Component({
  selector: 'app-pr-list',
  templateUrl: './pr-list.component.html',
  styleUrls: ['./pr-list.component.scss']
})
export class PrListComponent implements OnInit {
  username: string = '';
  repoName: string = '';
  prs: PullRequest[] = [];
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private prService: PullRequestService
  ) {}

  ngOnInit(): void {
    this.route.parent?.params.subscribe(params => {
      this.username = params['username'];
      this.repoName = params['repoName'];
      
      if(this.username && this.repoName) {
        this.loadPrs();
      } else {
        this.loading = false; 
      }
    });
  }

  loadPrs() {
    this.loading = true;
    this.prService.list(this.username, this.repoName).subscribe({
      next: (data) => {
        this.prs = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }
}