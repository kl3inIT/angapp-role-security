import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISecFetchPlan } from '../sec-fetch-plan.model';
import { SecFetchPlanService } from '../service/sec-fetch-plan.service';

@Component({
  templateUrl: './sec-fetch-plan-delete-dialog.component.html',
  imports: [SharedModule],
})
export class SecFetchPlanDeleteDialogComponent {
  @Input() secFetchPlan?: ISecFetchPlan;

  protected planService = inject(SecFetchPlanService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.planService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
