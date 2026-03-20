import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IOrganization } from '../organization.model';

@Component({
  selector: 'jhi-organization-detail',
  templateUrl: './organization-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class OrganizationDetailComponent {
  organization = input<IOrganization | null>(null);

  previousState(): void {
    window.history.back();
  }
}
