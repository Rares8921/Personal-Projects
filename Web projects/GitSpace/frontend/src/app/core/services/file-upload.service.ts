import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FileUploadResponse } from '../../shared/models/api-responses.model';

@Injectable({ providedIn: 'root' })
export class FileUploadService {
  private apiUrl = `${environment.apiUrl}/upload`;

  constructor(private http: HttpClient) {}

  // POST /api/upload/avatar
  uploadAvatar(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FileUploadResponse>(`${this.apiUrl}/avatar`, formData);
  }

  // POST /api/upload/attachment
  uploadAttachment(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<FileUploadResponse>(`${this.apiUrl}/attachment`, formData);
  }
}