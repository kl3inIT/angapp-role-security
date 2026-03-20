import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import OrganizationResolve from './route/organization-routing-resolve.service';

const organizationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/organization.component').then(m => m.OrganizationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/organization-detail.component').then(m => m.OrganizationDetailComponent),
    resolve: {
      organization: OrganizationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/organization-update.component').then(m => m.OrganizationUpdateComponent),
    resolve: {
      organization: OrganizationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/organization-update.component').then(m => m.OrganizationUpdateComponent),
    resolve: {
      organization: OrganizationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default organizationRoute;
