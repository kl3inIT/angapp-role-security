import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISecPermission, SecPermissionEffect, TargetType } from '../sec-permission.model';
import { SecPermissionService } from '../service/sec-permission.service';
import { SecPermissionFormGroup, SecPermissionFormService } from './sec-permission-form.service';

@Component({
  selector: 'jhi-sec-permission-update',
  templateUrl: './sec-permission-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SecPermissionUpdateComponent implements OnInit {
  isSaving = false;
  secPermission: ISecPermission | null = null;

  targetTypes: TargetType[] = ['ENTITY', 'ATTRIBUTE', 'ROW_POLICY', 'FETCH_PLAN'];
  effects: SecPermissionEffect[] = ['ALLOW', 'DENY'];

  protected secPermissionService = inject(SecPermissionService);
  protected secPermissionFormService = inject(SecPermissionFormService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: SecPermissionFormGroup = this.secPermissionFormService.createSecPermissionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ secPermission }) => {
      this.secPermission = secPermission;
      if (secPermission) {
        this.updateForm(secPermission);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const secPermission = this.secPermissionFormService.getSecPermission(this.editForm);
    if (secPermission.id !== null) {
      this.subscribeToSaveResponse(this.secPermissionService.update(secPermission));
    } else {
      this.subscribeToSaveResponse(this.secPermissionService.create(secPermission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecPermission>>): void {
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

  protected updateForm(secPermission: ISecPermission): void {
    this.secPermission = secPermission;
    this.secPermissionFormService.resetForm(this.editForm, secPermission);
  }
}
