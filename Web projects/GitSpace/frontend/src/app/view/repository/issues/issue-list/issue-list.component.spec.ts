import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { IssueListComponent } from './issue-list.component';
import { IssueService } from '../../../../core/services/issue.service';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';
import { By } from '@angular/platform-browser';
import { Issue } from '../../../../shared/models/issue.model';

const mockIssues: Issue[] = [
  {
    id: 1,
    issueNumber: 101,
    title: 'Bug in login',
    authorUsername: 'tester',
    isOpen: true,
    createdAt: new Date().toISOString(),
    commentsCount: 2,
    body: 'Fix it'
  },
  {
    id: 2,
    issueNumber: 102,
    title: 'Feature request',
    authorUsername: 'dev',
    isOpen: false,
    createdAt: new Date().toISOString(),
    commentsCount: 0,
    body: 'Add it'
  }
];

describe('IssueListComponent', () => {
  let component: IssueListComponent;
  let fixture: ComponentFixture<IssueListComponent>;
  let issueServiceSpy: jasmine.SpyObj<IssueService>;

  beforeEach(async () => {
    issueServiceSpy = jasmine.createSpyObj('IssueService', ['getIssues']);

    await TestBed.configureTestingModule({
      declarations: [ IssueListComponent ],
      imports: [ RouterTestingModule ],
      providers: [
        { provide: IssueService, useValue: issueServiceSpy },
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
    fixture = TestBed.createComponent(IssueListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    issueServiceSpy.getIssues.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load issues on init', fakeAsync(() => {
    issueServiceSpy.getIssues.and.returnValue(of(mockIssues));
    
    fixture.detectChanges(); 
    tick();
    fixture.detectChanges();

    expect(component.username).toBe('testuser');
    expect(component.repoName).toBe('testrepo');
    expect(issueServiceSpy.getIssues).toHaveBeenCalledWith('testuser', 'testrepo');
    expect(component.issues.length).toBe(2);
    expect(component.loading).toBeFalse();
  }));

  it('should show loading state', fakeAsync(() => {
    issueServiceSpy.getIssues.and.returnValue(of(mockIssues).pipe(delay(10)));
    
    fixture.detectChanges();
    expect(component.loading).toBeTrue();
    
    const loadingEl = fixture.debugElement.query(By.css('.loading-area'));
    expect(loadingEl).toBeTruthy();

    tick(10);
    fixture.detectChanges();
    expect(component.loading).toBeFalse();
  }));

  it('should display issue list in DOM', fakeAsync(() => {
    issueServiceSpy.getIssues.and.returnValue(of(mockIssues));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const rows = fixture.debugElement.queryAll(By.css('.issue-row'));
    expect(rows.length).toBe(2);

    const firstTitle = rows[0].query(By.css('.issue-title')).nativeElement.textContent;
    expect(firstTitle).toContain('Bug in login');
    
    const openIcon = rows[0].query(By.css('.open-icon'));
    expect(openIcon).toBeTruthy();

    const closedIcon = rows[1].query(By.css('.closed-icon'));
    expect(closedIcon).toBeTruthy();
  }));

  it('should display empty state', fakeAsync(() => {
    issueServiceSpy.getIssues.and.returnValue(of([]));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const emptyState = fixture.debugElement.query(By.css('.empty-issues'));
    expect(emptyState).toBeTruthy();
    expect(emptyState.nativeElement.textContent).toContain('Welcome to Issues!');
  }));

  it('should handle error', fakeAsync(() => {
    issueServiceSpy.getIssues.and.returnValue(throwError(() => new Error('API Fail')));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.issues.length).toBe(0);
  }));
});