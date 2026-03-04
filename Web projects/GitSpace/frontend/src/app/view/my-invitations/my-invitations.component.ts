import { Component, OnInit } from '@angular/core';
import { CollaboratorService, CollaboratorDto } from '../../core/services/collaborator.service';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Router } from '@angular/router';

@Component({
    selector: 'app-my-invitations',
    templateUrl: './my-invitations.component.html',
    styleUrls: ['./my-invitations.component.scss'],
    providers: [MessageService, ConfirmationService]
})
export class MyInvitationsComponent implements OnInit {
    invitations: CollaboratorDto[] = [];
    loading = false;

    constructor(
        private collaboratorService: CollaboratorService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadInvitations();
    }

    loadInvitations(): void {
        this.loading = true;
        this.collaboratorService.getMyPendingInvitations().subscribe({
            next: (data) => {
                this.invitations = data;
                this.loading = false;
            },
            error: (error) => {
                console.error('Error loading invitations:', error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'Failed to load invitations',
                    life: 5000
                });
                this.loading = false;
            }
        });
    }

    acceptInvitation(invitation: CollaboratorDto): void {
        this.collaboratorService.acceptInvitation(invitation.id).subscribe({
            next: () => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Invitation accepted',
                    detail: `You now have access to ${invitation.repositoryName}`,
                    life: 4000
                });
                this.loadInvitations();
            },
            error: (error) => {
                console.error('Error accepting invitation:', error);
                this.messageService.add({
                    severity: 'error',
                    summary: 'Failed to accept invitation',
                    detail: 'Please try again later',
                    life: 5000
                });
            }
        });
    }

    rejectInvitation(invitation: CollaboratorDto): void {
        this.confirmationService.confirm({
            message: `Are you sure you want to reject the invitation from <strong>${invitation.repositoryName}</strong>?`,
            header: 'Reject Invitation',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Reject',
            rejectLabel: 'Cancel',
            acceptButtonStyleClass: 'p-button-danger',
            accept: () => {
                this.collaboratorService.rejectInvitation(invitation.id).subscribe({
                    next: () => {
                        this.messageService.add({
                            severity: 'info',
                            summary: 'Invitation rejected',
                            detail: `You declined the invitation to ${invitation.repositoryName}`,
                            life: 4000
                        });
                        this.loadInvitations();
                    },
                    error: (error) => {
                        console.error('Error rejecting invitation:', error);
                        this.messageService.add({
                            severity: 'error',
                            summary: 'Failed to reject invitation',
                            detail: 'Please try again later',
                            life: 5000
                        });
                    }
                });
            }
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
