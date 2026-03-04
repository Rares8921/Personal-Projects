import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RepositoryService } from '../../../core/services/repo.service';

@Component({
  selector: 'app-repo-statistics',
  templateUrl: './repo-statistics.component.html',
  styleUrls: ['./repo-statistics.component.scss']
})
export class RepoStatisticsComponent implements OnInit {
  stats: any = null;
  loading = true;

  contributionData: { [date: string]: number } = {};
  days: string[] = [];

  constructor(private route: ActivatedRoute, private repoService: RepositoryService) {}

  ngOnInit(): void {
    this.route.parent?.params.subscribe(params => {
      const username = params['username'];
      const repoName = params['repoName'];
      this.loadStats(username, repoName);
    });
  }

  loadStats(user: string, repo: string) {
    this.loading = true;
    this.repoService.getRepoStats(user, repo).subscribe({
      next: (data) => {
        this.stats = data;
        this.processHeatmap(data.activity);
        this.loading = false;
      },
      error: (err) => {
        console.error("Failed to load repo stats", err);
        this.loading = false;
      }
    });
  }

  processHeatmap(activity: any[]) {
    const counts: { [key: string]: number } = {};
    
    const today = new Date();
    for (let i = 13; i >= 0; i--) {
      const d = new Date(today);
      d.setDate(today.getDate() - i);
      counts[d.toISOString().split('T')[0]] = 0;
    }

    activity.forEach(commit => {
      const dateStr = commit.timestamp.split('T')[0];
      if (counts.hasOwnProperty(dateStr)) {
        counts[dateStr]++;
      }
    });

    this.contributionData = counts;
    this.days = Object.keys(counts);
  }

  getHeatmapColor(date: string): string {
    const count = this.contributionData[date] || 0;
    if (count === 0) return '#161b22'; 
    if (count === 1) return '#0e4429';
    if (count === 2) return '#006d32';
    if (count >= 3) return '#39d353'; 
    return '#161b22';
  }
}