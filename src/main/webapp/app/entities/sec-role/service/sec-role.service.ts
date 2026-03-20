import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISecRole, NewSecRole } from '../sec-role.model';

export type PartialUpdateSecRole = Partial<ISecRole> & Pick<ISecRole, 'id'>;

export type EntityResponseType = HttpResponse<ISecRole>;
export type EntityArrayResponseType = HttpResponse<ISecRole[]>;

@Injectable({ providedIn: 'root' })
export class SecRoleService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sec-roles');

  create(secRole: NewSecRole): Observable<EntityResponseType> {
    return this.http.post<ISecRole>(this.resourceUrl, secRole, { observe: 'response' });
  }

  update(secRole: ISecRole): Observable<EntityResponseType> {
    return this.http.put<ISecRole>(`${this.resourceUrl}/${this.getSecRoleIdentifier(secRole)}`, secRole, { observe: 'response' });
  }

  partialUpdate(secRole: PartialUpdateSecRole): Observable<EntityResponseType> {
    return this.http.patch<ISecRole>(`${this.resourceUrl}/${this.getSecRoleIdentifier(secRole)}`, secRole, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISecRole>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISecRole[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSecRoleIdentifier(secRole: Pick<ISecRole, 'id'>): number {
    return secRole.id;
  }
}
