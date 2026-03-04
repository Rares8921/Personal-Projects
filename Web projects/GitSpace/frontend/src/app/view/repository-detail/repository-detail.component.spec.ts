import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RepositoryDetailComponent } from './repository-detail.component';
import { RepositoryService } from '../../core/services/repo.service';
import { AuthService } from '../../core/services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';
import { Repository } from '../../shared/models/repo.model';
import { User } from '../../shared/models/user.model';

const mockUser: User = { 
  id: 1, 
  username: 'testuser', 
  email: 'test@dacia.git', 
  token: 'x' 
};

const mockRepo: Repository = {
  id: 101,
  name: 'my-project',
  description: 'A cool project',
  isPublic: true,
  defaultBranch: 'main',
  starsCount: 5,
  forksCount: 2,
  ownerUsername: 'testuser',
  updatedAt: new Date().toISOString()
};

describe('RepositoryDetailComponent', () => {
  let component: RepositoryDetailComponent;
  let fixture: ComponentFixture<RepositoryDetailComponent>;
  let repoServiceSpy: jasmine.SpyObj<RepositoryService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    repoServiceSpy = jasmine.createSpyObj('RepositoryService', ['getRepo']);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUserValue: mockUser
    });

    await TestBed.configureTestingModule({
      declarations: [ RepositoryDetailComponent ],
      imports: [ RouterTestingModule ],
      providers: [
        { provide: RepositoryService, useValue: repoServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { 
          provide: ActivatedRoute, 
          useValue: { 
            params: of({ username: 'testuser', repoName: 'my-project' }),
            snapshot: { paramMap: { get: () => 'val' } },
            parent: null,
            pathFromRoot: [{ snapshot: {}, url: [] }],
            url: of([])
          } 
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load repository details on init', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.repo).toEqual(mockRepo);
    expect(repoServiceSpy.getRepo).toHaveBeenCalledWith('testuser', 'my-project');

    const headerTitle = fixture.debugElement.query(By.css('.repo-link'));
    expect(headerTitle.nativeElement.textContent).toContain('my-project');

    const desc = fixture.debugElement.query(By.css('.repo-description'));
    expect(desc.nativeElement.textContent).toContain('A cool project');
  }));

  it('should handle error when repository is not found', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(throwError(() => new Error('Not found')));

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.repo).toBeNull();
    expect(component.error).toContain('Repository not found');

    const errorMsg = fixture.debugElement.query(By.css('.center-msg.error h2'));
    expect(errorMsg.nativeElement.textContent).toContain('Repository not found');
  }));

  it('should increment star count locally', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));
    fixture.detectChanges();
    tick();

    const initialStars = component.repo!.starsCount;
    component.toggleStar();
    
    expect(component.repo!.starsCount).toBe(initialStars + 1);
  }));

  it('should show Settings tab only if current user is owner', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));

    component.currentUser = mockRepo.ownerUsername;

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const settingsTab = fixture.debugElement.query(By.css('a.tab-item'));
    expect(settingsTab).toBeTruthy();
  }));

});

describe('RepositoryDetailComponent (As Guest)', () => {
  let component: RepositoryDetailComponent;
  let fixture: ComponentFixture<RepositoryDetailComponent>;
  let repoServiceSpy: jasmine.SpyObj<RepositoryService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    repoServiceSpy = jasmine.createSpyObj('RepositoryService', ['getRepo']);
    
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUserValue: { ...mockUser, username: 'guest' }
    });

    await TestBed.configureTestingModule({
      declarations: [ RepositoryDetailComponent ],
      imports: [ RouterTestingModule ],
      providers: [
        { provide: RepositoryService, useValue: repoServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { 
          provide: ActivatedRoute, 
          useValue: { 
            params: of({ username: 'testuser', repoName: 'my-project' }),
            snapshot: { paramMap: { get: () => 'val' } },
            parent: null,
            pathFromRoot: [{ snapshot: {}, url: [] }],
            url: of([])
          } 
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailComponent);
    component = fixture.componentInstance;
  });

  it('should HIDE Settings tab if current user is NOT owner', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const settingsTab = fixture.debugElement.query(By.css('a[routerLink="settings"]'));
    expect(settingsTab).toBeFalsy();
  }));
});