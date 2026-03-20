import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISecRole } from '../sec-role.model';
import { SecRoleService } from '../service/sec-role.service';

@Component({
  templateUrl: './sec-role-delete-dialog.component.html',
  imports: [SharedModule],
})
export class SecRoleDeleteDialogComponent {
  @Input() secRole?: ISecRole;

  protected secRoleService = inject(SecRoleService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.secRoleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
