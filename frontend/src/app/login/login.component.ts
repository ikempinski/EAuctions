import { Component, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../_services/user.service';
import { Router } from '@angular/router';
import { AuthService } from '../_services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  email = '';
  password = '';
  okMessage = '';
  emailError = '';
  passwordError = '';
  errorMessage = '';
  constructor(
    private route: ActivatedRoute, 
    private router: Router, 
    private UserService: UserService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.route.queryParams.subscribe(params => {
      if (params['registered'] === 'true') {
        this.okMessage = 'Rejestracja zakończona. Możesz się teraz zalogować.';
        this.email = params['email'];
      }
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.okMessage = '';
    if (this.validateForm()) {
      this.UserService.login(this.email, this.password).subscribe({
        next: () => {
          this.authService.setLoggedIn(true);
          this.router.navigate(['/account']);
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.errorMessage = err?.status === 401
            ? 'Nieprawidłowy email lub hasło'
            : 'Nie udało się zalogować. Spróbuj ponownie później.';
          this.cdr.detectChanges();
        },
      });
    }
  }

  validateForm(): boolean {
    var isOk = true;
    if (!this.email.includes('@')) {
      isOk = false;
      this.emailError = 'Email jest nieprawidłowy';
    } else {
      this.emailError = '';
    }

    if (this.password.length < 6) {
      isOk = false;
      this.passwordError = 'Hasło musi mieć co najmniej 6 znaków';
    } else {
      this.passwordError = '';
    }
    return isOk;
  }
}
