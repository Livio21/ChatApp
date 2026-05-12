import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth-service';

/** Sends authenticated users away from sign-in / sign-up. */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.getStoredToken()) {
    return router.createUrlTree(['/home']);
  }
  return true;
};
