import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISecFetchPlan } from '../sec-fetch-plan.model';
import { SecFetchPlanService } from '../service/sec-fetch-plan.service';
import { SecFetchPlanFormGroup, SecFetchPlanFormService } from './sec-fetch-plan-form.service';

@Component({
  selector: 'jhi-sec-fetch-plan-update',
  templateUrl: './sec-fetch-plan-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SecFetchPlanUpdateComponent implements OnInit {
  isSaving = false;
  secFetchPlan: ISecFetchPlan | null = null;

  protected planService = inject(SecFetchPlanService);
  protected formService = inject(SecFetchPlanFormService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: SecFetchPlanFormGroup = this.formService.createSecFetchPlanFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ secFetchPlan }) => {
      this.secFetchPlan = secFetchPlan;
      if (secFetchPlan) {
        this.updateForm(secFetchPlan);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  formatJson(): void {
    const value = this.editForm.controls.definitionJson.value;
    if (!value) {
      return;
    }
    try {
      const parsed = JSON.parse(value);
      this.editForm.controls.definitionJson.setValue(JSON.stringify(parsed, null, 2));
    } catch (e) {
      // keep as is on invalid JSON
    }
  }

  save(): void {
    this.isSaving = true;
    const plan = this.formService.getSecFetchPlan(this.editForm);
    if (plan.id !== null) {
      this.subscribeToSaveResponse(this.planService.update(plan));
    } else {
      this.subscribeToSaveResponse(this.planService.create(plan));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecFetchPlan>>): void {
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

  protected updateForm(plan: ISecFetchPlan): void {
    this.secFetchPlan = plan;
    this.formService.resetForm(this.editForm, plan);
  }
}
