import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SnippetDetailComponent } from './snippet-detail.component';
import { SnippetService } from '../../../core/services/snippet.service';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';
import { By } from '@angular/platform-browser';
import { Snippet } from '../../../shared/models/snippet.model';

const mockSnippet: Snippet = {
  id: 1,
  filename: 'test.ts',
  description: 'Test snippet',
  content: 'console.log("hello");',
  authorUsername: 'tester',
  language: 'TypeScript',
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString()
};

describe('SnippetDetailComponent', () => {
  let component: SnippetDetailComponent;
  let fixture: ComponentFixture<SnippetDetailComponent>;
  let snippetServiceSpy: jasmine.SpyObj<SnippetService>;

  beforeEach(async () => {
    snippetServiceSpy = jasmine.createSpyObj('SnippetService', ['getById']);

    await TestBed.configureTestingModule({
      declarations: [ SnippetDetailComponent ],
      providers: [
        { provide: SnippetService, useValue: snippetServiceSpy },
        { 
          provide: ActivatedRoute, 
          useValue: { snapshot: { params: { id: 1 } } } 
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SnippetDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    snippetServiceSpy.getById.and.returnValue(of(mockSnippet));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should fetch snippet details on init', fakeAsync(() => {
    snippetServiceSpy.getById.and.returnValue(of(mockSnippet));
    
    fixture.detectChanges(); 
    tick();
    fixture.detectChanges();

    expect(snippetServiceSpy.getById).toHaveBeenCalledWith(1);
    expect(component.snippet).toEqual(mockSnippet);
    expect(component.loading).toBeFalse();
  }));

  it('should display loading state initially', fakeAsync(() => {
    snippetServiceSpy.getById.and.returnValue(of(mockSnippet).pipe(delay(10)));
    
    fixture.detectChanges();
    
    expect(component.loading).toBeTrue();
    const loadingEl = fixture.debugElement.query(By.css('.loading-area'));
    expect(loadingEl).toBeTruthy();
    expect(loadingEl.nativeElement.textContent).toContain('Fetching snippet');

    tick(10);
    fixture.detectChanges();
    expect(component.loading).toBeFalse();
  }));

  it('should render snippet content after loading', fakeAsync(() => {
    snippetServiceSpy.getById.and.returnValue(of(mockSnippet));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.textContent).toContain('test.ts');

    const code = fixture.debugElement.query(By.css('code'));
    expect(code.nativeElement.textContent).toContain('console.log("hello");');
    
    const author = fixture.debugElement.query(By.css('.author-box'));
    expect(author.nativeElement.textContent).toContain('tester');
  }));

  it('should handle error when fetching snippet', fakeAsync(() => {
    snippetServiceSpy.getById.and.returnValue(throwError(() => new Error('Not found')));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.snippet).toBeNull();
    
    const loadingEl = fixture.debugElement.query(By.css('.loading-area'));
    expect(loadingEl).toBeTruthy();
  }));
});