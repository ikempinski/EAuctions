import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check current backend session
  await authService.checkSession();

  const isLoggedIn = authService.isLoggedIn$.value;

  if (!isLoggedIn) {
    await router.navigate(['/login']);
    return false;
  }

  return true;
};

