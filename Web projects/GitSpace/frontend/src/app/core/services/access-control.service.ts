import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AccessControlService {

  constructor() { }

  canView(repo: any): boolean {
    // TODO: Issue 41
    // if (!repo.isPrivate) return true;
    // return this.authService.isAuthenticated() && this.authService.getCurrentUser().id === repo.ownerId;
    throw new Error('TODO: Implement canView');
  }

  canWrite(repo: any): boolean {
    // TODO: Issue 41
    // return this.authService.isAuthenticated() && this.authService.getCurrentUser().id === repo.ownerId;
    throw new Error('TODO: Implement canWrite');
  }
}