import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CollaboratorDto {
  id: number;
  userId: number;
  username: string;
  fullName: string;
  avatarUrl: string;
  role: 'READ' | 'WRITE' | 'ADMIN';
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'REVOKED';
  invitedAt: string;
  acceptedAt?: string;
  invitedByUsername?: string;
  repositoryName?: string;
}

export interface InviteCollaboratorRequest {
  username: string;
  role: 'READ' | 'WRITE' | 'ADMIN';
}

@Injectable({
  providedIn: 'root'
})
export class CollaboratorService {
  private apiUrl = `${environment.apiUrl}/repositories`;
  private invitationsUrl = `${environment.apiUrl}/invitations`;

  constructor(private http: HttpClient) { }

  getCollaborators(repositoryId: number): Observable<CollaboratorDto[]> {
    return this.http.get<CollaboratorDto[]>(`${this.apiUrl}/${repositoryId}/collaborators`);
  }

  getActiveCollaborators(repositoryId: number): Observable<CollaboratorDto[]> {
    return this.http.get<CollaboratorDto[]>(`${this.apiUrl}/${repositoryId}/collaborators/active`);
  }

  getPendingInvitations(repositoryId: number): Observable<CollaboratorDto[]> {
    return this.http.get<CollaboratorDto[]>(`${this.apiUrl}/${repositoryId}/collaborators/pending`);
  }

  inviteCollaborator(repositoryId: number, request: InviteCollaboratorRequest): Observable<CollaboratorDto> {
    return this.http.post<CollaboratorDto>(`${this.apiUrl}/${repositoryId}/collaborators`, request);
  }

  removeCollaborator(repositoryId: number, collaboratorId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${repositoryId}/collaborators/${collaboratorId}`);
  }

  checkWriteAccess(repositoryId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${repositoryId}/collaborators/check-access`);
  }

  getMyPendingInvitations(): Observable<CollaboratorDto[]> {
    return this.http.get<CollaboratorDto[]>(`${this.invitationsUrl}/pending`);
  }

  acceptInvitation(invitationId: number): Observable<CollaboratorDto> {
    return this.http.post<CollaboratorDto>(`${this.invitationsUrl}/${invitationId}/accept`, {});
  }

  rejectInvitation(invitationId: number): Observable<void> {
    return this.http.post<void>(`${this.invitationsUrl}/${invitationId}/reject`, {});
  }
}
