import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SecRoleResolve from './route/sec-role-routing-resolve.service';

const secRoleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sec-role.component').then(m => m.SecRoleComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sec-role-update.component').then(m => m.SecRoleUpdateComponent),
    resolve: {
      secRole: SecRoleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sec-role-update.component').then(m => m.SecRoleUpdateComponent),
    resolve: {
      secRole: SecRoleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default secRoleRoute;
