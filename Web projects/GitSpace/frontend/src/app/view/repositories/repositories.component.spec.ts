import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RepositoriesComponent } from './repositories.component';
import { RepositoryService } from '../../core/services/repo.service';
import { AuthService } from '../../core/services/auth.service';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { Repository } from 'src/app/shared/models/repo.model';
import { User } from '../../shared/models/user.model';
import { Subject } from 'rxjs'; // Adauga importul sus

const mockUser: User = {
  id: 1,
  username: 'testuser',
  email: 'test@dacia.git',
  token: 'jwt-token'
};

const mockRepos: Repository[] = [
  {
    id: 101,
    name: 'turbo-engine',
    description: 'High performance engine control unit',
    isPublic: true,
    defaultBranch: 'main',
    starsCount: 5,
    forksCount: 2,
    ownerUsername: 'testuser',
    updatedAt: new Date().toISOString()
  },
  {
    id: 102,
    name: 'gearbox-v2',
    description: '',
    isPublic: false, // Private repo
    defaultBranch: 'dev',
    starsCount: 0,
    forksCount: 0,
    ownerUsername: 'testuser',
    updatedAt: new Date().toISOString()
  }
];

describe('RepositoriesComponent', () => {
  let component: RepositoriesComponent;
  let fixture: ComponentFixture<RepositoriesComponent>;
  
  // Spies for services
  let repoServiceSpy: jasmine.SpyObj<RepositoryService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  
  let currentUserSubject: BehaviorSubject<User | null>;

  beforeEach(async () => {
    // 1. Setup Spies
    repoServiceSpy = jasmine.createSpyObj('RepositoryService', ['getUserRepos']);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUser$: new BehaviorSubject<User | null>(null) 
    });

    currentUserSubject = authServiceSpy.currentUser$ as BehaviorSubject<User | null>;

    await TestBed.configureTestingModule({
      declarations: [ RepositoriesComponent ],
      imports: [ RouterTestingModule ], // routerLink
      providers: [
        { provide: RepositoryService, useValue: repoServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoriesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should show loading spinner initially', fakeAsync(() => {
    currentUserSubject.next(mockUser); 

    const reposSubject = new Subject<Repository[]>();
    repoServiceSpy.getUserRepos.and.returnValue(reposSubject.asObservable());

    component.ngOnInit();
    fixture.detectChanges(); 

    expect(component.loading).toBeTrue();

    const loadingEl = fixture.debugElement.query(By.css('.loading-state'));
    expect(loadingEl).toBeTruthy();

    reposSubject.next(mockRepos);
    tick(); 
    fixture.detectChanges(); 

    expect(component.loading).toBeFalse();
    const loadingElAfter = fixture.debugElement.query(By.css('.loading-state'));
    expect(loadingElAfter).toBeFalsy(); 
  }));

  it('should load repositories and display them', fakeAsync(() => {
    currentUserSubject.next(mockUser);
    repoServiceSpy.getUserRepos.and.returnValue(of(mockRepos));

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.repositories.length).toBe(2);

    const cards = fixture.debugElement.queryAll(By.css('.repo-card'));
    expect(cards.length).toBe(2);

    const firstTitle = cards[0].query(By.css('.repo-title a')).nativeElement.textContent;
    expect(firstTitle).toContain('testuser / turbo-engine');

    const publicBadge = cards[0].query(By.css('.badge.public'));
    expect(publicBadge).toBeTruthy();
    
    const privateBadge = cards[1].query(By.css('.badge'));
    expect(privateBadge).toBeTruthy();
    expect(privateBadge.classes['public']).toBeFalsy();
  }));

  it('should display empty state message when no repos found', fakeAsync(() => {
    currentUserSubject.next(mockUser);
    repoServiceSpy.getUserRepos.and.returnValue(of([])); // Empty Array

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const emptyState = fixture.debugElement.query(By.css('.empty-state'));
    expect(emptyState).toBeTruthy();
    expect(emptyState.nativeElement.textContent).toContain('No repositories found');
    
    const grid = fixture.debugElement.query(By.css('.repo-grid'));
    expect(grid).toBeFalsy();
  }));

  it('should stop loading on error', fakeAsync(() => {
    currentUserSubject.next(mockUser);
    repoServiceSpy.getUserRepos.and.returnValue(throwError(() => new Error('Network fail')));

    spyOn(console, 'error');

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.repositories.length).toBe(0);
  }));

  it('should not load repos if user is not logged in', () => {
    currentUserSubject.next(null); 
    
    fixture.detectChanges();

    expect(repoServiceSpy.getUserRepos).not.toHaveBeenCalled();
    expect(component.loading).toBeFalse();
  });
});