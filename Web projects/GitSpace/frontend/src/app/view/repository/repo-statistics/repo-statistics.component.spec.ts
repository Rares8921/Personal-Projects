import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepoStatisticsComponent } from './repo-statistics.component';

describe('RepoStatisticsComponent', () => {
  let component: RepoStatisticsComponent;
  let fixture: ComponentFixture<RepoStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RepoStatisticsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
