import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserProfileComponent } from './user-profile.component';
import { UserService } from '../../core/services/user.service';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';
import { By } from '@angular/platform-browser';
import { UserProfileDto } from '../../shared/models/user.model';
import { Repository } from '../../shared/models/repo.model';

const mockRepos: Repository[] = [
  {
    id: 1,
    name: 'project-alpha',
    description: 'Alpha project',
    isPublic: true,
    language: 'TypeScript',
    starsCount: 10,
    forksCount: 2,
    ownerUsername: 'testuser',
    updatedAt: new Date().toISOString(),
    defaultBranch: 'main'
  },
  {
    id: 2,
    name: 'secret-beta',
    description: 'Beta project',
    isPublic: false,
    language: 'Python',
    starsCount: 0,
    forksCount: 0,
    ownerUsername: 'testuser',
    updatedAt: new Date().toISOString(),
    defaultBranch: 'dev'
  }
];

const mockProfile: UserProfileDto = {
  id: 1,
  username: 'testuser',
  fullName: 'Test User',
  avatarUrl: 'assets/avatar.png',
  bio: 'Full Stack Developer',
  location: 'New York',
  website: 'https://example.com',
  createdAt: new Date().toISOString(),
  followers: 100,
  following: 50,
  repos: mockRepos
};

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getProfile']);

    await TestBed.configureTestingModule({
      declarations: [ UserProfileComponent ],
      imports: [ RouterTestingModule ],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { 
          provide: ActivatedRoute, 
          useValue: { params: of({ username: 'testuser' }) } 
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load profile and repositories on init', fakeAsync(() => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(userServiceSpy.getProfile).toHaveBeenCalledWith('testuser');
    expect(component.profile).toEqual(mockProfile);
    expect(component.loading).toBeFalse();

    const name = fixture.debugElement.query(By.css('.names h1'));
    expect(name.nativeElement.textContent).toContain('Test User');

    const username = fixture.debugElement.query(By.css('.names .username'));
    expect(username.nativeElement.textContent).toContain('testuser');

    const stats = fixture.debugElement.query(By.css('.meta-item strong'));
    expect(stats.nativeElement.textContent).toContain('100');

    const repoCards = fixture.debugElement.queryAll(By.css('.repo-card-mini'));
    expect(repoCards.length).toBe(2);
    expect(repoCards[0].nativeElement.textContent).toContain('project-alpha');
    expect(repoCards[0].nativeElement.textContent).toContain('Public');
    expect(repoCards[1].nativeElement.textContent).toContain('secret-beta');
    expect(repoCards[1].nativeElement.textContent).toContain('Private');
  }));

  it('should display loading state initially', fakeAsync(() => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile).pipe(delay(10)));
    
    fixture.detectChanges();
    
    expect(component.loading).toBeTrue();
    
    const loadingScreen = fixture.debugElement.query(By.css('.loading-screen'));
    expect(loadingScreen).toBeTruthy();
    expect(loadingScreen.nativeElement.textContent).toContain('Accessing user database');

    tick(10);
    fixture.detectChanges();
    
    expect(component.loading).toBeFalse();
  }));

  it('should handle user not found error', fakeAsync(() => {
    userServiceSpy.getProfile.and.returnValue(throwError(() => new Error('Not found')));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.profile).toBeNull();
    expect(component.error).toBe('User not found.');

    const errorMsg = fixture.debugElement.query(By.css('.error-msg h3'));
    expect(errorMsg.nativeElement.textContent).toContain('User not found');
  }));

  it('should display empty state for repositories', fakeAsync(() => {
    const emptyProfile = { ...mockProfile, repos: [] };
    userServiceSpy.getProfile.and.returnValue(of(emptyProfile));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const emptyState = fixture.debugElement.query(By.css('.empty-state'));
    expect(emptyState).toBeTruthy();
    expect(emptyState.nativeElement.textContent).toContain("doesn't have any public repositories");
  }));

  it('should return correct language colors', () => {
    expect(component.getLanguageColor('TypeScript')).toBe('#2b7489');
    expect(component.getLanguageColor('Java')).toBe('#b07219');
    expect(component.getLanguageColor('Python')).toBe('#3572A5');
    expect(component.getLanguageColor('Unknown')).toBe('#cccccc');
    expect(component.getLanguageColor(undefined)).toBe('#cccccc');
  });

  it('should render language dots with correct colors', fakeAsync(() => {
    userServiceSpy.getProfile.and.returnValue(of(mockProfile));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const dots = fixture.debugElement.queryAll(By.css('.dot'));
    expect(dots[0].styles['backgroundColor']).toBe('rgb(43, 116, 137)');
    expect(dots[1].styles['backgroundColor']).toBe('rgb(53, 114, 165)');
  }));
});