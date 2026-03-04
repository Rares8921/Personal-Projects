import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Repository } from '../../shared/models/repo.model';
import { GitTreeEntry } from 'src/app/shared/models/git.model';
import { Subject } from 'rxjs';

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // current page
}

@Injectable({
  providedIn: 'root'
})
export class RepositoryService {
  private apiUrl = `${environment.apiUrl}/repos`;
  
  public refreshRepo$ = new Subject<void>();

  constructor(private http: HttpClient) { }

  // POST /api/repos
  createRepo(data: { name: string; description?: string; isPublic: boolean }): Observable<Repository> {
    return this.http.post<Repository>(this.apiUrl, data);
  }

  // GET /api/repos/{username}
  getUserRepos(username: string): Observable<Repository[]> {
    return this.http.get<Repository[]>(`${this.apiUrl}/${username}`);
  }

  // GET /api/repos/{username}/{repoName}
  getRepo(username: string, repoName: string): Observable<Repository> {
    return this.http.get<Repository>(`${this.apiUrl}/${username}/${repoName}`);
  }

  getExploreRepos(page: number = 0, size: number = 20): Observable<Page<Repository>> {
    // GET /api/repos/explore?page=0&size=20
    return this.http.get<Page<Repository>>(`${this.apiUrl}/explore?page=${page}&size=${size}`);
  }

  // PUT /api/repos/{username}/{repoName}
  updateRepo(username: string, repoName: string, data: { description?: string; isPublic?: boolean; isArchived?: boolean; }): Observable<Repository> {
    return this.http.put<Repository>(`${this.apiUrl}/${username}/${repoName}`, data);
  }

  // DELETE /api/repos/{username}/{repoName}
  deleteRepo(username: string, repoName: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${username}/${repoName}`);
  }

  getTree(username: string, repoName: string, branch: string, path?: string): Observable<GitTreeEntry[]> {
    let url = `${this.apiUrl}/${username}/${repoName}/git/tree/${branch}`;
    if (path) {
      url += `/${path}`;
    }
    return this.http.get<GitTreeEntry[]>(url);
  }

  getFileContent(username: string, repoName: string, branch: string, filePath: string): Observable<string> {
    const url = `${this.apiUrl}/${username}/${repoName}/git/blob/${branch}/${filePath}`;
    return this.http.get(url, { responseType: 'text' });
  }

  updateFile(username: string, repoName: string, path: string, data: { branch: string, content: string, message: string }): Observable<any> {
    return this.http.put(`${this.apiUrl}/${username}/${repoName}/contents/${path}`, data);
  }

  deleteFile(username: string, repoName: string, path: string, data: { branch: string, message: string }): Observable<any> {
    return this.http.request('delete', `${this.apiUrl}/${username}/${repoName}/contents/${path}`, { body: data });
  }

  getRepoStats(username: string, repoName: string, branch: string = 'master'): Observable<any> {
    return this.http.get(`${this.apiUrl}/${username}/${repoName}/stats?branch=${branch}`);
  }

  /**
   * Fetch the list of branches for a repository.
   * Usage: Used in PR creation dropdowns and branch switchers.
   */
  getBranches(username: string, repoName: string): Observable<string[]> {
    // GET /api/repos/{username}/{repoName}/branches
    // Endpoint-ul a fost deja definit in RepositoryController: listBranches
    return this.http.get<string[]>(`${this.apiUrl}/${username}/${repoName}/branches`);
  }
}