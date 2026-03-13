import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../_services/user.service';
import { User } from '../_models/user.model';

@Component({
  selector: 'app-account-details',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './account-details.component.html'
})
export class AccountDetailsComponent implements OnInit {
  okMessage = '';
  errorMessage = '';
  userEdit: Pick<User, 'email' | 'username'> = {
    email: '',
    username: '',
  };

  passwordForm = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  };

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.userEdit = {
          email: user.email,
          username: user.username,
        };
        this.cdr.detectChanges();
      },
      error: () => {
        this.userEdit = { email: '', username: '' };
        this.cdr.detectChanges();
      },
    });
  }

  saveProfile(): void {
    this.errorMessage = '';
    this.okMessage = '';
    this.userService.updateProfile(this.userEdit.email, this.userEdit.username).subscribe({
      next: () => {
        this.okMessage = 'Dane zostały zapisane.';
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Nie udało się zapisać danych.';
        this.cdr.detectChanges();
      },
    });
  }

  changePassword(): void {
    this.errorMessage = '';
    this.okMessage = '';

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.errorMessage = 'Nowe hasła nie są takie same.';
      return;
    }

    this.userService.changePassword(this.passwordForm.oldPassword, this.passwordForm.newPassword).subscribe({
      next: () => {
        this.okMessage = 'Hasło zostało zmienione.';
        this.passwordForm = { oldPassword: '', newPassword: '', confirmPassword: '' };
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Nie udało się zmienić hasła. Sprawdź stare hasło.';
        this.cdr.detectChanges();
      },
    });
  }
}
