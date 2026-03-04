import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest } from '../../shared/models/auth.models';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  error = '';
  successMessage = '';
  
  hidePassword = true;
  
  num1: number = 0;
  num2: number = 0;
  captchaResult: number = 0;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) { 
    this.generateCaptcha();
  }

  ngOnInit() {
    // Minim 8 caractere, cel putin o cifra, cel putin un caracter special (!@#...)
    const strongPasswordRegex = /^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/;

    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.pattern(strongPasswordRegex)]],
      captcha: ['', [Validators.required]] 
    });
  }

  get f() { return this.registerForm.controls; }

  generateCaptcha() {
    this.num1 = Math.floor(Math.random() * 10); // 0-9
    this.num2 = Math.floor(Math.random() * 10);
    this.captchaResult = this.num1 + this.num2;
  }

  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const userCaptchaAnswer = parseInt(this.registerForm.get('captcha')?.value, 10);
    if (userCaptchaAnswer !== this.captchaResult) {
      this.error = "Incorrect captcha answer. Are you a robot or slow?";
      this.generateCaptcha();
      this.registerForm.get('captcha')?.reset();
      return;
    }

    this.loading = true;
    this.error = '';
    this.successMessage = '';

    const { captcha, ...requestData } = this.registerForm.value;

    this.authService.register(requestData).subscribe({
      next: () => {
        this.successMessage = 'Access granted. Initializing environment...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: err => {
        this.error = err.error?.error || 'Registration failed.';
        this.loading = false;
        this.generateCaptcha();
      }
    });
  }
}