import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISecRole, NewSecRole, SecRoleType } from '../sec-role.model';

type SecRoleFormGroupInput = ISecRole | (Partial<Omit<NewSecRole, 'id'>> & { id: NewSecRole['id'] });

type SecRoleFormDefaults = Pick<NewSecRole, 'id' | 'type'>;

type SecRoleFormGroupContent = {
  id: FormControl<ISecRole['id'] | NewSecRole['id']>;
  code: FormControl<ISecRole['code']>;
  name: FormControl<ISecRole['name']>;
  type: FormControl<ISecRole['type']>;
};

export type SecRoleFormGroup = FormGroup<SecRoleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SecRoleFormService {
  createSecRoleFormGroup(secRole: SecRoleFormGroupInput = { id: null }): SecRoleFormGroup {
    const secRoleRawValue = {
      ...this.getFormDefaults(),
      ...secRole,
    };
    return new FormGroup<SecRoleFormGroupContent>({
      id: new FormControl(
        { value: secRoleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(secRoleRawValue.code, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      name: new FormControl(secRoleRawValue.name, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      type: new FormControl(secRoleRawValue.type ?? SecRoleType.RESOURCE, {
        validators: [Validators.required],
      }),
    });
  }

  getSecRole(form: SecRoleFormGroup): ISecRole | NewSecRole {
    return form.getRawValue() as ISecRole | NewSecRole;
  }

  resetForm(form: SecRoleFormGroup, secRole: SecRoleFormGroupInput): void {
    const secRoleRawValue = { ...this.getFormDefaults(), ...secRole };
    form.reset({
      ...secRoleRawValue,
      id: { value: secRoleRawValue.id, disabled: true },
    } as any);
  }

  private getFormDefaults(): SecRoleFormDefaults {
    return {
      id: null,
      type: SecRoleType.RESOURCE,
    };
  }
}
