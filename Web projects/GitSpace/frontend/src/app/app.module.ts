import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RouterModule } from '@angular/router';
import { MonacoEditorModule } from 'ngx-monaco-editor';
import { MarkdownModule } from 'ngx-markdown';
// primeNG modules
import { FileUploadModule } from 'primeng/fileupload';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { TabViewModule } from 'primeng/tabview';
import { PasswordModule } from 'primeng/password';
import { InputSwitchModule } from 'primeng/inputswitch';
import { DialogModule } from 'primeng/dialog';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

import { CoreModule } from './core/core.module';
import { FooterComponent } from './layout/footer/footer.component';
import { NavbarComponent } from './layout/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { DashboardComponent } from './view/dashboard/dashboard.component';
import { HomeComponent } from './view/home/home.component';
import { RepositoriesComponent } from './view/repositories/repositories.component';
import { SettingsComponent } from './view/settings/settings.component';
import { LoginComponent } from './view/login/login.component';
import { ProfileComponent } from './shared/components/profile/profile.component';
import { IssueComponent } from './shared/components/issue/issue.component';
import { SnippetsComponent } from './view/snippets/snippets.component';
import { TermsComponent } from './view/terms/terms.component';
import { ContactComponent } from './view/contact/contact.component';
import { PrivacyComponent } from './view/privacy/privacy.component';
import { RepositoryDetailComponent } from './view/repository-detail/repository-detail.component';
import { RegisterComponent } from './view/register/register.component';
import { ForgotPasswordComponent } from './view/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './view/reset-password/reset-password.component';
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
import { RepoSettingsComponent } from './view/repository/repo-settings/repo-settings.component';
import { NotFoundComponent } from './view/not-found/not-found.component';
import { SearchResultsComponent } from './view/search-results/search-results.component';
import { RepoCollaboratorsComponent } from './view/repository/repo-collaborators/repo-collaborators.component';
import { MyInvitationsComponent } from './view/my-invitations/my-invitations.component';
import { RepoStatisticsComponent } from './view/repository/repo-statistics/repo-statistics.component';


@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    NavbarComponent,
    DashboardComponent,
    HomeComponent,
    RepositoriesComponent,
    SettingsComponent,
    LoginComponent,
    ProfileComponent,
    IssueComponent,
    SnippetsComponent,
    TermsComponent,
    ContactComponent,
    PrivacyComponent,
    RepositoryDetailComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    CreateRepoComponent,
    CodeBrowserComponent,
    FileViewerComponent,
    IssueListComponent,
    IssueCreateComponent,
    IssueDetailComponent,
    PrListComponent,
    PrCreateComponent,
    PrDetailComponent,
    SnippetCreateComponent,
    SnippetDetailComponent,
    UserProfileComponent,
    RepoSettingsComponent,
    NotFoundComponent,
    SearchResultsComponent,
    RepoCollaboratorsComponent,
    MyInvitationsComponent,
    RepoStatisticsComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MarkdownModule.forRoot(),
    AppRoutingModule,
    CoreModule,
    CommonModule,
    FileUploadModule,
    FormsModule,
    ReactiveFormsModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    CardModule,
    ToggleButtonModule,
    TabViewModule,
    PasswordModule,
    InputSwitchModule,
    DialogModule,
    TooltipModule,
    ConfirmDialogModule,
    MonacoEditorModule.forRoot(),
    RouterModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }