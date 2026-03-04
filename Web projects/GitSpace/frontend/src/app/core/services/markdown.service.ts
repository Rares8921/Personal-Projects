import { Injectable } from '@angular/core';
import { marked } from 'marked';
// @ts-ignore
import DOMPurify from 'dompurify';

@Injectable({
  providedIn: 'root'
})
export class MarkdownService {
  constructor() { }

  render(markdown: string): string {
    if (!markdown) return '';
    
    // Configure marked for security and GitHub-flavored markdown
    marked.setOptions({
      breaks: true,
      gfm: true
    });
    
    const rawHtml = marked.parse(markdown);
    const sanitizedHtml = DOMPurify.sanitize(rawHtml);
    return sanitizedHtml;
  }
}