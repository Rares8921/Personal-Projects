import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FileUploadResponse } from '../../shared/models/api-responses.model';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private apiUrl = `${environment.apiUrl}/upload`;

  constructor(private http: HttpClient) { }

  uploadAvatar(file: File): Observable<FileUploadResponse> {
    // TODO: Issue 14, 18
    // const formData = new FormData();
    // formData.append('file', file);
    // return this.http.post<FileUploadResponse>(`${this.apiUrl}/avatar`, formData);
    throw new Error('TODO: Implement uploadAvatar');
  }

  uploadAttachment(file: File): Observable<FileUploadResponse> {
    // TODO: Issue 14
    // const formData = new FormData();
    // formData.append('file', file);
    // return this.http.post<FileUploadResponse>(`${this.apiUrl}/attachment`, formData);
    throw new Error('TODO: Implement uploadAttachment');
  }
}