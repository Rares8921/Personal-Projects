import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Snippet } from '../../shared/models/snippet.model';

@Injectable({ providedIn: 'root' })
export class SnippetService {
  private apiUrl = `${environment.apiUrl}/snippets`;

  constructor(private http: HttpClient) {}

  // POST /api/snippets
  create(data: Partial<Snippet>): Observable<Snippet> {
    return this.http.post<Snippet>(this.apiUrl, data);
  }

  // GET /api/snippets (Public ones)
  getPublic(): Observable<Snippet[]> {
    return this.http.get<Snippet[]>(this.apiUrl);
  }

  // GET /api/snippets/{id}
  getById(id: number): Observable<Snippet> {
    return this.http.get<Snippet>(`${this.apiUrl}/${id}`);
  }
}