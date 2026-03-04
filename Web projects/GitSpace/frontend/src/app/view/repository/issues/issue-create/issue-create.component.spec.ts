import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { IssueCreateComponent } from './issue-create.component';
import { IssueService } from '../../../../core/services/issue.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError, timer } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { By } from '@angular/platform-browser';
import { Issue } from '../../../../shared/models/issue.model';

describe('IssueCreateComponent', () => {
  let component: IssueCreateComponent;
  let fixture: ComponentFixture<IssueCreateComponent>;
  let issueServiceSpy: jasmine.SpyObj<IssueService>;
  let router: Router;
  let route: ActivatedRoute;

  beforeEach(async () => {
    issueServiceSpy = jasmine.createSpyObj('IssueService', ['createIssue']);

    await TestBed.configureTestingModule({
      declarations: [ IssueCreateComponent ],
      imports: [ ReactiveFormsModule, RouterTestingModule ],
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

    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    spyOn(router, 'navigate');
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IssueCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with route params', () => {
    expect(component.username).toBe('testuser');
    expect(component.repoName).toBe('testrepo');
  });

  it('should be invalid when empty', () => {
    expect(component.issueForm.valid).toBeFalsy();
    const btn = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(btn.nativeElement.disabled).toBeTrue();
  });

  it('should be valid with title', () => {
    component.issueForm.patchValue({ title: 'New Bug' });
    expect(component.issueForm.valid).toBeTruthy();
  });

  it('should submit successfully', fakeAsync(() => {
    const mockIssue: Issue = {
      id: 1,
      issueNumber: 101,
      title: 'Bug Report',
      authorUsername: 'testuser',
      isOpen: true,
      createdAt: new Date().toISOString(),
      commentsCount: 0
    };

    issueServiceSpy.createIssue.and.returnValue(of(mockIssue).pipe(delay(10)));
    
    component.issueForm.setValue({
      title: 'Bug Report',
      body: 'Description of bug'
    });

    component.submit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(component.loading).toBeFalse();
    expect(issueServiceSpy.createIssue).toHaveBeenCalledWith('testuser', 'testrepo', {
      title: 'Bug Report',
      body: 'Description of bug'
    });
    expect(router.navigate).toHaveBeenCalledWith(['../'], jasmine.objectContaining({ relativeTo: route }));
  }));

  it('should handle submission error', fakeAsync(() => {
    issueServiceSpy.createIssue.and.returnValue(
      timer(10).pipe(switchMap(() => throwError(() => new Error('Failed'))))
    );
    spyOn(console, 'error');

    component.issueForm.setValue({
      title: 'Error Issue',
      body: ''
    });

    component.submit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(component.loading).toBeFalse();
    expect(console.error).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  }));

  it('should not submit if invalid', () => {
    component.issueForm.patchValue({ title: '' });
    component.submit();
    expect(issueServiceSpy.createIssue).not.toHaveBeenCalled();
  });
});