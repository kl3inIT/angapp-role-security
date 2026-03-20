import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SecFetchPlanResolve from './route/sec-fetch-plan-routing-resolve.service';

const secFetchPlanRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sec-fetch-plan.component').then(m => m.SecFetchPlanComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sec-fetch-plan-update.component').then(m => m.SecFetchPlanUpdateComponent),
    resolve: {
      secFetchPlan: SecFetchPlanResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sec-fetch-plan-update.component').then(m => m.SecFetchPlanUpdateComponent),
    resolve: {
      secFetchPlan: SecFetchPlanResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default secFetchPlanRoute;
