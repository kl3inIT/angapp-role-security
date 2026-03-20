import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IOrganization } from '../organization.model';
import { OrganizationService } from '../service/organization.service';

@Component({
  templateUrl: './organization-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class OrganizationDeleteDialogComponent {
  organization?: IOrganization;

  protected organizationService = inject(OrganizationService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.organizationService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
