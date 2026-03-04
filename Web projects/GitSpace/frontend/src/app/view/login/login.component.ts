import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'] 
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  error = '';
  
  hidePassword = true;
  
  // Rate limiting
  failedAttempts = 0;
  showCaptcha = false;
  num1: number = 0;
  num2: number = 0;
  captchaResult: number = 0;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    if (this.authService.currentUserValue) { 
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      captcha: [''] // Optional initial
    });
  }

  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  generateCaptcha() {
    this.num1 = Math.floor(Math.random() * 10);
    this.num2 = Math.floor(Math.random() * 10);
    this.captchaResult = this.num1 + this.num2;
    this.loginForm.get('captcha')?.setValue('');
  }

  onSubmit() {
    if (this.loginForm.invalid) return;
    if (this.showCaptcha) {
      const ans = parseInt(this.loginForm.get('captcha')?.value, 10);
      if (ans !== this.captchaResult) {
        this.error = "Verification failed. Check math.";
        this.generateCaptcha();
        return;
      }
    }

    this.loading = true;
    this.error = '';

    const { captcha, ...creds } = this.loginForm.value;

    this.authService.login(creds).subscribe({
      next: () => {
        this.failedAttempts = 0; // Reset
        this.router.navigate(['/home']);
      },
      error: err => {
        this.error = "Authentication failed. Invalid credentials.";
        this.loading = false;
        this.failedAttempts++;

        // Daca a gresit de 3 ori, activam captcha
        if (this.failedAttempts >= 3) {
          this.showCaptcha = true;
          this.generateCaptcha();
          this.loginForm.get('captcha')?.setValidators(Validators.required);
          this.loginForm.get('captcha')?.updateValueAndValidity();
        }
      }   
    });
  }
}