import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CollaboratorService, CollaboratorDto, InviteCollaboratorRequest } from '../../../core/services/collaborator.service';
import { RepositoryService } from '../../../core/services/repo.service';
import { MessageService, ConfirmationService } from 'primeng/api';

@Component({
    selector: 'app-repo-collaborators',
    templateUrl: './repo-collaborators.component.html',
    styleUrls: ['./repo-collaborators.component.scss'],
    providers: [MessageService, ConfirmationService]
})
export class RepoCollaboratorsComponent implements OnInit {
    repositoryId!: number;
    collaborators: CollaboratorDto[] = [];
    pendingInvitations: CollaboratorDto[] = [];
    loading = false;

    displayInviteDialog = false;
    inviteUsername = '';
    selectedRole: 'READ' | 'WRITE' = 'WRITE';
    inviting = false;

    roleOptions = [
        { label: 'Read', value: 'READ', description: 'Can view and clone repository' },
        { label: 'Write', value: 'WRITE', description: 'Can push code and merge pull requests' }
    ];

    constructor(
        private route: ActivatedRoute,
        private collaboratorService: CollaboratorService,
        private repositoryService: RepositoryService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService
    ) { }

    ngOnInit(): void {
        this.route.parent?.params.subscribe(params => {
            const username = params['username'];
            const repoName = params['repoName'];

            if (username && repoName) {
                this.repositoryService.getRepo(username, repoName).subscribe({
                    next: (repo) => {
                        this.repositoryId = repo.id;
                        this.loadCollaborators();
                        this.loadPendingInvitations();
                    },
                    error: (error) => {
                        console.error('Error loading repository:', error);
                        this.showError('Failed to load repository', 'Please refresh the page and try again');
                    }
                });
            }
        });
    }

    loadCollaborators(): void {
        this.loading = true;
        this.collaboratorService.getActiveCollaborators(this.repositoryId).subscribe({
            next: (data) => {
                this.collaborators = data;
                this.loading = false;
            },
            error: (error) => {
                console.error('Error loading collaborators:', error);
                this.showError('Failed to load collaborators', 'Please try again later');
                this.loading = false;
            }
        });
    }

    loadPendingInvitations(): void {
        this.collaboratorService.getPendingInvitations(this.repositoryId).subscribe({
            next: (data) => {
                this.pendingInvitations = data;
            },
            error: (error) => {
                console.error('Error loading pending invitations:', error);
            }
        });
    }

    showInviteDialog(): void {
        this.inviteUsername = '';
        this.selectedRole = 'WRITE';
        this.displayInviteDialog = true;
    }

    inviteCollaborator(): void {
        if (!this.inviteUsername.trim()) {
            this.showWarning('Username required', 'Please enter a username to invite');
            return;
        }

        this.inviting = true;
        const request: InviteCollaboratorRequest = {
            username: this.inviteUsername.trim(),
            role: this.selectedRole
        };

        this.collaboratorService.inviteCollaborator(this.repositoryId, request).subscribe({
            next: (collaborator) => {
                this.showSuccess(
                    'Invitation sent',
                    `${collaborator.username} has been invited to collaborate`
                );
                this.displayInviteDialog = false;
                this.loadPendingInvitations();
                this.inviting = false;
            },
            error: (error) => {
                console.error('Error inviting collaborator:', error);
                this.inviting = false;

                // Handle specific error cases
                const errorMessage = error.error?.message || error.message || 'Unknown error';

                if (errorMessage.includes('Couldn\'t find this user')) {
                    this.showError(
                        'Couldn\'t find this user',
                        'Please check the username and try again'
                    );
                } else if (errorMessage.includes('already has a pending invitation')) {
                    this.showWarning(
                        'Invitation already sent',
                        `${this.inviteUsername} already has a pending invitation to this repository`
                    );
                } else if (errorMessage.includes('already a collaborator')) {
                    this.showWarning(
                        'Already a collaborator',
                        `${this.inviteUsername} is already a collaborator on this repository`
                    );
                } else if (errorMessage.includes('yourself') || errorMessage.includes('self')) {
                    this.showWarning(
                        'Cannot invite yourself',
                        'You are already the owner of this repository'
                    );
                } else if (errorMessage.includes('TODO') || errorMessage.includes('Implement intercept')) {
                    this.showError(
                        'Unable to send invitation',
                        'An unexpected error occurred. Please try again or contact support if the problem persists.'
                    );
                } else {
                    // Generic error with cleaned message
                    const cleanMessage = errorMessage.replace(/TODO.*$/i, '').trim() || 'An unexpected error occurred';
                    this.showError(
                        'Failed to send invitation',
                        cleanMessage
                    );
                }
            }
        });
    }

    removeCollaborator(collaborator: CollaboratorDto): void {
        this.confirmationService.confirm({
            message: `Are you sure you want to remove <strong>${collaborator.username}</strong> from this repository?`,
            header: 'Remove Collaborator',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Remove',
            rejectLabel: 'Cancel',
            acceptButtonStyleClass: 'p-button-danger',
            accept: () => {
                this.collaboratorService.removeCollaborator(this.repositoryId, collaborator.id).subscribe({
                    next: () => {
                        this.showSuccess(
                            'Collaborator removed',
                            `${collaborator.username} no longer has access to this repository`
                        );
                        this.loadCollaborators();
                    },
                    error: (error) => {
                        console.error('Error removing collaborator:', error);
                        this.showError(
                            'Failed to remove collaborator',
                            'Please try again later'
                        );
                    }
                });
            }
        });
    }

    revokeInvitation(invitation: CollaboratorDto): void {
        this.confirmationService.confirm({
            message: `Are you sure you want to revoke the invitation to <strong>${invitation.username}</strong>?`,
            header: 'Revoke Invitation',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Revoke',
            rejectLabel: 'Cancel',
            acceptButtonStyleClass: 'p-button-warning',
            accept: () => {
                this.collaboratorService.removeCollaborator(this.repositoryId, invitation.id).subscribe({
                    next: () => {
                        this.showSuccess(
                            'Invitation revoked',
                            `The invitation to ${invitation.username} has been cancelled`
                        );
                        this.loadPendingInvitations();
                    },
                    error: (error) => {
                        console.error('Error revoking invitation:', error);
                        this.showError(
                            'Failed to revoke invitation',
                            'Please try again later'
                        );
                    }
                });
            }
        });
    }

    private showSuccess(summary: string, detail: string): void {
        this.messageService.add({
            severity: 'success',
            summary,
            detail,
            life: 4000
        });
    }

    private showError(summary: string, detail: string): void {
        this.messageService.add({
            severity: 'error',
            summary,
            detail,
            life: 6000
        });
    }

    private showWarning(summary: string, detail: string): void {
        this.messageService.add({
            severity: 'warn',
            summary,
            detail,
            life: 5000
        });
    }

    getRoleBadgeClass(role: string): string {
        switch (role) {
            case 'WRITE': return 'badge-write';
            case 'READ': return 'badge-read';
            default: return '';
        }
    }
}
