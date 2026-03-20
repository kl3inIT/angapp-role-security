import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISecPermission, NewSecPermission } from '../sec-permission.model';
import { SecPermissionService } from '../service/sec-permission.service';

const secPermissionResolve = (route: ActivatedRouteSnapshot): Observable<ISecPermission | NewSecPermission | never> => {
  const id = route.params['id'];
  if (id) {
    const service = inject(SecPermissionService);
    const router = inject(Router);
    return service.find(id).pipe(
      mergeMap((res: HttpResponse<ISecPermission>) => {
        if (res.body) {
          return of(res.body);
        }
        router.navigate(['404']);
        return EMPTY;
      }),
    );
  }
  return of({ id: null } as NewSecPermission);
};

export default secPermissionResolve;
