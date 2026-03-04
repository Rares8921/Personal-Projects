import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {
  forgotForm!: FormGroup;
  loading = false;
  message = '';
  error = '';

  constructor(private fb: FormBuilder, private authService: AuthService) { }

  ngOnInit(): void {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.forgotForm.invalid) return;

    this.loading = true;
    this.error = '';
    this.message = '';

    const email = this.forgotForm.get('email')?.value;

    this.authService.forgotPassword(email).subscribe({
      next: (res: any) => {
        this.message = res.message || 'Recovery sequence initiated. Check your inbox.';
        this.loading = false;
        this.forgotForm.reset();
      },
      error: err => {
        this.error = err.error?.error || 'System error. Try again later.';
        this.loading = false;
      }
    });
  }
}