import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SnippetCreateComponent } from './snippet-create.component';
import { SnippetService } from '../../../core/services/snippet.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError, timer } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { By } from '@angular/platform-browser';
import { Snippet } from '../../../shared/models/snippet.model';

describe('SnippetCreateComponent', () => {
  let component: SnippetCreateComponent;
  let fixture: ComponentFixture<SnippetCreateComponent>;
  let snippetServiceSpy: jasmine.SpyObj<SnippetService>;
  let router: Router;

  beforeEach(async () => {
    snippetServiceSpy = jasmine.createSpyObj('SnippetService', ['create']);

    await TestBed.configureTestingModule({
      declarations: [ SnippetCreateComponent ],
      imports: [ 
        ReactiveFormsModule, 
        RouterTestingModule 
      ],
      providers: [
        { provide: SnippetService, useValue: snippetServiceSpy }
      ]
    })
    .compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SnippetCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be invalid when empty', () => {
    expect(component.snippetForm.valid).toBeFalsy();
    const btn = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(btn.nativeElement.disabled).toBeTrue();
  });

  it('should validate required fields', () => {
    const filename = component.snippetForm.controls['filename'];
    const content = component.snippetForm.controls['content'];

    filename.setValue('');
    content.setValue('');
    expect(component.snippetForm.valid).toBeFalsy();

    filename.setValue('test.js');
    expect(component.snippetForm.valid).toBeFalsy();

    content.setValue('console.log("test")');
    expect(component.snippetForm.valid).toBeTruthy();
  });

  it('should submit successfully', fakeAsync(() => {
    const mockSnippet: Snippet = {
      id: 1,
      filename: 'main.ts',
      description: 'Main entry',
      content: 'import { enableProdMode } from "@angular/core";',
      language: 'TypeScript',
      authorUsername: 'testuser',
      createdAt: new Date().toISOString()
    };

    snippetServiceSpy.create.and.returnValue(of(mockSnippet).pipe(delay(10)));

    component.snippetForm.setValue({
      filename: 'main.ts',
      description: 'Main entry',
      content: 'import { enableProdMode } from "@angular/core";'
    });

    component.submit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(snippetServiceSpy.create).toHaveBeenCalledWith(jasmine.objectContaining({
      filename: 'main.ts',
      content: 'import { enableProdMode } from "@angular/core";'
    }));
    expect(router.navigate).toHaveBeenCalledWith(['/snippets']);
  }));

  it('should handle submission error', fakeAsync(() => {
    snippetServiceSpy.create.and.returnValue(
      timer(10).pipe(switchMap(() => throwError(() => new Error('Failed'))))
    );
    spyOn(console, 'error');

    component.snippetForm.setValue({
      filename: 'error.ts',
      description: '',
      content: 'error'
    });

    component.submit();
    expect(component.loading).toBeTrue();

    tick(10);

    expect(component.loading).toBeFalse();
    expect(console.error).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  }));

  it('should not submit if form is invalid', () => {
    component.snippetForm.controls['filename'].setValue(''); 
    component.submit();
    expect(snippetServiceSpy.create).not.toHaveBeenCalled();
  });
});