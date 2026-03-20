import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'angappApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'department',
    data: { pageTitle: 'angappApp.department.home.title' },
    loadChildren: () => import('./department/department.routes'),
  },
  {
    path: 'organization',
    data: { pageTitle: 'angappApp.organization.home.title' },
    loadChildren: () => import('./organization/organization.routes'),
  },
  {
    path: 'sec-role',
    data: { pageTitle: 'Security roles' },
    loadChildren: () => import('./sec-role/sec-role.routes'),
  },
  {
    path: 'sec-permission',
    data: { pageTitle: 'Security permissions' },
    loadChildren: () => import('./sec-permission/sec-permission.routes'),
  },
  {
    path: 'sec-row-policy',
    data: { pageTitle: 'Row policies' },
    loadChildren: () => import('./sec-row-policy/sec-row-policy.routes'),
  },
  {
    path: 'sec-fetch-plan',
    data: { pageTitle: 'Fetch plans' },
    loadChildren: () => import('./sec-fetch-plan/sec-fetch-plan.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
