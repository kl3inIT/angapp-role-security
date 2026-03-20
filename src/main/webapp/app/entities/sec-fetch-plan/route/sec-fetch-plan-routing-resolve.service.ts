import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISecFetchPlan, NewSecFetchPlan } from '../sec-fetch-plan.model';
import { SecFetchPlanService } from '../service/sec-fetch-plan.service';

const secFetchPlanResolve = (route: ActivatedRouteSnapshot): Observable<ISecFetchPlan | NewSecFetchPlan | never> => {
  const id = route.params['id'];
  if (id) {
    const service = inject(SecFetchPlanService);
    const router = inject(Router);
    return service.find(id).pipe(
      mergeMap((res: HttpResponse<ISecFetchPlan>) => {
        if (res.body) {
          return of(res.body);
        }
        router.navigate(['404']);
        return EMPTY;
      }),
    );
  }
  return of({ id: null } as NewSecFetchPlan);
};

export default secFetchPlanResolve;
