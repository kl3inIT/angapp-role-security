import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { EntityOp, ISecRowPolicy, NewSecRowPolicy, SecRowPolicyType } from '../sec-row-policy.model';

type SecRowPolicyFormGroupInput = ISecRowPolicy | (Partial<Omit<NewSecRowPolicy, 'id'>> & { id: NewSecRowPolicy['id'] });

type SecRowPolicyFormDefaults = Pick<NewSecRowPolicy, 'id'>;

type SecRowPolicyFormGroupContent = {
  id: FormControl<ISecRowPolicy['id'] | NewSecRowPolicy['id']>;
  code: FormControl<ISecRowPolicy['code']>;
  entityName: FormControl<ISecRowPolicy['entityName']>;
  operation: FormControl<ISecRowPolicy['operation']>;
  policyType: FormControl<ISecRowPolicy['policyType']>;
  expression: FormControl<ISecRowPolicy['expression']>;
};

export type SecRowPolicyFormGroup = FormGroup<SecRowPolicyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SecRowPolicyFormService {
  createSecRowPolicyFormGroup(policy: SecRowPolicyFormGroupInput = { id: null }): SecRowPolicyFormGroup {
    const rawValue = {
      ...this.getFormDefaults(),
      ...policy,
    };
    return new FormGroup<SecRowPolicyFormGroupContent>({
      id: new FormControl(
        { value: rawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(rawValue.code, { validators: [Validators.required, Validators.maxLength(100)] }),
      entityName: new FormControl(rawValue.entityName, { validators: [Validators.required, Validators.maxLength(255)] }),
      operation: new FormControl(rawValue.operation as EntityOp, { validators: [Validators.required] }),
      policyType: new FormControl(rawValue.policyType as SecRowPolicyType, { validators: [Validators.required] }),
      expression: new FormControl(rawValue.expression, { validators: [Validators.required, Validators.maxLength(1000)] }),
    });
  }

  getSecRowPolicy(form: SecRowPolicyFormGroup): ISecRowPolicy | NewSecRowPolicy {
    return form.getRawValue() as ISecRowPolicy | NewSecRowPolicy;
  }

  resetForm(form: SecRowPolicyFormGroup, policy: SecRowPolicyFormGroupInput): void {
    const rawValue = { ...this.getFormDefaults(), ...policy };
    form.reset({
      ...rawValue,
      id: { value: rawValue.id, disabled: true },
    } as any);
  }

  private getFormDefaults(): SecRowPolicyFormDefaults {
    return {
      id: null,
    };
  }
}
