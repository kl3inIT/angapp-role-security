import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISecRole, NewSecRole } from '../sec-role.model';
import { SecRoleService } from '../service/sec-role.service';

const secRoleResolve = (route: ActivatedRouteSnapshot): Observable<ISecRole | NewSecRole | never> => {
  const id = route.params['id'];
  if (id) {
    const service = inject(SecRoleService);
    const router = inject(Router);
    return service.find(id).pipe(
      mergeMap((secRole: HttpResponse<ISecRole>) => {
        if (secRole.body) {
          return of(secRole.body);
        }
        router.navigate(['404']);
        return EMPTY;
      }),
    );
  }
  return of({ id: null } as NewSecRole);
};

export default secRoleResolve;
