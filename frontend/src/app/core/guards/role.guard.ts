import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, UserRole } from '../services/auth/service';

export const roleGuard = (allowedRoles: UserRole[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const session = auth.getSession();

    if (!session) {
      return router.createUrlTree(['/login']);
    }

    if (auth.hasRole(...allowedRoles)) {
      return true;
    }

    return router.createUrlTree([session.homeRoute]);
  };
};
