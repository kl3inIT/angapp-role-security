import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISecPermission } from '../sec-permission.model';
import { SecPermissionService } from '../service/sec-permission.service';

@Component({
  templateUrl: './sec-permission-delete-dialog.component.html',
  imports: [SharedModule],
})
export class SecPermissionDeleteDialogComponent {
  @Input() secPermission?: ISecPermission;

  protected secPermissionService = inject(SecPermissionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.secPermissionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
