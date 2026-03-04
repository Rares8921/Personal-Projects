import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, UserProfileDto, UserSettingsDto } from '../../shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  // GET /api/users/search?query=...
  search(query: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/search`, { params: { query } });
  }

  // GET /api/users/{username}
  getProfile(username: string): Observable<UserProfileDto> {
    return this.http.get<UserProfileDto>(`${this.apiUrl}/${username}`);
  }

  // PUT /api/users/settings
  updateSettings(dto: UserSettingsDto): Observable<any> {
    return this.http.put(`${this.apiUrl}/settings`, dto);
  }

  // POST /api/users/{username}/follow
  follow(username: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${username}/follow`, {});
  }

  // DELETE /api/users/{username}/follow
  unfollow(username: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${username}/follow`);
  }
}