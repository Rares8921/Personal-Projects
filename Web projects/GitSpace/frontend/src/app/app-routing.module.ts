import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { HomeComponent } from './view/home/home.component';
import { DashboardComponent } from './view/dashboard/dashboard.component';
import { RepositoriesComponent } from './view/repositories/repositories.component';
import { SettingsComponent } from './view/settings/settings.component';
import { LoginComponent } from './view/login/login.component';
import { RegisterComponent } from './view/register/register.component';
import { ForgotPasswordComponent } from './view/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './view/reset-password/reset-password.component';
import { SnippetsComponent } from './view/snippets/snippets.component';
import { PrivacyComponent } from './view/privacy/privacy.component';
import { TermsComponent } from './view/terms/terms.component';
import { ContactComponent } from './view/contact/contact.component';
import { RepositoryDetailComponent } from './view/repository-detail/repository-detail.component';
import { CreateRepoComponent } from './view/repository/create-repo/create-repo.component';
import { CodeBrowserComponent } from './view/repository/code-browser/code-browser.component';
import { FileViewerComponent } from './view/repository/file-viewer/file-viewer.component';
import { IssueListComponent } from './view/repository/issues/issue-list/issue-list.component';
import { IssueCreateComponent } from './view/repository/issues/issue-create/issue-create.component';
import { IssueDetailComponent } from './view/repository/issues/issue-detail/issue-detail.component';
import { PrListComponent } from './view/repository/pull-requests/pr-list/pr-list.component';
import { PrCreateComponent } from './view/repository/pull-requests/pr-create/pr-create.component';
import { PrDetailComponent } from './view/repository/pull-requests/pr-detail/pr-detail.component';
import { SnippetCreateComponent } from './view/snippets/snippet-create/snippet-create.component';
import { SnippetDetailComponent } from './view/snippets/snippet-detail/snippet-detail.component';
import { UserProfileComponent } from './view/user-profile/user-profile.component';
import { NotFoundComponent } from './view/not-found/not-found.component';
import { SearchResultsComponent } from './view/search-results/search-results.component';
import { RepoCollaboratorsComponent } from './view/repository/repo-collaborators/repo-collaborators.component';
import { RepoSettingsComponent } from './view/repository/repo-settings/repo-settings.component';
import { MyInvitationsComponent } from './view/my-invitations/my-invitations.component';
import { RepoStatisticsComponent } from './view/repository/repo-statistics/repo-statistics.component';

import { UrlSegment, UrlMatchResult } from '@angular/router';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'snippets',
    component: SnippetsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'snippets/new',
    component: SnippetCreateComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'snippets/:id',
    component: SnippetDetailComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'invitations',
    component: MyInvitationsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'repositories',
    component: RepositoriesComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'new',
    component: CreateRepoComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'user/:username',
    component: UserProfileComponent
  },
  {
    path: 'repo/:username/:repoName',
    component: RepositoryDetailComponent,
    canActivate: [AuthGuard], // repo privat
    children: [
      { path: '', component: CodeBrowserComponent },
      { path: 'tree/:branch', component: CodeBrowserComponent },
      { path: 'tree/:branch/:path', component: CodeBrowserComponent },

      { path: 'new-file', component: FileViewerComponent, data: { mode: 'create' } },

      { matcher: repoFileMatcher, component: FileViewerComponent },

      { path: 'issues', component: IssueListComponent },
      { path: 'issues/new', component: IssueCreateComponent },
      { path: 'issues/:id', component: IssueDetailComponent },

      { path: 'pulls', component: PrListComponent },
      { path: 'pulls/new', component: PrCreateComponent },
      { path: 'pulls/:id', component: PrDetailComponent },

      { path: 'activity', component: RepoStatisticsComponent },

      { path: 'settings', component: RepoSettingsComponent },
      { path: 'settings/collaborators', component: RepoCollaboratorsComponent },
    ]
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'privacy',
    component: PrivacyComponent
  },
  {
    path: 'terms',
    component: TermsComponent
  },
  {
    path: 'contact',
    component: ContactComponent
  },
  { 
    path: 'search', 
    component: SearchResultsComponent 
  },
  { 
    path: '', 
    redirectTo: '/login', 
    pathMatch: 'full' 
  },
  {
    path: '**',
    component: NotFoundComponent
  }
];

export function repoFileMatcher(url: UrlSegment[]): UrlMatchResult | null {
  if (url.length < 3 || url[0].path !== 'blob') {
    return null;
  }
  // url[0] = 'blob'
  // url[1] = branch (ex: 'master')
  // url[2+] = path (ex: 'src', 'main.ts')

  const branch = url[1];
  const filePath = url.slice(2).map(segment => segment.path).join('/');

  return {
    consumed: url,
    posParams: {
      branch: new UrlSegment(branch.path, {}),
      path: new UrlSegment(filePath, {})
    }
  };
}

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
