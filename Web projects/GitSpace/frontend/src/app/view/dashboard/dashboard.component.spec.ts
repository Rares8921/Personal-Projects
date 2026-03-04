import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../../core/services/auth.service';
import { RepositoryService } from '../../core/services/repo.service';
import { ActivityService } from '../../core/services/activity.service';
import { BehaviorSubject, of } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { User } from '../../shared/models/user.model';
import { Repository } from '../../shared/models/repo.model';
import { Activity } from '../../shared/models/activity.model';

const mockUser: User = { 
  id: 1, 
  username: 'testuser', 
  email: 'test@dacia.git', 
  token: 'x' 
};

const mockRepos: Repository[] = [
  { id: 1, name: 'repo-1', starsCount: 10, forksCount: 2, isPublic: true, defaultBranch: 'main', ownerUsername: 'testuser', updatedAt: new Date().toISOString() },
  { id: 2, name: 'repo-2', starsCount: 5, forksCount: 1, isPublic: false, defaultBranch: 'main', ownerUsername: 'testuser', updatedAt: new Date().toISOString() }
];

const mockActivity: Activity[] = [
  { id: 1, type: 'PUSH', description: 'Pushed code', actorUsername: 'testuser', timestamp: new Date().toISOString(), repoName: 'repo-1' }
];

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let repoServiceSpy: jasmine.SpyObj<RepositoryService>;
  let activityServiceSpy: jasmine.SpyObj<ActivityService>;
  
  let currentUserSubject: BehaviorSubject<User | null>;

  beforeEach(async () => {
    currentUserSubject = new BehaviorSubject<User | null>(mockUser);

    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUser$: currentUserSubject.asObservable(),
      currentUserValue: mockUser 
    });
    
    repoServiceSpy = jasmine.createSpyObj('RepositoryService', ['getUserRepos']);
    activityServiceSpy = jasmine.createSpyObj('ActivityService', ['getMyFeed']);

    await TestBed.configureTestingModule({
      declarations: [ DashboardComponent ],
      imports: [ 
        RouterTestingModule,
        FormsModule 
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: RepositoryService, useValue: repoServiceSpy },
        { provide: ActivityService, useValue: activityServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    repoServiceSpy.getUserRepos.and.returnValue(of([]));
    activityServiceSpy.getMyFeed.and.returnValue(of([]));
    
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load data and calculate stats for logged in user', fakeAsync(() => {
    repoServiceSpy.getUserRepos.and.returnValue(of(mockRepos));
    activityServiceSpy.getMyFeed.and.returnValue(of(mockActivity));

    fixture.detectChanges(); 
    
    tick(); 
    fixture.detectChanges();

    expect(repoServiceSpy.getUserRepos).toHaveBeenCalledWith('testuser');
    expect(activityServiceSpy.getMyFeed).toHaveBeenCalled();
  }));

  it('should display repository list', fakeAsync(() => {
    repoServiceSpy.getUserRepos.and.returnValue(of(mockRepos));
    activityServiceSpy.getMyFeed.and.returnValue(of([]));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const repoCards = fixture.debugElement.queryAll(By.css('.repo-item'));
    
    expect(repoCards.length).toBeGreaterThanOrEqual(1);
    expect(repoCards[0].nativeElement.textContent).toContain('repo-1');
  }));

  it('should show loading state initially', () => {
    repoServiceSpy.getUserRepos.and.returnValue(new BehaviorSubject<Repository[]>([]).asObservable());
    activityServiceSpy.getMyFeed.and.returnValue(of([]));

    component.ngOnInit();
  
    expect(component.loadingRepos).toBeTrue();
  });
});