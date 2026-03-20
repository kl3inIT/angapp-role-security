import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SecRowPolicyResolve from './route/sec-row-policy-routing-resolve.service';

const secRowPolicyRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sec-row-policy.component').then(m => m.SecRowPolicyComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sec-row-policy-update.component').then(m => m.SecRowPolicyUpdateComponent),
    resolve: {
      secRowPolicy: SecRowPolicyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sec-row-policy-update.component').then(m => m.SecRowPolicyUpdateComponent),
    resolve: {
      secRowPolicy: SecRowPolicyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default secRowPolicyRoute;
