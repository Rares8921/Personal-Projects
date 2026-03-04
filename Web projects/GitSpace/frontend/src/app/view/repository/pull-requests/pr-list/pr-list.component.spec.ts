import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { PrListComponent } from './pr-list.component';
import { PullRequestService } from '../../../../core/services/pr.service';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';
import { By } from '@angular/platform-browser';
import { PullRequest } from '../../../../shared/models/pr.model';

const mockPrs: PullRequest[] = [
  {
    id: 1,
    prNumber: 10,
    title: 'Fix navigation bug',
    state: 'OPEN',
    authorUsername: 'dev_user',
    fromBranch: 'bugfix/nav',
    toBranch: 'main',
    createdAt: new Date().toISOString()
  },
  {
    id: 2,
    prNumber: 11,
    title: 'Add new dashboard',
    state: 'MERGED',
    authorUsername: 'senior_dev',
    fromBranch: 'feat/dashboard',
    toBranch: 'main',
    createdAt: new Date().toISOString()
  }
];

describe('PrListComponent', () => {
  let component: PrListComponent;
  let fixture: ComponentFixture<PrListComponent>;
  let prServiceSpy: jasmine.SpyObj<PullRequestService>;

  beforeEach(async () => {
    prServiceSpy = jasmine.createSpyObj('PullRequestService', ['list']);

    await TestBed.configureTestingModule({
      declarations: [ PrListComponent ],
      imports: [ RouterTestingModule ],
      providers: [
        { provide: PullRequestService, useValue: prServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              parent: {
                params: of({ username: 'testuser', repoName: 'testrepo' })
              }
            }
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    prServiceSpy.list.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load pull requests on init', fakeAsync(() => {
    prServiceSpy.list.and.returnValue(of(mockPrs));
    
    fixture.detectChanges(); 
    tick();
    fixture.detectChanges();

    expect(component.username).toBe('testuser');
    expect(component.repoName).toBe('testrepo');
    expect(prServiceSpy.list).toHaveBeenCalledWith('testuser', 'testrepo');
    expect(component.prs.length).toBe(2);
    expect(component.loading).toBeFalse();
  }));

  it('should show loading state', fakeAsync(() => {
    prServiceSpy.list.and.returnValue(of(mockPrs).pipe(delay(10)));
    
    fixture.detectChanges();
    expect(component.loading).toBeTrue();
    
    const loadingEl = fixture.debugElement.query(By.css('.loading-area'));
    expect(loadingEl).toBeTruthy();

    tick(10);
    fixture.detectChanges();
    expect(component.loading).toBeFalse();
  }));

  it('should display PR list in DOM', fakeAsync(() => {
    prServiceSpy.list.and.returnValue(of(mockPrs));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const rows = fixture.debugElement.queryAll(By.css('.pr-row'));
    expect(rows.length).toBe(2);

    const firstTitle = rows[0].query(By.css('.pr-title')).nativeElement.textContent;
    expect(firstTitle).toContain('Fix navigation bug');
    
    const openIcon = rows[0].query(By.css('.open-icon'));
    expect(openIcon).toBeTruthy();

    const mergedIcon = rows[1].query(By.css('.merged-icon'));
    expect(mergedIcon).toBeTruthy();
    
    const branches = rows[0].query(By.css('.branches')).nativeElement.textContent;
    expect(branches).toContain('bugfix/nav');
    expect(branches).toContain('main');
  }));

  it('should display empty state', fakeAsync(() => {
    prServiceSpy.list.and.returnValue(of([]));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const emptyState = fixture.debugElement.query(By.css('.empty-state'));
    expect(emptyState).toBeTruthy();
    expect(emptyState.nativeElement.textContent).toContain('No pull requests found');
  }));

  it('should handle error', fakeAsync(() => {
    prServiceSpy.list.and.returnValue(throwError(() => new Error('API Fail')));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.prs.length).toBe(0);
  }));
});