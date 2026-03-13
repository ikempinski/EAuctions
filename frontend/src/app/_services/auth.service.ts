import { Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom } from 'rxjs';
import { UserService } from './user.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly isLoggedIn$ = new BehaviorSubject<boolean>(false);

  constructor(private userService: UserService) {}

  checkSession(): Promise<void> {
    return firstValueFrom(this.userService.getCurrentUser())
      .then(() => this.isLoggedIn$.next(true))
      .catch(() => this.isLoggedIn$.next(false));
  }

  setLoggedIn(loggedIn: boolean): void {
    this.isLoggedIn$.next(loggedIn);
  }

  logout(): void {
    this.userService.logout().subscribe({
      next: () => this.isLoggedIn$.next(false),
      error: () => this.isLoggedIn$.next(false),
    });
  }
}