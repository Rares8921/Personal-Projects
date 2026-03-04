import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Activity } from '../../shared/models/activity.model';

@Injectable({ providedIn: 'root' })
export class ActivityService {
  private apiUrl = `${environment.apiUrl}/activity`;

  constructor(private http: HttpClient) {}

  // GET /api/activity (Feed-ul userului logat)
  getMyFeed(): Observable<Activity[]> {
    return this.http.get<Activity[]>(this.apiUrl);
  }

  // GET /api/activity/{username} (Activitatea publica a altui user)
  getUserActivity(username: string): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.apiUrl}/${username}`);
  }
}