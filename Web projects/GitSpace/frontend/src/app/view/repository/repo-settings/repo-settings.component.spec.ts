import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RepoSettingsComponent } from './repo-settings.component';
import { RepositoryService } from '../../../core/services/repo.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError, BehaviorSubject, timer } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { Repository } from '../../../shared/models/repo.model';
import { By } from '@angular/platform-browser';

describe('RepoSettingsComponent', () => {
  let component: RepoSettingsComponent;
  let fixture: ComponentFixture<RepoSettingsComponent>;
  let repoServiceSpy: jasmine.SpyObj<RepositoryService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let parentParamsSubject: BehaviorSubject<any>;

  const mockRepo: Repository = {
    id: 1,
    name: 'test-repo',
    description: 'Original Description',
    isPublic: false,
    ownerUsername: 'testuser',
    defaultBranch: 'main',
    starsCount: 5,
    forksCount: 1,
    updatedAt: new Date().toISOString()
  };

  beforeEach(async () => {
    repoServiceSpy = jasmine.createSpyObj('RepositoryService', ['getRepo', 'updateRepo', 'deleteRepo']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    
    parentParamsSubject = new BehaviorSubject({ username: 'testuser', repoName: 'test-repo' });

    await TestBed.configureTestingModule({
      declarations: [ RepoSettingsComponent ],
      imports: [ ReactiveFormsModule ],
      providers: [
        { provide: RepositoryService, useValue: repoServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { params: parentParamsSubject.asObservable() }
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoSettingsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load repository data on init', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));

    fixture.detectChanges(); 
    tick();

    expect(component.username).toBe('testuser');
    expect(component.repoName).toBe('test-repo');
    expect(repoServiceSpy.getRepo).toHaveBeenCalledWith('testuser', 'test-repo');
    
    expect(component.settingsForm.get('description')?.value).toBe('Original Description');
    expect(component.settingsForm.get('isPublic')?.value).toBeFalse();
  }));

  it('should update repository settings successfully', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));
    fixture.detectChanges();
    tick();

    const updatedRepo = { ...mockRepo, description: 'New Desc', isPublic: true };
    repoServiceSpy.updateRepo.and.returnValue(of(updatedRepo).pipe(delay(10)));
    spyOn(window, 'alert');

    component.settingsForm.patchValue({
      description: 'New Desc',
      isPublic: true
    });

    component.updateRepo();
    
    expect(component.loading).toBeTrue();

    tick(10);

    expect(repoServiceSpy.updateRepo).toHaveBeenCalledWith('testuser', 'test-repo', {
      description: 'New Desc',
      isPublic: true
    });
    expect(component.loading).toBeFalse();
    expect(window.alert).toHaveBeenCalledWith('Repository updated successfully.');
    expect(component.repo).toEqual(updatedRepo);
  }));

  it('should handle update error', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));
    fixture.detectChanges();
    tick();

    repoServiceSpy.updateRepo.and.returnValue(
      timer(10).pipe(switchMap(() => throwError(() => new Error('undefined'))))
    );
    spyOn(window, 'alert');

    component.updateRepo();
    expect(component.loading).toBeTrue();
    
    tick(10);

    expect(component.loading).toBeFalse();
    expect(window.alert).toHaveBeenCalledWith('Update failed: undefined');
  }));

  it('should delete repository when confirmed', fakeAsync(() => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));
    fixture.detectChanges();
    tick();

    spyOn(window, 'prompt').and.returnValue('test-repo');
    repoServiceSpy.deleteRepo.and.returnValue(of(void 0));

    component.deleteRepo();

    expect(window.prompt).toHaveBeenCalled();
    expect(repoServiceSpy.deleteRepo).toHaveBeenCalledWith('testuser', 'test-repo');
    
    tick();
    
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
  }));

  it('should not delete repository if confirmation mismatches', () => {
    repoServiceSpy.getRepo.and.returnValue(of(mockRepo));
    fixture.detectChanges();

    spyOn(window, 'prompt').and.returnValue('wrong-name');
    spyOn(window, 'alert');

    component.deleteRepo();

    expect(repoServiceSpy.deleteRepo).not.toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Repo name does not match. Deletion cancelled.');
  });
});