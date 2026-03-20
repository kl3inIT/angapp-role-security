import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISecPermission, NewSecPermission, SecPermissionEffect, TargetType } from '../sec-permission.model';

type SecPermissionFormGroupInput = ISecPermission | (Partial<Omit<NewSecPermission, 'id'>> & { id: NewSecPermission['id'] });

type SecPermissionFormDefaults = Pick<NewSecPermission, 'id'>;

type SecPermissionFormGroupContent = {
  id: FormControl<ISecPermission['id'] | NewSecPermission['id']>;
  roleCode: FormControl<ISecPermission['roleCode']>;
  targetType: FormControl<ISecPermission['targetType']>;
  target: FormControl<ISecPermission['target']>;
  action: FormControl<ISecPermission['action']>;
  effect: FormControl<ISecPermission['effect']>;
};

export type SecPermissionFormGroup = FormGroup<SecPermissionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SecPermissionFormService {
  createSecPermissionFormGroup(secPermission: SecPermissionFormGroupInput = { id: null }): SecPermissionFormGroup {
    const rawValue = {
      ...this.getFormDefaults(),
      ...secPermission,
    };
    return new FormGroup<SecPermissionFormGroupContent>({
      id: new FormControl(
        { value: rawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      roleCode: new FormControl(rawValue.roleCode, { validators: [Validators.required] }),
      targetType: new FormControl(rawValue.targetType as TargetType, { validators: [Validators.required] }),
      target: new FormControl(rawValue.target, { validators: [Validators.required] }),
      action: new FormControl(rawValue.action, { validators: [Validators.required] }),
      effect: new FormControl(rawValue.effect as SecPermissionEffect, { validators: [Validators.required] }),
    });
  }

  getSecPermission(form: SecPermissionFormGroup): ISecPermission | NewSecPermission {
    return form.getRawValue() as ISecPermission | NewSecPermission;
  }

  resetForm(form: SecPermissionFormGroup, secPermission: SecPermissionFormGroupInput): void {
    const rawValue = { ...this.getFormDefaults(), ...secPermission };
    form.reset({
      ...rawValue,
      id: { value: rawValue.id, disabled: true },
    } as any);
  }

  private getFormDefaults(): SecPermissionFormDefaults {
    return {
      id: null,
    };
  }
}
