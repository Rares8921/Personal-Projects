import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { PrDetailComponent } from './pr-detail.component';
import { PullRequestService } from '../../../../core/services/pr.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { PullRequest, PullRequestFile } from '../../../../shared/models/pr.model';

const mockPr: PullRequest = {
  id: 123,
  prNumber: 10,
  title: 'Test Pull Request',
  description: 'This is a test description',
  state: 'OPEN',
  authorUsername: 'testauthor',
  fromBranch: 'feature-branch',
  toBranch: 'main',
  createdAt: new Date().toISOString()
};

const mockFiles: PullRequestFile[] = [
  {
    filePath: 'src/app/app.component.ts',
    status: 'modified',
    diff: '@@ -1,2 +1,2 @@\n-const a = 1;\n+const a = 2;'
  },
  {
    filePath: 'src/assets/logo.png',
    status: 'added',
    diff: 'Binary file added'
  }
];

describe('PrDetailComponent', () => {
  let component: PrDetailComponent;
  let fixture: ComponentFixture<PrDetailComponent>;
  let prServiceSpy: jasmine.SpyObj<PullRequestService>;

  beforeEach(async () => {
    prServiceSpy = jasmine.createSpyObj('PullRequestService', ['get', 'getFiles', 'merge']);

    await TestBed.configureTestingModule({
      declarations: [ PrDetailComponent ],
      providers: [
        { provide: PullRequestService, useValue: prServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            parent: {
              parent: {
                params: of({ username: 'owner', repoName: 'repo' })
              }
            },
            params: of({ id: '10' })
          }
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load PR details and files on init', fakeAsync(() => {
    prServiceSpy.get.and.returnValue(of(mockPr));
    prServiceSpy.getFiles.and.returnValue(of(mockFiles));

    fixture.detectChanges(); 
    tick(); 
    fixture.detectChanges(); 

    expect(component.username).toBe('owner');
    expect(component.repoName).toBe('repo');
    expect(component.prNumber).toBe(10);
    
    expect(prServiceSpy.get).toHaveBeenCalledWith('owner', 'repo', 10);
    expect(prServiceSpy.getFiles).toHaveBeenCalledWith('owner', 'repo', 10);
    
    expect(component.pr).toEqual(mockPr);
    expect(component.files).toEqual(mockFiles);
    expect(component.loading).toBeFalse();

    const title = fixture.debugElement.query(By.css('h1')).nativeElement.textContent;
    expect(title).toContain('Test Pull Request');
    expect(title).toContain('#10');

    const desc = fixture.debugElement.query(By.css('.comment-box .body')).nativeElement.textContent;
    expect(desc).toContain('This is a test description');
  }));

  it('should switch tabs', () => {
    component.pr = mockPr;
    component.files = mockFiles;
    component.loading = false;
    fixture.detectChanges();

    component.activeTab = 'conversation';
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.tab-content.conversation'))).toBeTruthy();
    expect(fixture.debugElement.query(By.css('.tab-content.files'))).toBeFalsy();

    const tabs = fixture.debugElement.queryAll(By.css('.tab-btn'));
    tabs[1].triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(component.activeTab).toBe('files');
    expect(fixture.debugElement.query(By.css('.tab-content.conversation'))).toBeFalsy();
    expect(fixture.debugElement.query(By.css('.tab-content.files'))).toBeTruthy();
  });

  it('should merge PR successfully', fakeAsync(() => {
    prServiceSpy.get.and.returnValue(of(mockPr));
    prServiceSpy.getFiles.and.returnValue(of(mockFiles));
    prServiceSpy.merge.and.returnValue(of(void 0)); 

    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const mergeBtn = fixture.debugElement.query(By.css('.btn-tech-success'));
    expect(mergeBtn).toBeTruthy();

    mergeBtn.triggerEventHandler('click', null);
    expect(component.merging).toBeTrue();

    tick();
    fixture.detectChanges();

    expect(prServiceSpy.merge).toHaveBeenCalledWith('owner', 'repo', 10);
    expect(component.pr?.state).toBe('MERGED');
    expect(component.merging).toBeFalse();
    
    const mergedMsg = fixture.debugElement.query(By.css('.merged-msg'));
    expect(mergedMsg).toBeTruthy();
  }));

  it('should display file diffs correctly', fakeAsync(() => {
    prServiceSpy.get.and.returnValue(of(mockPr));
    prServiceSpy.getFiles.and.returnValue(of(mockFiles));
    
    fixture.detectChanges();
    tick();
    
    component.activeTab = 'files';
    fixture.detectChanges();

    const diffCards = fixture.debugElement.queryAll(By.css('.diff-card'));
    expect(diffCards.length).toBe(2);
    
    const firstFilename = diffCards[0].query(By.css('.filename')).nativeElement.textContent;
    expect(firstFilename).toContain('src/app/app.component.ts');
  }));
});