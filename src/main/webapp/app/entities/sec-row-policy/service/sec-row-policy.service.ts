import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISecRowPolicy, NewSecRowPolicy } from '../sec-row-policy.model';

export type PartialUpdateSecRowPolicy = Partial<ISecRowPolicy> & Pick<ISecRowPolicy, 'id'>;

export type EntityResponseType = HttpResponse<ISecRowPolicy>;
export type EntityArrayResponseType = HttpResponse<ISecRowPolicy[]>;

@Injectable({ providedIn: 'root' })
export class SecRowPolicyService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sec-row-policies');

  create(policy: NewSecRowPolicy): Observable<EntityResponseType> {
    return this.http.post<ISecRowPolicy>(this.resourceUrl, policy, { observe: 'response' });
  }

  update(policy: ISecRowPolicy): Observable<EntityResponseType> {
    return this.http.put<ISecRowPolicy>(`${this.resourceUrl}/${this.getIdentifier(policy)}`, policy, { observe: 'response' });
  }

  partialUpdate(policy: PartialUpdateSecRowPolicy): Observable<EntityResponseType> {
    return this.http.patch<ISecRowPolicy>(`${this.resourceUrl}/${this.getIdentifier(policy)}`, policy, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISecRowPolicy>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISecRowPolicy[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getIdentifier(policy: Pick<ISecRowPolicy, 'id'>): number {
    return policy.id;
  }
}
