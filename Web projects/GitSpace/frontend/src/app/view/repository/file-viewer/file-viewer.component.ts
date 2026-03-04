import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GitService } from '../../../core/services/git-data.service';
import { combineLatest } from 'rxjs';
import { RepositoryService } from 'src/app/core/services/repo.service';

@Component({
  selector: 'app-file-viewer',
  templateUrl: './file-viewer.component.html',
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent implements OnInit {
  username: string = '';
  repoName: string = '';
  branch: string = 'master';
  filePath: string = '';

  isCreateMode = false;
  isEditing = false;
  commitMessage = '';
  isSaving = false;
  newFileName = '';

  content: string = '';
  fileSize: number = 0;
  loading = true;
  error = '';

  isImage = false;
  imageExtensions = ['png', 'jpg', 'jpeg', 'gif', 'svg', 'webp'];

  showDeleteModal = false;
  deleteCommitMessage = '';
  isDeleting = false;

  constructor(
    public route: ActivatedRoute,
    public router: Router,
    private gitService: GitService,
    private repoService: RepositoryService
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.isCreateMode = data['mode'] === 'create';
    });

    combineLatest([
      this.route.parent!.params,
      this.route.params
    ]).subscribe(([parentParams, params]) => {
      this.username = parentParams['username'];
      this.repoName = parentParams['repoName'];
      
      if (!this.isCreateMode) {
        this.branch = params['branch'] || 'master';
        this.filePath = params['path'] || '';
      }

      if (this.isCreateMode) {
        this.loading = true;
        this.content = '';
        this.newFileName = '';
        this.commitMessage = '';
        this.loadBranches();
        this.route.queryParams.subscribe(q => {
            if (q['path']) this.newFileName = q['path'] + '/';
        });
      } else if (this.username && this.repoName && this.filePath) {
        this.checkFileType();
        this.loadFile();
      }
    });
  }

  loadBranches() {
    this.gitService.getBranches(this.username, this.repoName).subscribe({
      next: (branches) => {
        if (branches && branches.length > 0) {
          this.branch = branches[0];
        } else {
          this.branch = 'master';
        }
        this.loading = false;
      },
      error: () => {
        this.branch = 'master';
        this.loading = false;
      }
    });
  }

  checkFileType() {
    const ext = this.filePath.split('.').pop()?.toLowerCase() || '';
    this.isImage = this.imageExtensions.includes(ext);
  }

  loadFile() {
    this.loading = true;
    this.error = '';

    if (this.isImage) {
      this.loading = false;
      return;
    }

    this.gitService.getBlob(this.username, this.repoName, this.branch, this.filePath)
      .subscribe({
        next: (data) => {
          this.content = data;
          this.fileSize = new Blob([data]).size;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Unable to load file content.';
          console.error(err);
          this.loading = false;
        }
      });
  }

  goUp() {
    const segments = this.filePath.split('/');
    segments.pop();
    const parentPath = segments.join('/');

    if (!parentPath) {
      this.router.navigate(['../../'], { relativeTo: this.route });
    } else {
      this.router.navigate(['../../tree', this.branch, parentPath], { relativeTo: this.route });
    }
  }

  get lineNumbers(): number[] {
    if (!this.content) return [];
    return Array(this.content.split('\n').length).fill(0).map((x, i) => i + 1);
  }

  saveChanges() {
    if (!this.filePath || this.filePath.trim() === '') {
        alert('File path is missing.');
        return;
    }

    if (!this.commitMessage) {
      alert('Please enter a commit message');
      return;
    }

    this.isSaving = true;
    const request = {
      branch: this.branch,
      content: this.content,
      message: this.commitMessage || (this.isCreateMode ? `Create ${this.filePath}` : `Update ${this.filePath}`),
      path: this.filePath
    };

    this.gitService.createOrUpdateFile(this.username, this.repoName, this.filePath, request)
      .subscribe({
        next: () => {
           alert('File saved successfully!');
           this.router.navigate(['../../tree', this.branch], { relativeTo: this.route });
        },
        error: (err) => {
           console.error(err);
           this.error = 'Failed to save file: ' + (err.error?.message || err.message);
           this.isSaving = false;
        }
      });
  }

  createFile() {
    if (!this.newFileName || !this.newFileName.trim()) {
      alert('Please enter a file name');
      return;
    }
    if (!this.commitMessage || !this.commitMessage.trim()) {
      alert('Please enter a commit message');
      return;
    }
    
    this.filePath = this.newFileName;
    this.saveChanges();
  }

  cancelCreate() {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  openDeleteModal() {
    this.showDeleteModal = true;
    this.deleteCommitMessage = `Delete ${this.filePath}`;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.deleteCommitMessage = '';
  }

  confirmDelete() {
    if (!this.deleteCommitMessage.trim()) {
      alert('Commit message is required to delete a file.');
      return;
    }

    this.isDeleting = true;
    const payload = {
      branch: this.branch,
      message: this.deleteCommitMessage
    };

    this.gitService.deleteFile(this.username, this.repoName, this.filePath, payload).subscribe({
      next: () => {
        this.closeDeleteModal();
        this.isDeleting = false;
        alert('File deleted successfully.');
        this.goUp();
      },
      error: (err) => {
        this.error = 'Failed to delete file.';
        console.error(err);
        this.isDeleting = false;
        this.closeDeleteModal();
      }
    });
  }

  deleteFile() {
    this.openDeleteModal();
  }
}