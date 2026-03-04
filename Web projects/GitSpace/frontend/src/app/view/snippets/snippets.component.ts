import { Component, OnInit } from '@angular/core';
import { SnippetService } from '../../core/services/snippet.service';
import { Snippet } from '../../shared/models/snippet.model';

@Component({
  selector: 'app-snippets',
  templateUrl: './snippets.component.html',
  styleUrls: ['./snippets.component.scss']
})
export class SnippetsComponent implements OnInit {
  // Toate snippet-urile aduse de la backend (max 1000)
  allSnippets: Snippet[] = [];
  // Snippet-urile afisate pe pagina curenta (max 10)
  displayedSnippets: Snippet[] = [];
  
  loading = true;

  currentPage = 1;
  pageSize = 10;
  totalPages = 0;

  constructor(private snippetService: SnippetService) {}

  ngOnInit(): void {
    this.loadSnippets();
  }

  loadSnippets() {
    this.loading = true;
    this.snippetService.getPublic().subscribe({
      next: (data) => {
        this.allSnippets = data;
        this.totalPages = Math.ceil(this.allSnippets.length / this.pageSize);
        this.updatePage(); 
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  updatePage() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.displayedSnippets = this.allSnippets.slice(startIndex, endIndex);
    
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePage();
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePage();
    }
  }
}