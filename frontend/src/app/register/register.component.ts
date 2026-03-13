import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from '../_services/user.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  email = '';
  username = '';
  password = '';
  confirmPassword = '';
  usernameError = '';
  passwordError = '';
  confirmPasswordError = '';
  emailError = '';
  errorMessage = '';
  
  constructor(private userService: UserService, private router: Router) {}

  onSubmit(): void {
    if (this.validateForm()) {
        this.userService.register(this.email, this.username, this.password).subscribe({
            next: (response) => {
                if (response.id) {
                    this.router.navigate(['/login'], { queryParams: { registered: 'true', email: this.email } });
                } else {
                    this.errorMessage = 'Nie udało się zarejestrować użytkownika';
                }
            },
            error: (error) => {
                this.errorMessage = 'Nie udało się zarejestrować użytkownika';
            }
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
    if (this.username.length < 3) {
      isOk = false;
      this.usernameError = 'Nazwa użytkownika musi mieć co najmniej 3 znaki';
    } else {
      this.usernameError = '';
    }
    if (this.password.length < 6) {
      isOk = false;
      this.passwordError = 'Hasło musi mieć co najmniej 6 znaków';
    } else {
      this.passwordError = '';
    }
    if (this.confirmPassword !== this.password) {
      isOk = false;
      this.confirmPasswordError = 'Hasła nie pasują do siebie';
    } else {
      this.confirmPasswordError = '';
    }
    return isOk;
  }
  
}
