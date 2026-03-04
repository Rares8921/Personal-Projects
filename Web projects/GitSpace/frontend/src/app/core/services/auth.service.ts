import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators'; 
import { Router } from '@angular/router';

import { environment } from '../../../environments/environment';
import { User } from '../../shared/models/user.model';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/models/auth.models';
import { UserService } from './user.service'; 

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl = `${environment.apiUrl}/auth`;
  
  private readonly USER_KEY = 'currentUser';
  private readonly TOKEN_KEY = 'token';

  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;

  constructor(
    private http: HttpClient, 
    private router: Router,
    private userService: UserService 
  ) {
    const storedUser = localStorage.getItem(this.USER_KEY);
    this.currentUserSubject = new BehaviorSubject<User | null>(storedUser ? JSON.parse(storedUser) : null);
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  login(credentials: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.authUrl}/login`, credentials)
      .pipe(
        switchMap(response => {
          localStorage.setItem(this.TOKEN_KEY, response.token);
          return this.userService.getProfile(response.username).pipe(
            map(profile => {
              const user: User = { 
                  id: profile.id,
                  username: profile.username, 
                  token: response.token,
                  email: '',
                  avatarUrl: profile.avatarUrl,
                  fullName: profile.fullName,
                  bio: profile.bio,
                  location: profile.location,
                  website: profile.website
              };
              
              // 4. Salvam user-ul complet in LocalStorage
              localStorage.setItem(this.USER_KEY, JSON.stringify(user));
              
              this.currentUserSubject.next(user);
              return user;
            })
          );
        })
      );
  }

  // Backend: POST /api/auth/register -> returneaza Map("message": "...")
  register(data: RegisterRequest) {
    return this.http.post(`${this.authUrl}/register`, data);
  }

  logout() {
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken(); 
  }

  // Backend: POST /api/auth/forgot-password -> body { email: "..." }
  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.authUrl}/forgot-password`, { email });
  }

  // Backend: POST /api/auth/reset-password -> body { token: "...", newPassword: "..." }
  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.authUrl}/reset-password`, { token, newPassword });
  }

  updateLocalUser(partialUser: Partial<User>) {
    const current = this.currentUserSubject.value;
    if (current) {
      const updatedUser = { ...current, ...partialUser };
      localStorage.setItem(this.USER_KEY, JSON.stringify(updatedUser));
      this.currentUserSubject.next(updatedUser);
    }
  }
}