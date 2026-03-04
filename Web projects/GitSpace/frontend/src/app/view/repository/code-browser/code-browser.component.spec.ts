import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CodeBrowserComponent } from './code-browser.component';
import { GitService } from '../../../core/services/git-data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { GitTreeEntry } from '../../../shared/models/git.model';
import { By } from '@angular/platform-browser';
import { delay } from 'rxjs/operators';

const mockEntries: GitTreeEntry[] = [
  { name: 'file.txt', type: 'blob', size: 2048, path: 'file.txt', sha: 'abc', mode: '100644' },
  { name: 'src', type: 'tree', size: 0, path: 'src', sha: 'def', mode: '040000' },
  { name: '.gitignore', type: 'blob', size: 100, path: '.gitignore', sha: 'ghi', mode: '100644' }
];

describe('CodeBrowserComponent', () => {
  let component: CodeBrowserComponent;
  let fixture: ComponentFixture<CodeBrowserComponent>;
  let gitServiceSpy: jasmine.SpyObj<GitService>;
  let routerSpy: jasmine.SpyObj<Router>;
  
  let parentParamsSubject: BehaviorSubject<any>;
  let childParamsSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    gitServiceSpy = jasmine.createSpyObj('GitService', ['getTree']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    
    parentParamsSubject = new BehaviorSubject({ username: 'testuser', repoName: 'testrepo' });
    childParamsSubject = new BehaviorSubject({});

    await TestBed.configureTestingModule({
      declarations: [ CodeBrowserComponent ],
      providers: [
        { provide: GitService, useValue: gitServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { params: parentParamsSubject.asObservable() },
            params: childParamsSubject.asObservable()
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CodeBrowserComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize and load tree data', fakeAsync(() => {
    gitServiceSpy.getTree.and.returnValue(of(mockEntries).pipe(delay(1)));
    
    fixture.detectChanges(); 
    
    expect(component.username).toBe('testuser');
    expect(component.repoName).toBe('testrepo');
    expect(component.branch).toBe('master'); 
    expect(component.currentPath).toBe('');
    expect(component.loading).toBeTrue();

    tick(1);
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.files.length).toBe(3);
    expect(gitServiceSpy.getTree).toHaveBeenCalledWith('testuser', 'testrepo', 'master', '');
  }));

  it('should sort folders before files', fakeAsync(() => {
    gitServiceSpy.getTree.and.returnValue(of(mockEntries));
    fixture.detectChanges();
    tick();

    expect(component.files[0].name).toBe('src');
    expect(component.files[1].name).toBe('.gitignore');
    expect(component.files[2].name).toBe('file.txt');
  }));

  it('should handle subfolder navigation via params', fakeAsync(() => {
    childParamsSubject.next({ branch: 'dev', path: 'src/app' });
    gitServiceSpy.getTree.and.returnValue(of([]));

    fixture.detectChanges();
    tick();

    expect(component.branch).toBe('dev');
    expect(component.currentPath).toBe('src/app');
    expect(gitServiceSpy.getTree).toHaveBeenCalledWith('testuser', 'testrepo', 'dev', 'src/app');
  }));

  it('should handle error state', fakeAsync(() => {
    gitServiceSpy.getTree.and.returnValue(throwError(() => new Error('API Error')));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.error).toContain('Failed to load');
    
    const errorMsg = fixture.debugElement.query(By.css('.error-msg'));
    expect(errorMsg).toBeTruthy();
  }));

  it('should format file sizes correctly', () => {
    expect(component.formatSize(0)).toBe('');
    expect(component.formatSize(500)).toBe('500 B');
    expect(component.formatSize(1024)).toBe('1 KB');
    expect(component.formatSize(2048)).toBe('2 KB');
    expect(component.formatSize(1048576)).toBe('1 MB');
  });

  it('should navigate deeper into tree (folder)', () => {
    component.currentPath = 'src';
    component.branch = 'main';
    const folderEntry: GitTreeEntry = { name: 'components', type: 'tree', path: '', size: 0, sha: '', mode: '040000' };

    component.navigate(folderEntry);

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['tree', 'main', 'src/components'], 
      jasmine.objectContaining({ relativeTo: component.route.parent })
    );
  });

  it('should navigate to file view (blob)', () => {
    component.currentPath = '';
    component.branch = 'master';
    const fileEntry: GitTreeEntry = { name: 'readme.md', type: 'blob', path: '', size: 0, sha: '', mode: '100644' };

    component.navigate(fileEntry);

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['blob', 'master', 'readme.md'],
      jasmine.objectContaining({ relativeTo: component.route.parent })
    );
  });

  it('should navigate up (goUp)', () => {
    component.currentPath = 'src/app/core';
    component.branch = 'dev';
    
    component.goUp();
    
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['tree', 'dev', 'src/app'],
      jasmine.objectContaining({ relativeTo: component.route.parent })
    );
  });

  it('should navigate to root when going up from top-level folder', () => {
    component.currentPath = 'src';
    component.goUp();
    
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['./'],
      jasmine.objectContaining({ relativeTo: component.route.parent })
    );
  });
});