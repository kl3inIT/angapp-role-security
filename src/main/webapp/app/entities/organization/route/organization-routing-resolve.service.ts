import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrganization } from '../organization.model';
import { OrganizationService } from '../service/organization.service';

const organizationResolve = (route: ActivatedRouteSnapshot): Observable<null | IOrganization> => {
  const id = route.params.id;
  if (id) {
    return inject(OrganizationService)
      .find(id)
      .pipe(
        mergeMap((organization: HttpResponse<IOrganization>) => {
          if (organization.body) {
            return of(organization.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default organizationResolve;
