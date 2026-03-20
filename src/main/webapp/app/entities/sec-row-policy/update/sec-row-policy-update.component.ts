import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EntityOp, ISecRowPolicy, SecRowPolicyType } from '../sec-row-policy.model';
import { SecRowPolicyService } from '../service/sec-row-policy.service';
import { SecRowPolicyFormGroup, SecRowPolicyFormService } from './sec-row-policy-form.service';

@Component({
  selector: 'jhi-sec-row-policy-update',
  templateUrl: './sec-row-policy-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SecRowPolicyUpdateComponent implements OnInit {
  isSaving = false;
  secRowPolicy: ISecRowPolicy | null = null;

  operations: EntityOp[] = ['READ', 'CREATE', 'UPDATE', 'DELETE'];
  policyTypes: SecRowPolicyType[] = ['SPECIFICATION', 'JPQL', 'JAVA'];

  protected policyService = inject(SecRowPolicyService);
  protected formService = inject(SecRowPolicyFormService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: SecRowPolicyFormGroup = this.formService.createSecRowPolicyFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ secRowPolicy }) => {
      this.secRowPolicy = secRowPolicy;
      if (secRowPolicy) {
        this.updateForm(secRowPolicy);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const policy = this.formService.getSecRowPolicy(this.editForm);
    if (policy.id !== null) {
      this.subscribeToSaveResponse(this.policyService.update(policy));
    } else {
      this.subscribeToSaveResponse(this.policyService.create(policy));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecRowPolicy>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {}

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(policy: ISecRowPolicy): void {
    this.secRowPolicy = policy;
    this.formService.resetForm(this.editForm, policy);
  }
}
