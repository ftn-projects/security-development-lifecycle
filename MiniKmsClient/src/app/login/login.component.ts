import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { ApiService, LoginRequest } from '../api.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.setLoadingState(true);
      const credentials: LoginRequest = this.loginForm.value;
      
      this.apiService.login(credentials).subscribe({
        next: (response) => {
          this.authService.login(response.token);
          
          // Navigate based on user role
          const user = this.authService.getCurrentUser();
          if (user?.role === 'manager') {
            this.router.navigate(['/manage-keys']);
          } else {
            this.router.navigate(['/crypto']);
          }
          
          this.snackBar.open('Login successful!', 'Close', { duration: 3000 });
          this.setLoadingState(false);
        },
        error: (error) => {
          console.error('Login error:', error);
          this.snackBar.open('Invalid credentials', 'Close', { duration: 3000 });
          this.setLoadingState(false);
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private setLoadingState(loading: boolean): void {
    this.loading = loading;
    if (loading) {
      this.loginForm.disable();
    } else {
      this.loginForm.enable();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(field => {
      const control = this.loginForm.get(field);
      control?.markAsTouched({ onlySelf: true });
    });
  }

  getErrorMessage(field: string): string {
    const control = this.loginForm.get(field);
    if (control?.hasError('required')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
    }
    if (control?.hasError('minlength')) {
      return `${field.charAt(0).toUpperCase() + field.slice(1)} must be at least 3 characters`;
    }
    return '';
  }
}
