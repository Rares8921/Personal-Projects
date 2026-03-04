import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PullRequest, PullRequestFile } from '../../shared/models/pr.model';

@Injectable({ providedIn: 'root' })
export class PullRequestService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getBaseUrl(username: string, repoName: string): string {
    return `${this.apiUrl}/repos/${username}/${repoName}/pulls`;
  }

  // GET /api/repos/{u}/{r}/pulls
  list(username: string, repoName: string): Observable<PullRequest[]> {
    return this.http.get<PullRequest[]>(this.getBaseUrl(username, repoName));
  }

  // POST /api/repos/{u}/{r}/pulls
  create(username: string, repoName: string, data: Partial<PullRequest>): Observable<PullRequest> {
    return this.http.post<PullRequest>(this.getBaseUrl(username, repoName), data);
  }

  // GET .../{prNumber}
  get(username: string, repoName: string, prNumber: number): Observable<PullRequest> {
    return this.http.get<PullRequest>(`${this.getBaseUrl(username, repoName)}/${prNumber}`);
  }

  // GET .../{prNumber}/files (DIFF)
  getFiles(username: string, repoName: string, prNumber: number): Observable<PullRequestFile[]> {
    return this.http.get<PullRequestFile[]>(`${this.getBaseUrl(username, repoName)}/${prNumber}/files`);
  }

  // POST .../{prNumber}/reviews
  addReview(username: string, repoName: string, prNumber: number, state: string): Observable<any> {
    return this.http.post(`${this.getBaseUrl(username, repoName)}/${prNumber}/reviews`, { state });
  }

  // POST .../{prNumber}/merge
  merge(username: string, repoName: string, prNumber: number): Observable<any> {
    return this.http.post(`${this.getBaseUrl(username, repoName)}/${prNumber}/merge`, {});
  }

  getComments(username: string, repoName: string, prNumber: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.getBaseUrl(username, repoName)}/${prNumber}/comments`);
  }

  addComment(username: string, repoName: string, prNumber: number, commentData: any): Observable<any> {
    return this.http.post<any>(`${this.getBaseUrl(username, repoName)}/${prNumber}/comments`, commentData);
  }

  submitReview(username: string, repoName: string, prNumber: number, state: string): Observable<any> {
    return this.http.post(`${this.getBaseUrl(username, repoName)}/${prNumber}/reviews`, { state }, { responseType: 'text' });
  }
}