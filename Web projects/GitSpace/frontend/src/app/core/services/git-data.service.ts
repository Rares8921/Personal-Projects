import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { GitTreeEntry, GitCommit } from '../../shared/models/git.model';

@Injectable({ providedIn: 'root' })
export class GitService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getBaseUrl(username: string, repoName: string): string {
    return `${this.apiUrl}/repos/${username}/${repoName}/git`;
  }

  // GET /tree/{branch}/**
  getTree(username: string, repoName: string, branch: string, path: string = ''): Observable<GitTreeEntry[]> {
    let url = `${this.getBaseUrl(username, repoName)}/tree/${branch}`;
    if (path) {
      url += `/${path}`;
    }
    return this.http.get<GitTreeEntry[]>(url);
  }

  // GET /blob/{branch}/**
  getBlob(username: string, repoName: string, branch: string, path: string): Observable<string> {
    const url = `${this.getBaseUrl(username, repoName)}/blob/${branch}/${path}`;
    // Specificam responseType: 'text' pentru ca backend-ul poate returna text simplu
    return this.http.get(url, { responseType: 'text' });
  }

  // GET /commits/{branch}
  getCommits(username: string, repoName: string, branch: string): Observable<GitCommit[]> {
    return this.http.get<GitCommit[]>(`${this.getBaseUrl(username, repoName)}/commits/${branch}`);
  }

  // GET /branches
  getBranches(username: string, repoName: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.getBaseUrl(username, repoName)}/branches`);
  }

  getFileContent(username: string, repoName: string, branch: string, path: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/repos/${username}/${repoName}/git/blob/${branch}/${path}`, { responseType: 'text' });
  }

  // CREATE / UPDATE -> Folosim PUT conform Controller-ului tău
  createOrUpdateFile(username: string, repo: string, path: string, body: any): Observable<any> {
    const cleanPath = path.startsWith('/') ? path.substring(1) : path;
    return this.http.put(`${this.apiUrl}/repos/${username}/${repo}/contents/${cleanPath}`, body);
  }

  // DELETE -> Necesită un request custom pentru a trimite body (mesajul de commit)
  deleteFile(username: string, repo: string, path: string, body: any): Observable<any> {
    const cleanPath = path.startsWith('/') ? path.substring(1) : path;
    
    return this.http.request('delete', `${this.apiUrl}/repos/${username}/${repo}/contents/${cleanPath}`, {
      body: body,
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    });
  }

  // URL pentru Download
  getArchiveUrl(username: string, repo: string, branch: string): string {
    return `${this.apiUrl}/repos/${username}/${repo}/contents/archive/${branch}.zip`;
  }

  downloadRepoArchive(username: string, repo: string, branch: string): Observable<Blob> {
    const url = `${this.apiUrl}/repos/${username}/${repo}/contents/archive/${branch}.zip`;
    return this.http.get(url, { responseType: 'blob' });
  }
}