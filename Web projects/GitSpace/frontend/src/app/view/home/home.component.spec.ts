import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HomeComponent } from './home.component';
import { AuthService } from '../../core/services/auth.service';
import { ActivityService } from '../../core/services/activity.service';
import { SnippetService } from '../../core/services/snippet.service';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { By } from '@angular/platform-browser';
import { User } from '../../shared/models/user.model';
import { Activity } from '../../shared/models/activity.model';
import { Snippet } from '../../shared/models/snippet.model';

const mockUser: User = { 
  id: 1, 
  username: 'testuser', 
  email: 'test@dacia.git', 
  token: 'jwt-token' 
};

const mockActivities: Activity[] = [
  { id: 1, type: 'PUSH', description: 'Pushed code', actorUsername: 'dev1', timestamp: new Date().toISOString(), repoName: 'core-engine' },
  { id: 2, type: 'FORK', description: 'Forked repo', actorUsername: 'dev2', timestamp: new Date().toISOString(), repoName: 'ui-kit' }
];

const mockSnippets: Snippet[] = [
  { id: 101, filename: 'main.ts', description: 'Javascript print', content: 'console.log()', language: 'TypeScript', authorUsername: 'dev1', createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() },
  { id: 102, filename: 'style.css', description: 'CSS trick', content: 'body { margin: 0 }', language: 'CSS', authorUsername: 'dev2', createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() }
];

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let activityServiceSpy: jasmine.SpyObj<ActivityService>;
  let snippetServiceSpy: jasmine.SpyObj<SnippetService>;
  
  let currentUserSubject: BehaviorSubject<User | null>;

  beforeEach(async () => {
    currentUserSubject = new BehaviorSubject<User | null>(null);

    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUser$: currentUserSubject.asObservable()
    });
    activityServiceSpy = jasmine.createSpyObj('ActivityService', ['getMyFeed']);
    snippetServiceSpy = jasmine.createSpyObj('SnippetService', ['getPublic']);

    await TestBed.configureTestingModule({
      declarations: [ HomeComponent ],
      imports: [ RouterTestingModule ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: SnippetService, useValue: snippetServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should show landing page when user is NOT logged in', fakeAsync(() => {
    currentUserSubject.next(null);
    
    fixture.detectChanges();
    tick();

    const landing = fixture.debugElement.query(By.css('.landing-wrapper'));
    const community = fixture.debugElement.query(By.css('.community-wrapper'));
    
    expect(landing).toBeTruthy();
    expect(community).toBeFalsy();
    
    expect(activityServiceSpy.getMyFeed).not.toHaveBeenCalled();
    expect(snippetServiceSpy.getPublic).not.toHaveBeenCalled();
  }));

  it('should show community feed when user IS logged in', fakeAsync(() => {
    activityServiceSpy.getMyFeed.and.returnValue(of(mockActivities));
    snippetServiceSpy.getPublic.and.returnValue(of(mockSnippets));
    
    currentUserSubject.next(mockUser);
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const landing = fixture.debugElement.query(By.css('.landing-wrapper'));
    const community = fixture.debugElement.query(By.css('.community-wrapper'));
    
    expect(landing).toBeFalsy();
    expect(community).toBeTruthy();

    expect(activityServiceSpy.getMyFeed).toHaveBeenCalled();
    expect(snippetServiceSpy.getPublic).toHaveBeenCalled();
  }));

  it('should display feed items and trending snippets', fakeAsync(() => {
    activityServiceSpy.getMyFeed.and.returnValue(of(mockActivities));
    snippetServiceSpy.getPublic.and.returnValue(of(mockSnippets));
    
    currentUserSubject.next(mockUser);
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const feedItems = fixture.debugElement.queryAll(By.css('.feed-card'));
    expect(feedItems.length).toBe(2);
    expect(feedItems[0].nativeElement.textContent).toContain('dev1');
    expect(feedItems[0].nativeElement.textContent).toContain('core-engine');

    const snippetItems = fixture.debugElement.queryAll(By.css('.snippet-list li'));
    expect(snippetItems.length).toBe(2); 
    expect(snippetItems[0].nativeElement.textContent).toContain('main.ts');
  }));

  it('should display empty states when data is missing', fakeAsync(() => {
    activityServiceSpy.getMyFeed.and.returnValue(of([]));
    snippetServiceSpy.getPublic.and.returnValue(of([]));
    
    currentUserSubject.next(mockUser);
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const emptyFeed = fixture.debugElement.query(By.css('.empty-feed'));
    expect(emptyFeed).toBeTruthy();
    expect(emptyFeed.nativeElement.textContent).toContain('Quiet Signal');

    const noSnippets = fixture.debugElement.query(By.css('.no-data'));
    expect(noSnippets).toBeTruthy();
    expect(noSnippets.nativeElement.textContent).toContain('No public snippets');
  }));

  it('should show loading spinner while fetching data', () => {
    activityServiceSpy.getMyFeed.and.returnValue(new Subject<Activity[]>().asObservable());
    snippetServiceSpy.getPublic.and.returnValue(new Subject<Snippet[]>().asObservable());

    currentUserSubject.next(mockUser);
    
    component.ngOnInit();
    
    expect(component.loadingFeed).toBeTrue();
  });
});