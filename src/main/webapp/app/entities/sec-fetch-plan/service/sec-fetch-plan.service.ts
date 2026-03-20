import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISecFetchPlan, NewSecFetchPlan } from '../sec-fetch-plan.model';

export type PartialUpdateSecFetchPlan = Partial<ISecFetchPlan> & Pick<ISecFetchPlan, 'id'>;

export type EntityResponseType = HttpResponse<ISecFetchPlan>;
export type EntityArrayResponseType = HttpResponse<ISecFetchPlan[]>;

@Injectable({ providedIn: 'root' })
export class SecFetchPlanService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sec-fetch-plans');

  create(plan: NewSecFetchPlan): Observable<EntityResponseType> {
    return this.http.post<ISecFetchPlan>(this.resourceUrl, plan, { observe: 'response' });
  }

  update(plan: ISecFetchPlan): Observable<EntityResponseType> {
    return this.http.put<ISecFetchPlan>(`${this.resourceUrl}/${this.getIdentifier(plan)}`, plan, { observe: 'response' });
  }

  partialUpdate(plan: PartialUpdateSecFetchPlan): Observable<EntityResponseType> {
    return this.http.patch<ISecFetchPlan>(`${this.resourceUrl}/${this.getIdentifier(plan)}`, plan, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISecFetchPlan>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISecFetchPlan[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getIdentifier(plan: Pick<ISecFetchPlan, 'id'>): number {
    return plan.id;
  }
}
