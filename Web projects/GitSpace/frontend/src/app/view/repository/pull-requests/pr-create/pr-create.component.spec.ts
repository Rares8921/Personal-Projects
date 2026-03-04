import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { PrCreateComponent } from './pr-create.component';
import { PullRequestService } from '../../../../core/services/pr.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError, timer } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { PullRequest } from '../../../../shared/models/pr.model';
import { By } from '@angular/platform-browser';

const mockPr: PullRequest = {
  id: 1,
  prNumber: 101,
  title: 'New Feature',
  description: 'Added cool things',
  fromBranch: 'feature/login',
  toBranch: 'master',
  authorUsername: 'testuser',
  state: 'OPEN',
  createdAt: new Date().toISOString()
};

describe('PrCreateComponent', () => {
  let component: PrCreateComponent;
  let fixture: ComponentFixture<PrCreateComponent>;
  let prServiceSpy: jasmine.SpyObj<PullRequestService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    prServiceSpy = jasmine.createSpyObj('PullRequestService', ['create']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      declarations: [ PrCreateComponent ],
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: PullRequestService, useValue: prServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              parent: {
                params: of({ username: 'testuser', repoName: 'testrepo' })
              }
            },
            relativeTo: {}
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with route params and default form values', () => {
    expect(component.username).toBe('testuser');
    expect(component.repoName).toBe('testrepo');
    expect(component.prForm.get('toBranch')?.value).toBe('master');
    expect(component.prForm.valid).toBeFalse(); 
  });

  it('should validate required fields', () => {
    const form = component.prForm;
    expect(form.valid).toBeFalse();

    form.patchValue({
      title: 'Fix bug',
      fromBranch: 'bugfix/123',
      toBranch: 'main'
    });

    expect(form.valid).toBeTrue();
  });

  it('should create PR and navigate on success', fakeAsync(() => {
    prServiceSpy.create.and.returnValue(of(mockPr).pipe(delay(10)));
    
    component.prForm.setValue({
      title: 'New Feature',
      description: 'Added cool things',
      fromBranch: 'feature/login',
      toBranch: 'master'
    });

    component.submit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(prServiceSpy.create).toHaveBeenCalledWith('testuser', 'testrepo', {
      title: 'New Feature',
      description: 'Added cool things',
      fromBranch: 'feature/login',
      toBranch: 'master'
    });
    
     expect(routerSpy.navigate).toHaveBeenCalledWith(['../', 101], jasmine.any(Object));
  }));

  it('should handle creation error', fakeAsync(() => {
    prServiceSpy.create.and.returnValue(
      timer(10).pipe(switchMap(() => throwError(() => new Error('Branch conflict'))))
    );
    
    component.prForm.setValue({
      title: 'Fail PR',
      description: '',
      fromBranch: 'bad-branch',
      toBranch: 'master'
    });

    component.submit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(component.loading).toBeFalse();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  }));

  it('should not submit if form is invalid', () => {
    component.prForm.patchValue({ title: '' }); 
    component.submit();
    expect(prServiceSpy.create).not.toHaveBeenCalled();
  });
});