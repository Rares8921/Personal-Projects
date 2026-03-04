import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  resetForm!: FormGroup;
  token = '';
  loading = false;
  message = '';
  error = '';
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'];

    const strongPasswordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;

    this.resetForm = this.fb.group({
      password: ['', [Validators.required, Validators.pattern(strongPasswordRegex)]],
      confirmPassword: ['', Validators.required]
    }, { validator: this.passwordMatchValidator });

    if (!this.token) {
      this.error = 'Security Token Missing. Link invalid.';
      this.resetForm.disable();
    }
  }

  get f() { return this.resetForm.controls; }

  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  passwordMatchValidator(frm: FormGroup) {
    return frm.get('password')?.value === frm.get('confirmPassword')?.value
      ? null : { mismatch: true };
  }

  onSubmit() {
    if (this.resetForm.invalid || !this.token) return;

    this.loading = true;
    this.error = '';
    
    const newPassword = this.resetForm.get('password')?.value;

    this.authService.resetPassword(this.token, newPassword).subscribe({
      next: () => {
        this.message = 'Key updated successfully. Redirecting...';
        this.resetForm.disable();
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: err => {
        this.error = err.error?.error || 'Update failed. Token may be expired.';
        this.loading = false;
      }
    });
  }
}