import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISecRowPolicy } from '../sec-row-policy.model';
import { SecRowPolicyService } from '../service/sec-row-policy.service';

@Component({
  templateUrl: './sec-row-policy-delete-dialog.component.html',
  imports: [SharedModule],
})
export class SecRowPolicyDeleteDialogComponent {
  @Input() secRowPolicy?: ISecRowPolicy;

  protected policyService = inject(SecRowPolicyService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.policyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
