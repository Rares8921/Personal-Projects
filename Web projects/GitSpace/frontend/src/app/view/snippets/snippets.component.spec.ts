import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SnippetsComponent } from './snippets.component';
import { SnippetService } from '../../core/services/snippet.service';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';
import { By } from '@angular/platform-browser';
import { Snippet } from '../../shared/models/snippet.model';

const mockSnippets: Snippet[] = [
  { 
    id: 1, 
    filename: 'algo.ts', 
    authorUsername: 'dev_master', 
    description: 'Sorting algorithm', 
    content: 'console.log("sort")', 
    language: 'TypeScript', 
    createdAt: new Date().toISOString(), 
    updatedAt: new Date().toISOString() 
  },
  { 
    id: 2, 
    filename: 'style.scss', 
    authorUsername: 'css_wizard', 
    description: 'Mixins', 
    content: '.box { color: red; }', 
    language: 'SCSS', 
    createdAt: new Date().toISOString(), 
    updatedAt: new Date().toISOString() 
  }
];

describe('SnippetsComponent', () => {
  let component: SnippetsComponent;
  let fixture: ComponentFixture<SnippetsComponent>;
  let snippetServiceSpy: jasmine.SpyObj<SnippetService>;

  beforeEach(async () => {
    snippetServiceSpy = jasmine.createSpyObj('SnippetService', ['getPublic']);

    await TestBed.configureTestingModule({
      declarations: [ SnippetsComponent ],
      imports: [ RouterTestingModule ],
      providers: [
        { provide: SnippetService, useValue: snippetServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SnippetsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    snippetServiceSpy.getPublic.and.returnValue(of([]));
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should show loading state initially', fakeAsync(() => {
    snippetServiceSpy.getPublic.and.returnValue(of(mockSnippets).pipe(delay(10)));
    
    fixture.detectChanges();
    expect(component.loading).toBeTrue();
    
    const loader = fixture.debugElement.query(By.css('.loading-area'));
    expect(loader).toBeTruthy();

    tick(10);
    fixture.detectChanges();
    
    expect(component.loading).toBeFalse();
  }));

  it('should load and display snippets', fakeAsync(() => {
    snippetServiceSpy.getPublic.and.returnValue(of(mockSnippets));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.snippets.length).toBe(2);
    expect(component.loading).toBeFalse();

    const cards = fixture.debugElement.queryAll(By.css('.snippet-card'));
    expect(cards.length).toBe(2);
    
    expect(cards[0].nativeElement.textContent).toContain('algo.ts');
    expect(cards[0].nativeElement.textContent).toContain('dev_master');
  }));

  it('should display empty state when no snippets found', fakeAsync(() => {
    snippetServiceSpy.getPublic.and.returnValue(of([]));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.snippets.length).toBe(0);
    
    const emptyState = fixture.debugElement.query(By.css('.empty-state'));
    expect(emptyState).toBeTruthy();
    expect(emptyState.nativeElement.textContent).toContain('No snippets found');
  }));

  it('should handle error when loading snippets', fakeAsync(() => {
    snippetServiceSpy.getPublic.and.returnValue(throwError(() => new Error('Network error')));
    
    fixture.detectChanges();
    tick();
    fixture.detectChanges();

    expect(component.loading).toBeFalse();
    expect(component.snippets.length).toBe(0);
    
    const emptyState = fixture.debugElement.query(By.css('.empty-state'));
    expect(emptyState).toBeTruthy();
  }));
});