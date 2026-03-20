import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISecPermission, NewSecPermission } from '../sec-permission.model';

export type PartialUpdateSecPermission = Partial<ISecPermission> & Pick<ISecPermission, 'id'>;

export type EntityResponseType = HttpResponse<ISecPermission>;
export type EntityArrayResponseType = HttpResponse<ISecPermission[]>;

@Injectable({ providedIn: 'root' })
export class SecPermissionService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sec-permissions');

  create(secPermission: NewSecPermission): Observable<EntityResponseType> {
    return this.http.post<ISecPermission>(this.resourceUrl, secPermission, { observe: 'response' });
  }

  update(secPermission: ISecPermission): Observable<EntityResponseType> {
    return this.http.put<ISecPermission>(`${this.resourceUrl}/${this.getSecPermissionIdentifier(secPermission)}`, secPermission, {
      observe: 'response',
    });
  }

  partialUpdate(secPermission: PartialUpdateSecPermission): Observable<EntityResponseType> {
    return this.http.patch<ISecPermission>(`${this.resourceUrl}/${this.getSecPermissionIdentifier(secPermission)}`, secPermission, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISecPermission>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISecPermission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSecPermissionIdentifier(secPermission: Pick<ISecPermission, 'id'>): number {
    return secPermission.id;
  }
}
