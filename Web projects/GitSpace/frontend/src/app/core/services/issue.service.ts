import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Issue, IssueComment } from '../../shared/models/issue.model';

@Injectable({ providedIn: 'root' })
export class IssueService {
  private apiUrl = environment.apiUrl; // Folosim base api pt ca URL-ul e complex

  constructor(private http: HttpClient) {}

  private getBaseUrl(username: string, repoName: string): string {
    return `${this.apiUrl}/repos/${username}/${repoName}/issues`;
  }

  // GET /api/repos/{u}/{r}/issues?search=...
  getIssues(username: string, repoName: string, search?: string): Observable<Issue[]> {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    
    return this.http.get<Issue[]>(this.getBaseUrl(username, repoName), { params });
  }

  // POST /api/repos/{u}/{r}/issues
  createIssue(username: string, repoName: string, data: { title: string; body: string }): Observable<Issue> {
    return this.http.post<Issue>(this.getBaseUrl(username, repoName), data);
  }

  // GET /api/repos/{u}/{r}/issues/{number}
  getIssue(username: string, repoName: string, issueNumber: number): Observable<Issue> {
    return this.http.get<Issue>(`${this.getBaseUrl(username, repoName)}/${issueNumber}`);
  }

  // PATCH /api/repos/{u}/{r}/issues/{number}/status
  changeStatus(username: string, repoName: string, issueNumber: number, isOpen: boolean): Observable<void> {
    return this.http.patch<void>(`${this.getBaseUrl(username, repoName)}/${issueNumber}/status`, { open: isOpen });
  }

  // GET /api/repos/{u}/{r}/issues/{number}/comments
  getComments(username: string, repoName: string, issueNumber: number): Observable<IssueComment[]> {
    return this.http.get<IssueComment[]>(`${this.getBaseUrl(username, repoName)}/${issueNumber}/comments`);
  }

  // POST /api/repos/{u}/{r}/issues/{number}/comments
  addComment(username: string, repoName: string, issueNumber: number, body: string): Observable<IssueComment> {
    return this.http.post<IssueComment>(`${this.getBaseUrl(username, repoName)}/${issueNumber}/comments`, { body });
  }
}