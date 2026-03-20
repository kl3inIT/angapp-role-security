import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SecPermissionResolve from './route/sec-permission-routing-resolve.service';

const secPermissionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sec-permission.component').then(m => m.SecPermissionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sec-permission-update.component').then(m => m.SecPermissionUpdateComponent),
    resolve: {
      secPermission: SecPermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sec-permission-update.component').then(m => m.SecPermissionUpdateComponent),
    resolve: {
      secPermission: SecPermissionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default secPermissionRoute;
