import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { IssueDetailComponent } from './issue-detail.component';
import { IssueService } from '../../../../core/services/issue.service';
import { ActivatedRoute } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { Issue, IssueComment } from '../../../../shared/models/issue.model';

const mockIssue: Issue = {
  id: 1,
  issueNumber: 1,
  title: 'Test Issue',
  body: 'Issue Body',
  authorUsername: 'tester',
  createdAt: new Date().toISOString(),
  isOpen: true,
  commentsCount: 1
};

const mockComments: IssueComment[] = [
  {
    id: 1,
    body: 'Comment 1',
    authorUsername: 'dev',
    createdAt: new Date().toISOString(),
    authorAvatarUrl: 'assets/dev.png'
  }
];

describe('IssueDetailComponent', () => {
  let component: IssueDetailComponent;
  let fixture: ComponentFixture<IssueDetailComponent>;
  let issueServiceSpy: jasmine.SpyObj<IssueService>;

  beforeEach(async () => {
    issueServiceSpy = jasmine.createSpyObj('IssueService', ['getIssue', 'getComments', 'addComment', 'changeStatus']);

    await TestBed.configureTestingModule({
      declarations: [ IssueDetailComponent ],
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: IssueService, useValue: issueServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              parent: {
                params: of({ username: 'testuser', repoName: 'testrepo' })
              }
            },
            params: of({ id: '1' })
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IssueDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load issue and comments on init', fakeAsync(() => {
    issueServiceSpy.getIssue.and.returnValue(of(mockIssue));
    issueServiceSpy.getComments.and.returnValue(of(mockComments));

    fixture.detectChanges(); 
    tick();
    fixture.detectChanges();

    expect(component.username).toBe('testuser');
    expect(component.repoName).toBe('testrepo');
    expect(component.issueNumber).toBe(1);
    
    expect(issueServiceSpy.getIssue).toHaveBeenCalledWith('testuser', 'testrepo', 1);
    expect(issueServiceSpy.getComments).toHaveBeenCalledWith('testuser', 'testrepo', 1);
    
    expect(component.issue).toEqual(mockIssue);
    expect(component.comments.length).toBe(1);
    expect(component.loading).toBeFalse();

    const title = fixture.debugElement.query(By.css('h1')).nativeElement.textContent;
    expect(title).toContain('Test Issue');
  }));

  it('should post a comment', fakeAsync(() => {
    issueServiceSpy.getIssue.and.returnValue(of(mockIssue));
    issueServiceSpy.getComments.and.returnValue(of([]));
    fixture.detectChanges();
    tick();

    const newComment: IssueComment = {
      id: 2,
      body: 'New Comment',
      authorUsername: 'testuser',
      createdAt: new Date().toISOString()
    };

    issueServiceSpy.addComment.and.returnValue(of(newComment));

    component.commentForm.controls['body'].setValue('New Comment');
    component.postComment();
    
    expect(issueServiceSpy.addComment).toHaveBeenCalledWith('testuser', 'testrepo', 1, 'New Comment');
    expect(component.comments.length).toBe(1);
    expect(component.comments[0]).toEqual(newComment);
    expect(component.commentForm.value.body).toBeNull();
  }));

  it('should toggle issue status', fakeAsync(() => {
    issueServiceSpy.getIssue.and.returnValue(of(mockIssue));
    issueServiceSpy.getComments.and.returnValue(of([]));
    fixture.detectChanges();
    tick();

    issueServiceSpy.changeStatus.and.returnValue(of(void 0));

    component.toggleStatus();
    
    expect(issueServiceSpy.changeStatus).toHaveBeenCalledWith('testuser', 'testrepo', 1, false);
    expect(component.issue?.isOpen).toBeFalse();
  }));

  it('should not post comment if form is invalid', () => {
    component.commentForm.controls['body'].setValue('');
    component.postComment();
    expect(issueServiceSpy.addComment).not.toHaveBeenCalled();
  });
});