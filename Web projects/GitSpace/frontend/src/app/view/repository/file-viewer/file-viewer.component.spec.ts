import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { FileViewerComponent } from './file-viewer.component';
import { GitService } from '../../../core/services/git-data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';
import { By } from '@angular/platform-browser';

describe('FileViewerComponent', () => {
  let component: FileViewerComponent;
  let fixture: ComponentFixture<FileViewerComponent>;
  let gitServiceSpy: jasmine.SpyObj<GitService>;
  let routerSpy: jasmine.SpyObj<Router>;
  
  let parentParamsSubject: BehaviorSubject<any>;
  let paramsSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    gitServiceSpy = jasmine.createSpyObj('GitService', ['getBlob']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    
    parentParamsSubject = new BehaviorSubject({ username: 'testuser', repoName: 'testrepo' });
    paramsSubject = new BehaviorSubject({ branch: 'master', path: 'src/main.ts' });

    await TestBed.configureTestingModule({
      declarations: [ FileViewerComponent ],
      providers: [
        { provide: GitService, useValue: gitServiceSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: { params: parentParamsSubject.asObservable() },
            params: paramsSubject.asObservable()
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileViewerComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load file content on init', fakeAsync(() => {
    const mockContent = 'console.log("Hello World");';
    gitServiceSpy.getBlob.and.returnValue(of(mockContent).pipe(delay(1)));

    fixture.detectChanges(); 
    
    expect(component.loading).toBeTrue();
    
    tick(1); 
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.content).toBe(mockContent);
    expect(component.isImage).toBeFalse();
    
    expect(gitServiceSpy.getBlob).toHaveBeenCalledWith('testuser', 'testrepo', 'master', 'src/main.ts');
    
    const codeEl = fixture.debugElement.query(By.css('code'));
    expect(codeEl.nativeElement.textContent).toContain('console.log');
  }));

  it('should calculate line numbers correctly', fakeAsync(() => {
    const multiLineContent = 'Line 1\nLine 2\nLine 3';
    gitServiceSpy.getBlob.and.returnValue(of(multiLineContent));

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.lineNumbers.length).toBe(3);
    expect(component.lineNumbers).toEqual([1, 2, 3]);

    const lineNumEls = fixture.debugElement.queryAll(By.css('.line-numbers div'));
    expect(lineNumEls.length).toBe(3);
  }));

  it('should identify and handle image files', fakeAsync(() => {
    paramsSubject.next({ branch: 'master', path: 'assets/logo.png' });
    fixture.detectChanges(); 
    
    expect(component.isImage).toBeTrue();
    expect(gitServiceSpy.getBlob).not.toHaveBeenCalled(); 
    
    const imageBox = fixture.debugElement.query(By.css('.image-box'));
    expect(imageBox).toBeTruthy();
  }));

  it('should handle error when loading fails', fakeAsync(() => {
    gitServiceSpy.getBlob.and.returnValue(throwError(() => new Error('API Error')));
    
    spyOn(console, 'error');

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.error).toContain('Unable to load file content');
    
    const errorMsg = fixture.debugElement.query(By.css('.error-msg'));
    expect(errorMsg).toBeTruthy();
  }));

  it('should show loading state', fakeAsync(() => {
    gitServiceSpy.getBlob.and.returnValue(of('content').pipe(delay(10)));
    
    fixture.detectChanges();
    
    expect(component.loading).toBeTrue();
    const loadingEl = fixture.debugElement.query(By.css('.loading-area'));
    expect(loadingEl).toBeTruthy();

    tick(10);
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
  }));

  it('should navigate up to parent folder', () => {
    component.branch = 'master';
    component.filePath = 'src/main.ts';
    
    component.goUp();
    
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['../../tree', 'master', 'src'], 
      jasmine.objectContaining({ relativeTo: component.route })
    );
  });

  it('should navigate up to root if in top-level file', () => {
    component.branch = 'master';
    component.filePath = 'readme.md';

    component.goUp();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['../../'],
      jasmine.objectContaining({ relativeTo: component.route })
    );
  });
});