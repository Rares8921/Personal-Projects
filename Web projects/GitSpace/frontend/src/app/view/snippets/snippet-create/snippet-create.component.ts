import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SnippetService } from '../../../core/services/snippet.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-snippet-create',
  templateUrl: './snippet-create.component.html',
  styleUrls: ['./snippet-create.component.scss']
})
export class SnippetCreateComponent implements OnInit, OnDestroy {
  snippetForm: FormGroup;
  loading = false;
  private filenameSub: Subscription | undefined;


  editorOptions = { 
    theme: 'vs-dark', 
    language: 'plaintext', 
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    automaticLayout: true 
  };


  private languageMap: { [key: string]: string } = {
    'js': 'javascript', 'jsx': 'javascript',
    'ts': 'typescript', 'tsx': 'typescript',
    'py': 'python',
    'java': 'java',
    'cpp': 'cpp', 'c': 'c',
    'html': 'html',
    'css': 'css',
    'scss': 'scss',
    'json': 'json',
    'sql': 'sql',
    'md': 'markdown',
    'go': 'go',
    'rs': 'rust',
    'php': 'php',
    'cs': 'csharp'
  };

  constructor(
    private fb: FormBuilder,
    private snippetService: SnippetService,
    private router: Router
  ) {
    this.snippetForm = this.fb.group({
      filename: ['', Validators.required],
      description: [''],
      content: ['', Validators.required],
      isPublic: [true]
    });
  }

  ngOnInit(): void {
    this.filenameSub = this.snippetForm.get('filename')?.valueChanges.subscribe((filename: string) => {
      this.detectLanguage(filename);
    });
  }

  ngOnDestroy(): void {
    if (this.filenameSub) {
      this.filenameSub.unsubscribe();
    }
  }

  private detectLanguage(filename: string) {
    if (!filename) return;

    const ext = filename.split('.').pop()?.toLowerCase() || '';
    const newLang = this.languageMap[ext] || 'plaintext';

    if (this.editorOptions.language !== newLang) {
      this.editorOptions = { ...this.editorOptions, language: newLang };
    }
  }

  submit() {
    if (this.snippetForm.invalid) return;

    this.loading = true;
    this.snippetService.create(this.snippetForm.value).subscribe({
      next: () => {
        this.router.navigate(['/snippets']);
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }
}