import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../shared/models/user.model';
import { Observable } from 'rxjs';
import { Router } from '@angular/router'; 

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  
  currentUser$: Observable<User | null>;
  searchQuery: string = '';

  constructor(
    private authService: AuthService, 
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  logout() {
    this.authService.logout();
  }

  onSearch() {
    if (this.searchQuery && this.searchQuery.trim().length > 0) {
      // Navigheaza catre /search?q=valoare
      this.router.navigate(['/search'], { queryParams: { q: this.searchQuery } });
      
      this.searchQuery = ''; 
    }
  }
}