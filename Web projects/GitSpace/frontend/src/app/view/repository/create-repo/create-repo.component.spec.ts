import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreateRepoComponent } from './create-repo.component';
import { RepositoryService } from '../../../core/services/repo.service';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { Repository } from '../../../shared/models/repo.model';
import { User } from '../../../shared/models/user.model';

describe('CreateRepoComponent', () => {
  let component: CreateRepoComponent;
  let fixture: ComponentFixture<CreateRepoComponent>;
  let repoServiceSpy: jasmine.SpyObj<RepositoryService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  const mockUser: User = { 
    id: 1, 
    username: 'testuser', 
    email: 'test@git.com', 
    token: 'jwt-token' 
  };

  beforeEach(async () => {
    repoServiceSpy = jasmine.createSpyObj('RepositoryService', ['createRepo']);
    authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      currentUserValue: mockUser,
      currentUser$: of(mockUser)
    });

    await TestBed.configureTestingModule({
      declarations: [ CreateRepoComponent ],
      imports: [ ReactiveFormsModule, RouterTestingModule ],
      providers: [
        { provide: RepositoryService, useValue: repoServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateRepoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.repoForm.get('isPublic')?.value).toBeTrue();
    expect(component.repoForm.get('name')?.value).toBe('');
    expect(component.repoForm.valid).toBeFalse();
  });

  it('should validate repository name pattern', () => {
    const nameControl = component.repoForm.controls['name'];

    nameControl.setValue('invalid name'); 
    expect(nameControl.hasError('pattern')).toBeTrue();

    nameControl.setValue('invalid@name'); 
    expect(nameControl.hasError('pattern')).toBeTrue();

    nameControl.setValue('valid-name_123');
    expect(nameControl.valid).toBeTrue();
  });

  it('should not submit if form is invalid', () => {
    component.repoForm.patchValue({
      name: 'invalid name',
      description: 'desc',
      isPublic: true
    });

    component.onSubmit();
    expect(repoServiceSpy.createRepo).not.toHaveBeenCalled();
  });

  it('should create repository and navigate on success', fakeAsync(() => {
    const mockRepoResponse: Repository = { 
      id: 1,
      name: 'new-project', 
      description: 'My new project',
      isPublic: true,
      defaultBranch: 'main',
      starsCount: 0,
      forksCount: 0,
      ownerUsername: 'testuser',
      updatedAt: new Date().toISOString()
    };

    repoServiceSpy.createRepo.and.returnValue(timer(10).pipe(switchMap(() => of(mockRepoResponse))));

    component.repoForm.setValue({
      name: 'new-project',
      description: 'My new project',
      isPublic: true
    });

    component.onSubmit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(repoServiceSpy.createRepo).toHaveBeenCalledWith({
      name: 'new-project',
      description: 'My new project',
      isPublic: true
    });

    expect(router.navigate).toHaveBeenCalledWith(['/repo', 'testuser', 'new-project']);
  }));

  it('should handle error during creation', fakeAsync(() => {
    const errorResponse = { error: { message: 'Failed to create repository. Name might be taken.' } };
    
    repoServiceSpy.createRepo.and.returnValue(
      timer(10).pipe(switchMap(() => throwError(() => errorResponse)))
    );

    component.repoForm.setValue({
      name: 'existing-repo',
      description: '',
      isPublic: false
    });

    component.onSubmit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('Failed to create repository. Name might be taken.');
    expect(router.navigate).not.toHaveBeenCalled();
  }));
});