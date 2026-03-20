import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISecRole, SecRoleType } from '../sec-role.model';
import { SecRoleService } from '../service/sec-role.service';
import { SecRoleFormGroup, SecRoleFormService } from './sec-role-form.service';

@Component({
  selector: 'jhi-sec-role-update',
  templateUrl: './sec-role-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SecRoleUpdateComponent implements OnInit {
  isSaving = false;
  secRole: ISecRole | null = null;
  roleTypes = Object.values(SecRoleType);

  protected secRoleService = inject(SecRoleService);
  protected secRoleFormService = inject(SecRoleFormService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: SecRoleFormGroup = this.secRoleFormService.createSecRoleFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ secRole }) => {
      this.secRole = secRole;
      if (secRole) {
        this.updateForm(secRole);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const secRole = this.secRoleFormService.getSecRole(this.editForm);
    if (secRole.id !== null) {
      this.subscribeToSaveResponse(this.secRoleService.update(secRole));
    } else {
      this.subscribeToSaveResponse(this.secRoleService.create(secRole));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecRole>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(secRole: ISecRole): void {
    this.secRole = secRole;
    this.secRoleFormService.resetForm(this.editForm, secRole);
  }
}
