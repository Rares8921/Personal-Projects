import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SnippetService } from '../../../core/services/snippet.service';
import { Snippet } from '../../../shared/models/snippet.model';

@Component({
  selector: 'app-snippet-detail',
  templateUrl: './snippet-detail.component.html',
  styleUrls: ['./snippet-detail.component.scss']
})
export class SnippetDetailComponent implements OnInit {
  snippet: Snippet | null = null;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private snippetService: SnippetService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.snippetService.getById(id).subscribe({
      next: (data) => {
        this.snippet = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}