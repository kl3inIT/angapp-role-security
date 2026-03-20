import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISecRowPolicy, NewSecRowPolicy } from '../sec-row-policy.model';
import { SecRowPolicyService } from '../service/sec-row-policy.service';

const secRowPolicyResolve = (route: ActivatedRouteSnapshot): Observable<ISecRowPolicy | NewSecRowPolicy | never> => {
  const id = route.params['id'];
  if (id) {
    const service = inject(SecRowPolicyService);
    const router = inject(Router);
    return service.find(id).pipe(
      mergeMap((res: HttpResponse<ISecRowPolicy>) => {
        if (res.body) {
          return of(res.body);
        }
        router.navigate(['404']);
        return EMPTY;
      }),
    );
  }
  return of({ id: null } as NewSecRowPolicy);
};

export default secRowPolicyResolve;
